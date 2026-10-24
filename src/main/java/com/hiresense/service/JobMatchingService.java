package com.hiresense.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hiresense.dto.JobFilterDto;
import com.hiresense.dto.JobMatchDto;
import com.hiresense.entity.*;
import com.hiresense.repository.JobMatchResultRepository;
import com.hiresense.repository.JobRepository;
import com.hiresense.repository.SavedJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobMatchingService {

    private final JobRepository jobRepository;
    private final SavedJobRepository savedJobRepository;
    private final JobMatchResultRepository matchResultRepository;
    private final AtsScoringService atsScoringService;

    public JobMatchingService(
            JobRepository jobRepository,
            SavedJobRepository savedJobRepository,
            JobMatchResultRepository matchResultRepository,
            AtsScoringService atsScoringService) {
        this.jobRepository = jobRepository;
        this.savedJobRepository = savedJobRepository;
        this.matchResultRepository = matchResultRepository;
        this.atsScoringService = atsScoringService;
    }

    @Transactional(readOnly = true)
    public List<Job> findJobs(JobFilterDto filter) {
        if (filter == null) {
            return jobRepository.findAllByOrderByCreatedAtDesc();
        }
        return jobRepository.searchJobs(
                filter.getKeyword(),
                filter.getLocation(),
                filter.getEmploymentType(),
                filter.getExperienceLevel()
        );
    }

    @Transactional(readOnly = true)
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Job opportunity not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public boolean isJobSaved(User user, Job job) {
        if (user == null || job == null) {
            return false;
        }
        return savedJobRepository.existsByUserAndJob(user, job);
    }

    public boolean toggleJobSave(User user, Long jobId) {
        Job job = getJobById(jobId);
        Optional<SavedJob> existing = savedJobRepository.findByUserAndJob(user, job);
        if (existing.isPresent()) {
            savedJobRepository.delete(existing.get());
            return false; // un-saved
        } else {
            savedJobRepository.save(new SavedJob(user, job));
            return true; // saved
        }
    }

    @Transactional(readOnly = true)
    public List<SavedJob> getSavedJobsForUser(User user) {
        return savedJobRepository.findByUserOrderBySavedAtDesc(user);
    }

    public JobMatchDto matchResumeToJob(ResumeAnalysis analysis, Job job, User user) {
        List<String> resumeSkills = atsScoringService.fromJson(
                analysis.getExtractedSkills(),
                new TypeReference<List<String>>() {},
                Collections.emptyList()
        );

        Set<String> normalizedCandidateSkills = resumeSkills.stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        List<String> reqSkills = parseSkillsList(job.getRequiredSkills());
        List<String> prefSkills = parseSkillsList(job.getPreferredSkills());

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        int matchedReqCount = 0;
        for (String req : reqSkills) {
            if (containsNormalized(normalizedCandidateSkills, req.toLowerCase())) {
                matchedSkills.add(req);
                matchedReqCount++;
            } else {
                missingSkills.add(req);
            }
        }

        int matchedPrefCount = 0;
        for (String pref : prefSkills) {
            if (containsNormalized(normalizedCandidateSkills, pref.toLowerCase())) {
                matchedSkills.add(pref + " (Preferred)");
                matchedPrefCount++;
            } else {
                missingSkills.add(pref);
            }
        }

        // Weighted match score calculation
        double reqWeight = 0.80;
        double prefWeight = 0.20;

        double reqRatio = reqSkills.isEmpty() ? 1.0 : ((double) matchedReqCount / reqSkills.size());
        double prefRatio = prefSkills.isEmpty() ? reqRatio : ((double) matchedPrefCount / prefSkills.size());

        int calculatedScore = (int) Math.round((reqRatio * reqWeight + prefRatio * prefWeight) * 100);
        calculatedScore = Math.max(15, Math.min(100, calculatedScore));

        List<String> recommendations = new ArrayList<>();
        if (!missingSkills.isEmpty()) {
            recommendations.add("Consider emphasizing or upskilling in high-priority requirements: " +
                    String.join(", ", missingSkills.subList(0, Math.min(3, missingSkills.size()))));
        }
        if (calculatedScore >= 80) {
            recommendations.add("High suitability! Tailor your resume summary specifically to " + job.getCompany() + " and apply promptly.");
        } else if (calculatedScore >= 60) {
            recommendations.add("Good foundational overlap. Highlight project achievements demonstrating proficiency in " +
                    (missingSkills.isEmpty() ? "related tools" : missingSkills.get(0)) + ".");
        } else {
            recommendations.add("Explore foundational certifications and practical GitHub projects covering: " +
                    String.join(", ", missingSkills.subList(0, Math.min(4, missingSkills.size()))) + ".");
        }

        boolean isSaved = (user != null) && isJobSaved(user, job);

        // Persist or update Match Result in database
        saveMatchResultRecord(analysis, job, calculatedScore, matchedSkills, missingSkills, recommendations);

        return new JobMatchDto(job, calculatedScore, matchedSkills, missingSkills, recommendations, isSaved);
    }

    public List<JobMatchDto> matchAllJobs(ResumeAnalysis analysis, User user) {
        List<Job> allJobs = jobRepository.findAllByOrderByCreatedAtDesc();
        List<JobMatchDto> results = new ArrayList<>();
        for (Job job : allJobs) {
            results.add(matchResumeToJob(analysis, job, user));
        }
        results.sort((a, b) -> Integer.compare(b.getMatchPercentage(), a.getMatchPercentage()));
        return results;
    }

    private void saveMatchResultRecord(
            ResumeAnalysis analysis,
            Job job,
            int score,
            List<String> matched,
            List<String> missing,
            List<String> recommendations) {
        JobMatchResult result = matchResultRepository.findByResumeAnalysisAndJob(analysis, job)
                .orElse(new JobMatchResult());

        result.setResumeAnalysis(analysis);
        result.setJob(job);
        result.setMatchPercentage(score);
        result.setMatchedSkills(atsScoringService.toJson(matched));
        result.setMissingSkills(atsScoringService.toJson(missing));
        result.setRecommendations(atsScoringService.toJson(recommendations));

        matchResultRepository.save(result);
    }

    private List<String> parseSkillsList(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(raw.split("[,;]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private boolean containsNormalized(Set<String> candidateSkills, String target) {
        if (candidateSkills.contains(target)) {
            return true;
        }
        for (String skill : candidateSkills) {
            if (skill.contains(target) || target.contains(skill)) {
                return true;
            }
        }
        return false;
    }
}
