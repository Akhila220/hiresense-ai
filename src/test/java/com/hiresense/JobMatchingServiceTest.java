package com.hiresense;

import com.hiresense.dto.JobMatchDto;
import com.hiresense.entity.Job;
import com.hiresense.entity.ResumeAnalysis;
import com.hiresense.entity.User;
import com.hiresense.repository.JobMatchResultRepository;
import com.hiresense.repository.JobRepository;
import com.hiresense.repository.SavedJobRepository;
import com.hiresense.service.AtsScoringService;
import com.hiresense.service.JobMatchingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class JobMatchingServiceTest {

    private JobRepository jobRepository;
    private SavedJobRepository savedJobRepository;
    private JobMatchResultRepository matchResultRepository;
    private AtsScoringService atsScoringService;
    private JobMatchingService jobMatchingService;

    @BeforeEach
    void setUp() {
        jobRepository = Mockito.mock(JobRepository.class);
        savedJobRepository = Mockito.mock(SavedJobRepository.class);
        matchResultRepository = Mockito.mock(JobMatchResultRepository.class);
        atsScoringService = new AtsScoringService();
        jobMatchingService = new JobMatchingService(jobRepository, savedJobRepository, matchResultRepository, atsScoringService);
    }

    @Test
    @DisplayName("Should accurately calculate match score when candidate has matching skills")
    void testMatchResumeToJob() {
        User user = new User("candidate@example.com", "hash", "Candidate", "ROLE_USER");
        user.setId(1L);

        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setId(10L);
        analysis.setExtractedSkills("[\"Java\", \"Spring Boot\", \"PostgreSQL\", \"Docker\"]");

        Job job = new Job();
        job.setId(100L);
        job.setTitle("Java Backend Developer");
        job.setCompany("Acme Corp");
        job.setLocation("Remote");
        job.setEmploymentType("FULL_TIME");
        job.setExperienceLevel("ENTRY");
        job.setDescription("Sample role");
        job.setRequiredSkills("Java, Spring Boot, PostgreSQL");
        job.setPreferredSkills("Docker, Kubernetes");

        when(matchResultRepository.findByResumeAnalysisAndJob(any(), any())).thenReturn(Optional.empty());

        JobMatchDto match = jobMatchingService.matchResumeToJob(analysis, job, user);

        assertNotNull(match);
        assertTrue(match.getMatchPercentage() >= 80, "Expected high match percentage for strong overlap");
        assertTrue(match.getMatchedSkills().contains("Java"));
        assertTrue(match.getMatchedSkills().contains("Spring Boot"));
        assertTrue(match.getMatchedSkills().contains("PostgreSQL"));
        assertTrue(match.getMissingSkills().contains("Kubernetes"));
    }
}
