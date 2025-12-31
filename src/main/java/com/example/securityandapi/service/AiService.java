package com.example.securityandapi.service;

import com.example.securityandapi.model.ChatHistory;
import com.example.securityandapi.repository.ChatHistoryRepo;
import com.google.genai.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AiService {

    private final ChatHistoryRepo repo;
    private final Client client;

    private static final Logger logger = (Logger) LogManager.getLogger(AiService.class);


    public AiService(ChatHistoryRepo repo,
                     @Value("${gemini.api-key:}") String apiKey) {
        this.repo = repo;

        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("Gemini API key is missing. Client not initialized.");
            this.client = null;
        } else {
            this.client = Client.builder().apiKey(apiKey).build();
            logger.info("Gemini client initialized successfully.");
        }
    }


    //asks gemini api for response
    public String askGemini(String prompt, String username) {

        if (client == null) {
            logger.error("Gemini client is null. Username: {}", username);
            return "Gemini client is not initialized.";
        }

        String response;
        try {
            logger.info("Calling Gemini API for user: {}", username);
            response = client.models
                    .generateContent("gemini-2.5-flash", prompt, null)
                    .text();
        } catch (Exception e) {
            logger.error("Gemini API call failed", e);
            response = "Error while calling Gemini API";
        }

        //  Save prompt & response to database
        try {
            ChatHistory history = new ChatHistory();
            history.setUserPrompt(prompt);
            history.setAiResponse(response);
            history.setUsername(username);
            history.setTimestamp(LocalDateTime.now());
            repo.save(history);

            logger.info("Chat history saved for user: {}", username);
        } catch (Exception e) {
            logger.error("Failed to save chat history", e);
        }

        return response;
    }

    public List<ChatHistory> getChatHistory(String username) {
        return repo.findByUsername(username);
    }


}
