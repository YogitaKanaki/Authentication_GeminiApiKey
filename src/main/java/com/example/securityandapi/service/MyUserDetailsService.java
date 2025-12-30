package com.example.securityandapi.service;

import com.example.securityandapi.model.UserPrincipal;
import com.example.securityandapi.model.Users;
import com.example.securityandapi.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger logger =
            LoggerFactory.getLogger(MyUserDetailsService.class);

    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) {

        Users user = repo.findByUsername(username);

        if (user == null) {
            logger.warn("User not found: {}", username);
            throw new UsernameNotFoundException("User Not Found");
        }

        logger.info("User authenticated: {}", username);
        return new UserPrincipal(user);
    }
}

