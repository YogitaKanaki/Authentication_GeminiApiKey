package com.example.securityandapi.service;

import com.example.securityandapi.model.Users;
import com.example.securityandapi.repository.UserRepo;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10); //strength means rounds of hashing


    private static final Logger logger = (Logger) LogManager.getLogger(UserService.class);

    public Users register(Users user) {
        logger.info("Registering user: {}", user.getUsername());
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public String verify(Users user) {
        try {
            Authentication auth =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    user.getUsername(), user.getPassword()
                            )
                    );

            if (auth.isAuthenticated()) {
                logger.info("Login successful: {}", user.getUsername());
                return jwtService.generateToken(user.getUsername());
            }
        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", user.getUsername(), e);
        }
        return "Fail";
    }
}


