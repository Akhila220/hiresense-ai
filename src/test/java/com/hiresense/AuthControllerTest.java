package com.hiresense;

import com.hiresense.entity.User;
import com.hiresense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should successfully register a new user and redirect to login")
    void testSuccessfulRegistration() throws Exception {
        mockMvc.perform(post("/register")
                .param("fullName", "Student Candidate")
                .param("email", "student@example.com")
                .param("password", "secret123")
                .param("confirmPassword", "secret123")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered=true"));

        assertTrue(userRepository.existsByEmail("student@example.com"));
    }

    @Test
    @DisplayName("Should reject registration when passwords do not match")
    void testPasswordMismatchRegistration() throws Exception {
        mockMvc.perform(post("/register")
                .param("fullName", "Student Candidate")
                .param("email", "mismatch@example.com")
                .param("password", "secret123")
                .param("confirmPassword", "different456")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("Should reject registration when email is already registered")
    void testDuplicateEmailRegistration() throws Exception {
        userRepository.save(new User("duplicate@example.com", "$2a$12$abcdef", "Existing", "ROLE_USER"));

        mockMvc.perform(post("/register")
                .param("fullName", "Another Candidate")
                .param("email", "duplicate@example.com")
                .param("password", "secret123")
                .param("confirmPassword", "secret123")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("Unauthenticated request to dashboard should redirect to login")
    void testProtectedDashboardRedirect() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
