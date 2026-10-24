package com.hiresense.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hiresense.dto.AtsScoreBreakdownDto;
import com.hiresense.dto.DashboardSummaryDto;
import com.hiresense.entity.*;
import com.hiresense.repository.*;
import com.hiresense.service.AtsScoringService;
import com.hiresense.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class DashboardController {

    private final UserService userService;
    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository analysisRepository;
    private final SavedJobRepository savedJobRepository;
    private final JobMatchResultRepository matchResultRepository;
    private final AtsScoringService atsScoringService;

    public DashboardController(
            UserService userService,
            ResumeRepository resumeRepository,
            ResumeAnalysisRepository analysisRepository,
            SavedJobRepository savedJobRepository,
            JobMatchResultRepository matchResultRepository,
            AtsScoringService atsScoringService) {
        this.userService = userService;
        this.resumeRepository = resumeRepository;
        this.analysisRepository = analysisRepository;
        this.savedJobRepository = savedJobRepository;
        this.matchResultRepository = matchResultRepository;
        this.atsScoringService = atsScoringService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User user = userService.getCurrentAuthenticatedUser();

        DashboardSummaryDto summary = new DashboardSummaryDto();
        summary.setTotalResumes(resumeRepository.countByUser(user));
        summary.setTotalSavedJobs(savedJobRepository.countByUser(user));
        summary.setTotalMatches(matchResultRepository.countByResumeAnalysisResumeUser(user));

        List<Resume> userResumes = resumeRepository.findByUserOrderByUploadedAtDesc(user);
        summary.setRecentResumes(userResumes);

        Optional<ResumeAnalysis> latestOpt = analysisRepository.findTopByResumeUserOrderByCreatedAtDesc(user);
        if (latestOpt.isPresent()) {
            ResumeAnalysis analysis = latestOpt.get();
            summary.setLatestAnalysis(analysis);
            summary.setLatestAtsScore(analysis.getOverallAtsScore());

            AtsScoreBreakdownDto breakdown = new AtsScoreBreakdownDto();
            breakdown.setOverallScore(analysis.getOverallAtsScore());
            breakdown.setSectionScore(analysis.getSectionScore());
            breakdown.setSkillsScore(analysis.getSkillsScore());
            breakdown.setImpactScore(analysis.getImpactScore());
            breakdown.setFormattingScore(analysis.getFormattingScore());
            breakdown.setAiEnhanced(analysis.getIsAiEnhanced());

            List<String> skills = atsScoringService.fromJson(
                    analysis.getExtractedSkills(),
                    new TypeReference<List<String>>() {},
                    Collections.emptyList()
            );
            breakdown.setExtractedSkills(skills);

            List<String> strengths = atsScoringService.fromJson(
                    analysis.getStrengths(),
                    new TypeReference<List<String>>() {},
                    Collections.emptyList()
            );
            breakdown.setStrengths(strengths);

            List<String> suggestions = atsScoringService.fromJson(
                    analysis.getSuggestions(),
                    new TypeReference<List<String>>() {},
                    Collections.emptyList()
            );
            breakdown.setSuggestions(suggestions);

            summary.setLatestBreakdown(breakdown);

            // Compute skill categories count for Chart.js
            Map<String, Integer> categoryCounts = new LinkedHashMap<>();
            categoryCounts.put("Languages", 0);
            categoryCounts.put("Frameworks", 0);
            categoryCounts.put("Databases", 0);
            categoryCounts.put("Cloud & DevOps", 0);
            categoryCounts.put("Tools", 0);

            for (String s : skills) {
                String lower = s.toLowerCase();
                if (lower.matches(".*(java|python|javascript|typescript|c\\+\\+|sql|html|css).*")) {
                    categoryCounts.put("Languages", categoryCounts.get("Languages") + 1);
                } else if (lower.matches(".*(spring|react|angular|hibernate|bootstrap|rest).*")) {
                    categoryCounts.put("Frameworks", categoryCounts.get("Frameworks") + 1);
                } else if (lower.matches(".*(postgres|mysql|mongo|redis|database).*")) {
                    categoryCounts.put("Databases", categoryCounts.get("Databases") + 1);
                } else if (lower.matches(".*(aws|docker|kubernetes|linux|cloud|ci/cd).*")) {
                    categoryCounts.put("Cloud & DevOps", categoryCounts.get("Cloud & DevOps") + 1);
                } else {
                    categoryCounts.put("Tools", categoryCounts.get("Tools") + 1);
                }
            }
            summary.setSkillCategoryCounts(categoryCounts);
        }

        List<SavedJob> savedJobs = savedJobRepository.findByUserOrderBySavedAtDesc(user);
        summary.setSavedJobs(savedJobs);

        List<JobMatchResult> recentMatches = matchResultRepository.findByResumeAnalysisResumeUserOrderByCreatedAtDesc(user);
        summary.setRecentMatches(recentMatches.stream().limit(6).toList());

        model.addAttribute("summary", summary);
        model.addAttribute("currentUser", user);

        return "dashboard/index";
    }
}
