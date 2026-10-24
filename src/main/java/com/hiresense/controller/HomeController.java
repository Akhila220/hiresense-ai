package com.hiresense.controller;

import com.hiresense.entity.User;
import com.hiresense.repository.JobRepository;
import com.hiresense.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class HomeController {

    private final UserService userService;
    private final JobRepository jobRepository;

    public HomeController(UserService userService, JobRepository jobRepository) {
        this.userService = userService;
        this.jobRepository = jobRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        Optional<User> currentUser = userService.getCurrentUserOptional();
        model.addAttribute("currentUser", currentUser.orElse(null));
        model.addAttribute("totalJobsCount", jobRepository.count());
        return "index";
    }
}
