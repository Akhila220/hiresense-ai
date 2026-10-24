package com.hiresense;

import com.hiresense.dto.AtsScoreBreakdownDto;
import com.hiresense.service.AtsScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AtsScoringServiceTest {

    private AtsScoringService scoringService;

    @BeforeEach
    void setUp() {
        scoringService = new AtsScoringService();
    }

    @Test
    @DisplayName("Should extract skills, detect sections, and calculate score for full resume text")
    void testAnalyzeFullResume() {
        String resumeText = """
                Jane Doe
                Email: jane.doe@example.com | Phone: 555-123-4567 | linkedin.com/in/janedoe
                
                Professional Summary:
                Dedicated Software Engineer with 3+ years of experience designing high-scale web systems.
                
                Technical Skills:
                Java, Spring Boot, PostgreSQL, Docker, Kubernetes, AWS, Git, REST API, Microservices
                
                Work Experience:
                Software Engineer - Tech Corp (2022 - Present)
                - Engineered backend microservices using Spring Boot and PostgreSQL, reducing latency by 35%.
                - Architected automated CI/CD pipelines deploying to Docker containers in Kubernetes.
                - Developed secure REST APIs handling 5,000+ daily requests.
                
                Education:
                Bachelor of Science in Computer Science, Tech University (2018 - 2022)
                
                Projects:
                - E-Commerce Microservices: Built using Java, Spring Boot, and PostgreSQL.
                """;

        AtsScoreBreakdownDto result = scoringService.analyze(resumeText);

        assertNotNull(result);
        assertTrue(result.getOverallScore() >= 70, "Score should be at least 70 for comprehensive resume");
        assertTrue(result.getDetectedSections().contains("Contact Information"));
        assertTrue(result.getDetectedSections().contains("Work Experience"));
        assertTrue(result.getDetectedSections().contains("Education"));
        assertTrue(result.getDetectedSections().contains("Skills"));
        assertTrue(result.getDetectedSections().contains("Projects"));

        assertTrue(result.getExtractedSkills().contains("Java"));
        assertTrue(result.getExtractedSkills().contains("Spring Boot"));
        assertTrue(result.getExtractedSkills().contains("PostgreSQL"));
        assertTrue(result.getExtractedSkills().contains("Docker"));

        assertFalse(result.getStrengths().isEmpty());
    }

    @Test
    @DisplayName("Should handle minimal resume gracefully with appropriate suggestions")
    void testAnalyzeMinimalResume() {
        String minimalText = "John Doe. Contact me at john@example.com. I know basic programming.";

        AtsScoreBreakdownDto result = scoringService.analyze(minimalText);

        assertNotNull(result);
        assertTrue(result.getOverallScore() <= 50, "Minimal resume should receive low ATS score");
        assertFalse(result.getWeaknesses().isEmpty(), "Should identify weaknesses for minimal resume");
        assertFalse(result.getSuggestions().isEmpty(), "Should provide actionable suggestions");
    }
}
