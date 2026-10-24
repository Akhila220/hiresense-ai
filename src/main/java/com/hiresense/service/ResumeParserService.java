package com.hiresense.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ResumeParserService {

    private final Path uploadStoragePath;

    public ResumeParserService(@Value("${hiresense.upload.dir:uploads}") String uploadDir) {
        this.uploadStoragePath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadStoragePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload storage directory at " + this.uploadStoragePath, e);
        }
    }

    public void validateResumeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a resume file to upload.");
        }

        // Max 10MB
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 10MB limit. Please upload a smaller resume.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Invalid file: filename is missing.");
        }

        String lowerName = originalFilename.toLowerCase();
        if (!lowerName.endsWith(".pdf") && !lowerName.endsWith(".docx")) {
            throw new IllegalArgumentException("Only PDF (.pdf) and Microsoft Word (.docx) resumes are supported.");
        }
    }

    public String extractText(MultipartFile file) throws IOException {
        validateResumeFile(file);

        String originalFilename = file.getOriginalFilename();
        String lowerName = originalFilename != null ? originalFilename.toLowerCase() : "";
        String extractedText = "";

        if (lowerName.endsWith(".pdf")) {
            extractedText = extractFromPdf(file.getBytes());
        } else if (lowerName.endsWith(".docx")) {
            try (InputStream is = file.getInputStream()) {
                extractedText = extractFromDocx(is);
            }
        }

        if (extractedText == null || extractedText.trim().length() < 30) {
            throw new IllegalArgumentException(
                "Unable to extract readable text from this file. " +
                "The document may be an image-only scan, flattened graphic, or password-protected. " +
                "Please upload a standard text-based PDF or Word document."
            );
        }

        return extractedText.trim();
    }

    public String saveFileLocally(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String safeName = (originalFilename != null) ? originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_") : "resume.pdf";
        String uniqueFileName = UUID.randomUUID().toString() + "_" + safeName;

        Path targetLocation = this.uploadStoragePath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }

    private String extractFromPdf(byte[] pdfBytes) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            if (document.isEncrypted()) {
                throw new IllegalArgumentException("The uploaded PDF is password protected. Please remove password encryption and retry.");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }

    private String extractFromDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return extractor.getText();
        }
    }
}
