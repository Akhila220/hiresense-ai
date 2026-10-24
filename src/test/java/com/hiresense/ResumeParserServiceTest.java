package com.hiresense;

import com.hiresense.service.ResumeParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class ResumeParserServiceTest {

    private ResumeParserService parserService;

    @BeforeEach
    void setUp() {
        parserService = new ResumeParserService("target/test-uploads");
    }

    @Test
    @DisplayName("Should accept valid PDF and DOCX file extensions")
    void testValidFileExtensions() {
        MockMultipartFile pdfFile = new MockMultipartFile("file", "resume.pdf", "application/pdf", "Valid dummy content".getBytes());
        MockMultipartFile docxFile = new MockMultipartFile("file", "resume.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Valid dummy content".getBytes());

        assertDoesNotThrow(() -> parserService.validateResumeFile(pdfFile));
        assertDoesNotThrow(() -> parserService.validateResumeFile(docxFile));
    }

    @Test
    @DisplayName("Should reject unsupported file extensions like .exe or .txt")
    void testInvalidFileExtensions() {
        MockMultipartFile exeFile = new MockMultipartFile("file", "malicious.exe", "application/octet-stream", "dummy".getBytes());
        MockMultipartFile txtFile = new MockMultipartFile("file", "resume.txt", "text/plain", "dummy".getBytes());

        assertThrows(IllegalArgumentException.class, () -> parserService.validateResumeFile(exeFile));
        assertThrows(IllegalArgumentException.class, () -> parserService.validateResumeFile(txtFile));
    }

    @Test
    @DisplayName("Should reject empty uploaded file")
    void testEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "resume.pdf", "application/pdf", new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> parserService.validateResumeFile(emptyFile));
    }
}
