package com.hiresense.controller;

import com.hiresense.dto.JobFilterDto;
import com.hiresense.dto.JobMatchDto;
import com.hiresense.entity.*;
import com.hiresense.repository.ResumeAnalysisRepository;
import com.hiresense.service.JobMatchingService;
import com.hiresense.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/jobs")
public class JobController {

    private final UserService userService;
    private final JobMatchingService jobMatchingService;
    private final ResumeAnalysisRepository analysisRepository;

    public JobController(
            UserService userService,
            JobMatchingService jobMatchingService,
            ResumeAnalysisRepository analysisRepository) {
        this.userService = userService;
        this.jobMatchingService = jobMatchingService;
        this.analysisRepository = analysisRepository;
    }

    @GetMapping
    public String listJobs(
            @ModelAttribute("filter") JobFilterDto filter,
            Model model) {

        User user = userService.getCurrentAuthenticatedUser();
        List<Job> jobs = jobMatchingService.findJobs(filter);

        Optional<ResumeAnalysis> latestAnalysis = analysisRepository.findTopByResumeUserOrderByCreatedAtDesc(user);

        model.addAttribute("jobs", jobs);
        model.addAttribute("filter", filter);
        model.addAttribute("hasResume", latestAnalysis.isPresent());
        model.addAttribute("currentUser", user);

        return "jobs/list";
    }

    @GetMapping("/{id}")
    public String viewJob(@PathVariable("id") Long id, Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        Job job = jobMatchingService.getJobById(id);

        boolean isSaved = jobMatchingService.isJobSaved(user, job);

        Optional<ResumeAnalysis> latestAnalysis = analysisRepository.findTopByResumeUserOrderByCreatedAtDesc(user);
        JobMatchDto matchDto = null;
        if (latestAnalysis.isPresent()) {
            matchDto = jobMatchingService.matchResumeToJob(latestAnalysis.get(), job, user);
        }

        model.addAttribute("job", job);
        model.addAttribute("isSaved", isSaved);
        model.addAttribute("match", matchDto);
        model.addAttribute("hasResume", latestAnalysis.isPresent());
        model.addAttribute("currentUser", user);

        return "jobs/detail";
    }

    @PostMapping("/{id}/save")
    public String toggleSaveJob(
            @PathVariable("id") Long id,
            @RequestHeader(value = "Referer", required = false) String referer,
            RedirectAttributes redirectAttributes) {

        User user = userService.getCurrentAuthenticatedUser();
        boolean saved = jobMatchingService.toggleJobSave(user, id);

        if (saved) {
            redirectAttributes.addFlashAttribute("successMessage", "Job saved to your bookmarks!");
        } else {
            redirectAttributes.addFlashAttribute("infoMessage", "Job removed from your saved bookmarks.");
        }

        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/jobs/" + id;
    }

    @GetMapping("/saved")
    public String savedJobs(Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        List<SavedJob> savedList = jobMatchingService.getSavedJobsForUser(user);

        model.addAttribute("savedJobs", savedList);
        model.addAttribute("currentUser", user);

        return "jobs/saved";
    }

    @GetMapping("/match")
    public String matchJobs(Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        Optional<ResumeAnalysis> latestAnalysis = analysisRepository.findTopByResumeUserOrderByCreatedAtDesc(user);

        if (latestAnalysis.isEmpty()) {
            return "redirect:/resumes/upload";
        }

        List<JobMatchDto> matches = jobMatchingService.matchAllJobs(latestAnalysis.get(), user);

        model.addAttribute("matches", matches);
        model.addAttribute("analysis", latestAnalysis.get());
        model.addAttribute("currentUser", user);

        return "jobs/match";
    }
}
