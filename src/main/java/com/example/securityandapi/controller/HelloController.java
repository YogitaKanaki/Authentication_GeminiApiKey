package com.example.securityandapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String grett(HttpServletRequest request){
        return "Hello World " + request.getSession().getId();
    }
}
