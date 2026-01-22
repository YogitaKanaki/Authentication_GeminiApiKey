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

    // STEP 1: Forgot password
    public String forgotPassword(String username, String ip) {


        username = username.trim();
        Optional<Users> userOpt = Optional.ofNullable(userRepo.findByUsername(username));

        System.out.println("Username from request = [" + username + "]");
        System.out.println("User found in DB = " + userOpt.isPresent());

        if (userOpt.isEmpty()) {
            logger.warn("Forgot password attempt for invalid user from IP {}", ip);
            return "If user exists, reset link will be sent";
        }

        Users user = userOpt.get();

        if (user == null) {
            logger.warn("Forgot password attempt for invalid user from IP {}", ip);
            return "If user exists, reset link will be sent";
        }

        tokenRepo.deleteByUsername(username);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUsername(username);
        resetToken.setToken(token);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        resetToken.setRequestIp(ip);

        try {
            tokenRepo.save(resetToken);
        } catch (Exception e) {
            e.printStackTrace();
        }


        logger.info("Password reset requested for user {} from IP {}",
                username, ip);

        // In real apps → send email
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

        logger.info("Password reset successful for user {}",
                user.getUsername());

        return "Password reset successful";
    }
}
