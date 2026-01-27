package com.example.securityandapi.service;


import com.example.securityandapi.model.SecureJson;
import com.example.securityandapi.repository.SecureJsonRepo;
import com.example.securityandapi.util.AESUtil1;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SecureJsonService {

    private final SecureJsonRepo repo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SecureJsonService(SecureJsonRepo repo) {
        this.repo = repo;
    }

    // Save Json (encrypt before DB)
    public SecureJson saveJson(Map<String, Object> jsonMap) throws Exception {
        String jsonString = objectMapper.writeValueAsString(jsonMap);
        String encrypted = AESUtil1.encrypt(jsonString);

        SecureJson entity = new SecureJson();
        entity.setEncryptedJson(encrypted);

        return repo.save(entity);
    }

    // Fetch Json (decrypt after DB)
    public Map<String, Object> getDecryptedJson(Long id) throws Exception {

        SecureJson entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        // Decrypt first
        String decryptedJson = AESUtil1.decrypt(entity.getEncryptedJson());

        // Convert to Map (pretty JSON in Postman)
        return objectMapper.readValue(decryptedJson, Map.class);
    }


}

