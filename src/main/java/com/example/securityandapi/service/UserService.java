package com.example.securityandapi.service;

import com.example.securityandapi.model.Users;
import com.example.securityandapi.repository.UserRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private BCryptPasswordEncoder encoder;


    private static final Logger logger = (Logger) LogManager.getLogger(UserService.class);

    public Users register(Users user) {
        logger.info("Registering user: {}", user.getUsername());
        user.setPassword(encoder.encode(user.getPassword()));
        user.setFavorite_teacher(encoder.encode(user.getFavorite_teacher().toLowerCase().trim()));
        return repo.save(user);
    }


    public String verify(Users user) {

        Users dbUser = repo.findByUsername(user.getUsername());

        if (dbUser == null) {
            logger.warn("Login attempt for non-existing user: {}", user.getUsername());
            return "Invalid username or password";
        }


        //  Auto-unlock after 1 hour
        if (dbUser.getLockUntil() != null &&
                dbUser.getLockUntil().isBefore(LocalDateTime.now())) {

            dbUser.setLockUntil(null);
            dbUser.setFailedAttempts(0);
            repo.save(dbUser);

            logger.info("User {} automatically unlocked after lock period",
                    user.getUsername());
        }

        //  Check if still locked
        if (dbUser.getLockUntil() != null) {
            logger.warn("Blocked login attempt for user {}",
                    user.getUsername());

            return "Login failed 3 times. Account blocked for 1 hour.";
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(), user.getPassword()
                    )
            );

            //  Successful login
            dbUser.setFailedAttempts(0);
            dbUser.setLockUntil(null);
            repo.save(dbUser);

            logger.info("Login successful: {}", user.getUsername());
            return jwtService.generateToken(user.getUsername());

        } catch (BadCredentialsException e) {

            int attempts = dbUser.getFailedAttempts() + 1;
            dbUser.setFailedAttempts(attempts);

            logger.warn("Login failed for user {} (attempt {})",
                    user.getUsername(), attempts);

            //  Lock after 3rd failure
            if (attempts > 3) {
                dbUser.setLockUntil(LocalDateTime.now().plusHours(1));
                repo.save(dbUser);

                logger.error("User {} blocked for 1 hour due to 3 failed attempts",
                        user.getUsername());

                return "Login failed 3 times. Account blocked for 1 hour.";
            }
            

            repo.save(dbUser);
            return "Invalid username or password";
        }
    }

}



