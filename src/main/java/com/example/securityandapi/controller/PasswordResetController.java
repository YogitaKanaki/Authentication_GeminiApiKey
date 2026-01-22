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
    public String forgotPassword(
            @RequestParam String username,
            HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        return service.forgotPassword(username.trim(), ip);
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {

        return service.resetPassword(token, newPassword);
    }
}

