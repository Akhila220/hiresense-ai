package com.hiresense.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = 500;
        String errorMessage = "An unexpected error occurred. Please try again or return to the dashboard.";

        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorMessage = "The requested page or resume record could not be found.";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                errorMessage = "Access Denied: You do not have permission to view or manipulate this record.";
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                errorMessage = "The server could not process your request due to invalid input.";
            }
        }

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);
        return "error/error";
    }

    @GetMapping("/error/403")
    public String accessDenied(Model model) {
        model.addAttribute("statusCode", 403);
        model.addAttribute("errorMessage", "Access Denied: You do not have authorization to access this resource.");
        return "error/error";
    }
}
