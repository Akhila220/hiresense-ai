package com.hiresense;

import com.hiresense.entity.Resume;
import com.hiresense.entity.ResumeAnalysis;
import com.hiresense.entity.User;
import com.hiresense.repository.ResumeAnalysisRepository;
import com.hiresense.repository.ResumeRepository;
import com.hiresense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserDataIsolationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ResumeAnalysisRepository analysisRepository;

    private Long userAResumeId;

    @BeforeEach
    void setUpData() {
        analysisRepository.deleteAll();
        resumeRepository.deleteAll();
        userRepository.deleteAll();

        // User A
        User userA = new User("alice@example.com", "$2a$12$abcdef", "Alice Applicant", "ROLE_USER");
        userA = userRepository.save(userA);

        // User B
        User userB = new User("bob@example.com", "$2a$12$abcdef", "Bob Bystander", "ROLE_USER");
        userRepository.save(userB);

        // Resume belonging to User A
        Resume resumeA = new Resume();
        resumeA.setUser(userA);
        resumeA.setFileName("uuid_alice.pdf");
        resumeA.setOriginalFileName("Alice_Private_Resume.pdf");
        resumeA.setFileType("application/pdf");
        resumeA.setFileSize(2048L);
        resumeA.setExtractedText("Alice Private Resume Experience Java Spring");
        resumeA = resumeRepository.save(resumeA);

        ResumeAnalysis analysisA = new ResumeAnalysis();
        analysisA.setResume(resumeA);
        analysisA.setOverallAtsScore(85);
        analysisA.setSectionScore(25);
        analysisA.setSkillsScore(30);
        analysisA.setImpactScore(20);
        analysisA.setFormattingScore(10);
        analysisA.setExtractedSkills("[\"Java\"]");
        analysisRepository.save(analysisA);

        userAResumeId = resumeA.getId();
    }

    @Test
    @WithMockUser(username = "bob@example.com")
    @DisplayName("User B must NOT be able to view User A's private resume analysis")
    void testUserBCannotAccessUserAResume() throws Exception {
        mockMvc.perform(get("/resumes/" + userAResumeId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "bob@example.com")
    @DisplayName("User B must NOT be able to delete User A's resume")
    void testUserBCannotDeleteUserAResume() throws Exception {
        mockMvc.perform(post("/resumes/" + userAResumeId + "/delete")
                .with(csrf()))
                .andExpect(status().isNotFound());

        // Verify record still exists in repository
        assertTrue(resumeRepository.findById(userAResumeId).isPresent(), "Resume A must not be deleted by User B");
    }

    @Test
    @WithMockUser(username = "alice@example.com")
    @DisplayName("User A can successfully view their own resume analysis")
    void testUserACanAccessOwnResume() throws Exception {
        mockMvc.perform(get("/resumes/" + userAResumeId))
                .andExpect(status().isOk());
    }
}
