package com.example.securityandapi.service;

import com.example.securityandapi.model.PasswordResetToken;
import com.example.securityandapi.model.Users;
import com.example.securityandapi.repository.PasswordResetTokenRepo;
import com.example.securityandapi.repository.UserRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordResetTokenRepo tokenRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private static final Logger logger =
            (Logger) LogManager.getLogger(PasswordResetService.class);

    // STEP 0: Get security question
    public String getSecurityQuestion(String username) {

        Users user = userRepo.findByUsername(username.trim());

        if (user == null) {
            return "User does not exist";
        }

        return "Who is your favorite teacher?";
    }


    // STEP 1: Verify answer & generate token
    public String verifyAnswerAndGenerateToken(
            String username, String answer, String ip) {

        Users user = userRepo.findByUsername(username.trim());

        if (user == null) {
            return "Invalid answer";
        }

        boolean matches = encoder.matches(
                answer.toLowerCase().trim(),
                user.getFavorite_teacher()
        );

        if (!matches) {
            return "Invalid answer";
        }

        tokenRepo.deleteByUsername(username);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUsername(username);
        resetToken.setToken(token);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        resetToken.setRequestIp(ip);

        tokenRepo.save(resetToken);

        logger.info("Security answer verified for user {}", username);

        return "Reset token: " + token;
    }


    // STEP 2: Reset password
    public String resetPassword(String token, String newPassword) {

        Optional<PasswordResetToken> opt = tokenRepo.findByToken(token);

        if (opt.isEmpty()) {
            return "Invalid reset token";
        }

        PasswordResetToken resetToken = opt.get();

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return "Reset token expired";
        }

        Users user = userRepo.findByUsername(resetToken.getUsername());
        user.setPassword(encoder.encode(newPassword));
        user.setFailedAttempts(0);
        user.setLockUntil(null);

        userRepo.save(user);
        tokenRepo.delete(resetToken);

        logger.info("Password reset successful for user {}", user.getUsername());

        return "Password reset successful";
    }
}