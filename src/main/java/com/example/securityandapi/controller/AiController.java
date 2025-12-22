package com.example.securityandapi.controller;

import com.example.securityandapi.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;


    @PostMapping("/prompt")
    public String promptAI(@RequestBody Map<String, String> body, Principal principal) {
        String prompt = body.get("prompt");
        String username = principal.getName();
        return aiService.askGemini(prompt, username);
    }

    @GetMapping("/response")
    public Map<String, Object> getChatHistory(Principal principal) {
        String username = principal.getName();

        return Map.of(
                "username: ", username,
                "response: ", aiService.getChatHistory(username)
        );
    }


}
