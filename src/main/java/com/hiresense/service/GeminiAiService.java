package com.hiresense.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiresense.dto.AtsScoreBreakdownDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiAiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiAiService.class);
    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GeminiAiService(@Value("${hiresense.gemini.api-key:}") String apiKey) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public boolean isConfigured() {
        return !apiKey.isEmpty() && 
               !apiKey.equalsIgnoreCase("your_gemini_api_key_here") && 
               !apiKey.equalsIgnoreCase("optional_gemini_api_key");
    }

    public AtsScoreBreakdownDto enhanceAnalysisWithFallback(String resumeText, AtsScoreBreakdownDto baseDto) {
        if (!isConfigured()) {
            logger.info("Gemini API key is not configured or empty. Using deterministic ATS analysis.");
            baseDto.setAiEnhanced(false);
            return baseDto;
        }

        try {
            String prompt = "You are an expert executive tech recruiter and ATS optimization specialist. " +
                    "Review this extracted resume text and provide 3 high-impact, professional bullet points " +
                    "suggesting improvements to make the candidate stand out. Keep each bullet point under 25 words.\n\n" +
                    "Resume text:\n" + (resumeText.length() > 2500 ? resumeText.substring(0, 2500) : resumeText);

            String requestBody = objectMapper.writeValueAsString(new GeminiPayload(prompt));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(12))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode candidateText = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");
                if (!candidateText.isMissingNode()) {
                    String aiText = candidateText.asText();
                    String[] lines = aiText.split("\n");
                    List<String> aiSuggestions = new ArrayList<>(baseDto.getSuggestions());
                    for (String line : lines) {
                        String clean = line.replaceFirst("^[*•\\-\\d.]+\\s*", "").trim();
                        if (!clean.isEmpty()) {
                            aiSuggestions.add("[AI Advisory] " + clean);
                        }
                    }
                    baseDto.setSuggestions(aiSuggestions);
                    baseDto.setAiEnhanced(true);
                    logger.info("Successfully enhanced resume analysis with Gemini AI.");
                    return baseDto;
                }
            } else {
                logger.warn("Gemini API returned HTTP status {}: {}. Falling back to deterministic results.",
                        response.statusCode(), response.body());
            }
        } catch (Exception e) {
            logger.warn("Could not reach Gemini API or parse response ({}). Gracefully falling back to deterministic analysis.", e.getMessage());
        }

        baseDto.setAiEnhanced(false);
        return baseDto;
    }

    private static class GeminiPayload {
        public List<Content> contents;

        public GeminiPayload(String prompt) {
            this.contents = List.of(new Content(List.of(new Part(prompt))));
        }

        public static class Content {
            public List<Part> parts;
            public Content(List<Part> parts) { this.parts = parts; }
        }

        public static class Part {
            public String text;
            public Part(String text) { this.text = text; }
        }
    }
}
