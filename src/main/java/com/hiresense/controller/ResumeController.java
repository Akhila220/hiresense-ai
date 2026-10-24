package com.hiresense.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hiresense.dto.AtsScoreBreakdownDto;
import com.hiresense.entity.*;
import com.hiresense.repository.JobMatchResultRepository;
import com.hiresense.repository.ResumeAnalysisRepository;
import com.hiresense.repository.ResumeRepository;
import com.hiresense.service.AtsScoringService;
import com.hiresense.service.GeminiAiService;
import com.hiresense.service.JobMatchingService;
import com.hiresense.service.ResumeParserService;
import com.hiresense.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/resumes")
public class ResumeController {

    private final UserService userService;
    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository analysisRepository;
    private final JobMatchResultRepository matchResultRepository;
    private final ResumeParserService resumeParserService;
    private final AtsScoringService atsScoringService;
    private final GeminiAiService geminiAiService;
    private final JobMatchingService jobMatchingService;

    public ResumeController(
            UserService userService,
            ResumeRepository resumeRepository,
            ResumeAnalysisRepository analysisRepository,
            JobMatchResultRepository matchResultRepository,
            ResumeParserService resumeParserService,
            AtsScoringService atsScoringService,
            GeminiAiService geminiAiService,
            JobMatchingService jobMatchingService) {
        this.userService = userService;
        this.resumeRepository = resumeRepository;
        this.analysisRepository = analysisRepository;
        this.matchResultRepository = matchResultRepository;
        this.resumeParserService = resumeParserService;
        this.atsScoringService = atsScoringService;
        this.geminiAiService = geminiAiService;
        this.jobMatchingService = jobMatchingService;
    }

    @GetMapping
    public String listResumes(Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        List<Resume> resumes = resumeRepository.findByUserOrderByUploadedAtDesc(user);
        model.addAttribute("resumes", resumes);
        model.addAttribute("currentUser", user);
        return "resume/list";
    }

    @GetMapping("/upload")
    public String uploadPage(Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        model.addAttribute("currentUser", user);
        model.addAttribute("isAiConfigured", geminiAiService.isConfigured());
        return "resume/upload";
    }

    @PostMapping("/upload")
    public String handleResumeUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "enableAi", defaultValue = "false") boolean enableAi,
            RedirectAttributes redirectAttributes) {

        User user = userService.getCurrentAuthenticatedUser();

        try {
            // 1. Text Extraction
            String extractedText = resumeParserService.extractText(file);
            String savedFileName = resumeParserService.saveFileLocally(file);

            // 2. ATS Analysis
            AtsScoreBreakdownDto analysisDto = atsScoringService.analyze(extractedText);

            // 3. Optional Gemini AI Enhancement
            if (enableAi && geminiAiService.isConfigured()) {
                analysisDto = geminiAiService.enhanceAnalysisWithFallback(extractedText, analysisDto);
            }

            // 4. Persist Resume
            Resume resume = new Resume();
            resume.setUser(user);
            resume.setOriginalFileName(file.getOriginalFilename());
            resume.setFileName(savedFileName);
            resume.setFileType(file.getContentType() != null ? file.getContentType() : "application/pdf");
            resume.setFileSize(file.getSize());
            resume.setExtractedText(extractedText);
            resume = resumeRepository.save(resume);

            // 5. Persist Analysis
            ResumeAnalysis analysis = new ResumeAnalysis();
            analysis.setResume(resume);
            analysis.setOverallAtsScore(analysisDto.getOverallScore());
            analysis.setSectionScore(analysisDto.getSectionScore());
            analysis.setSkillsScore(analysisDto.getSkillsScore());
            analysis.setImpactScore(analysisDto.getImpactScore());
            analysis.setFormattingScore(analysisDto.getFormattingScore());
            analysis.setDetectedSections(atsScoringService.toJson(analysisDto.getDetectedSections()));
            analysis.setExtractedSkills(atsScoringService.toJson(analysisDto.getExtractedSkills()));
            analysis.setStrengths(atsScoringService.toJson(analysisDto.getStrengths()));
            analysis.setWeaknesses(atsScoringService.toJson(analysisDto.getWeaknesses()));
            analysis.setSuggestions(atsScoringService.toJson(analysisDto.getSuggestions()));
            analysis.setIsAiEnhanced(analysisDto.isAiEnhanced());
            analysis = analysisRepository.save(analysis);

            // 6. Automatically compute Job Matches
            jobMatchingService.matchAllJobs(analysis, user);

            redirectAttributes.addFlashAttribute("successMessage", "Resume parsed and analyzed successfully!");
            return "redirect:/resumes/" + resume.getId();

        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/resumes/upload";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing resume: " + ex.getMessage());
            return "redirect:/resumes/upload";
        }
    }

    @GetMapping("/{id}")
    public String viewResumeAnalysis(@PathVariable("id") Long id, Model model) {
        User user = userService.getCurrentAuthenticatedUser();

        // Strict User Data Isolation Check
        Resume resume = resumeRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found or access denied"));

        Optional<ResumeAnalysis> analysisOpt = analysisRepository.findTopByResumeIdOrderByCreatedAtDesc(resume.getId());
        if (analysisOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Analysis not found for this resume");
        }

        ResumeAnalysis analysis = analysisOpt.get();

        AtsScoreBreakdownDto breakdown = new AtsScoreBreakdownDto();
        breakdown.setOverallScore(analysis.getOverallAtsScore());
        breakdown.setSectionScore(analysis.getSectionScore());
        breakdown.setSkillsScore(analysis.getSkillsScore());
        breakdown.setImpactScore(analysis.getImpactScore());
        breakdown.setFormattingScore(analysis.getFormattingScore());
        breakdown.setAiEnhanced(analysis.getIsAiEnhanced());

        breakdown.setDetectedSections(atsScoringService.fromJson(analysis.getDetectedSections(), new TypeReference<List<String>>() {}, Collections.emptyList()));
        breakdown.setExtractedSkills(atsScoringService.fromJson(analysis.getExtractedSkills(), new TypeReference<List<String>>() {}, Collections.emptyList()));
        breakdown.setStrengths(atsScoringService.fromJson(analysis.getStrengths(), new TypeReference<List<String>>() {}, Collections.emptyList()));
        breakdown.setWeaknesses(atsScoringService.fromJson(analysis.getWeaknesses(), new TypeReference<List<String>>() {}, Collections.emptyList()));
        breakdown.setSuggestions(atsScoringService.fromJson(analysis.getSuggestions(), new TypeReference<List<String>>() {}, Collections.emptyList()));

        List<JobMatchResult> matchResults = matchResultRepository.findByResumeAnalysisOrderByMatchPercentageDesc(analysis);

        model.addAttribute("resume", resume);
        model.addAttribute("analysis", analysis);
        model.addAttribute("breakdown", breakdown);
        model.addAttribute("matchResults", matchResults);
        model.addAttribute("currentUser", user);

        return "resume/view";
    }

    @PostMapping("/{id}/delete")
    public String deleteResume(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentAuthenticatedUser();

        // Isolation Check
        Resume resume = resumeRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found or access denied"));

        resumeRepository.delete(resume);
        redirectAttributes.addFlashAttribute("successMessage", "Resume deleted successfully.");
        return "redirect:/dashboard";
    }
}
