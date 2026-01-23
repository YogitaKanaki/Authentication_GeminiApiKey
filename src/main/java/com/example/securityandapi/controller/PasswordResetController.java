package com.example.securityandapi.controller;

import com.example.securityandapi.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class PasswordResetController {

    @Autowired
    private PasswordResetService service;

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String username) {
        return service.getSecurityQuestion(username);
    }

    @PostMapping("/verify-answer")
    public String verifyAnswer(
            @RequestParam String username,
            @RequestParam String answer,
            HttpServletRequest request) {

        return service.verifyAnswerAndGenerateToken(
                username, answer, request.getRemoteAddr());
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {

        return service.resetPassword(token, newPassword);
    }
}

