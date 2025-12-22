package com.example.securityandapi.service;

import com.example.securityandapi.model.ChatHistory;
import com.example.securityandapi.repository.ChatHistoryRepo;
import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AiService {

    private final ChatHistoryRepo repo;
    private final Client client;


    public AiService(ChatHistoryRepo repo,
                     @Value("${gemini.api-key:}") String apiKey) {
        this.repo = repo;

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("WARNING: Gemini API key is missing. Client will not be initialized!");
            this.client = null; // client is null if API key is missing
        } else {
            this.client = Client.builder().apiKey(apiKey).build();
        }
    }


    //asks gemini api for response
    public String askGemini(String prompt, String username) {

        //  If client is not initialized, return error message
        if (client == null) {
            return "Gemini client is not initialized. Please provide a valid API key.";
        }

        String response;
        try {
            // Call Gemini API
            response = client.models.generateContent("gemini-2.5-flash", prompt, null).text();
        } catch (Exception e) {
            // Handle API errors 
            response = "Error while calling Gemini API: " + e.getMessage();
        }

        //  Save prompt & response to database
        try {
            ChatHistory history = new ChatHistory();
            history.setUserPrompt(prompt);
            history.setAiResponse(response);
            history.setUsername(username);
            history.setTimestamp(LocalDateTime.now());
            repo.save(history);
        } catch (Exception e) {
            System.err.println("Warning: failed to save chat history: " + e.getMessage());
        }
        return response;
    }

    public List<ChatHistory> getChatHistory(String username) {
        return repo.findByUsername(username);
    }


}
