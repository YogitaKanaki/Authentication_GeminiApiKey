package com.example.securityandapi.controller;

import com.example.securityandapi.service.SecureDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/secure")
public class SecureDataController {

    @Autowired
    private SecureDataService service;

    // Encrypt & Save
    @PostMapping(value = "/save", consumes = "application/json")
    public String save(@RequestBody Map<String, String> body) {
        String secretData = body.get("secretData");
        service.saveData(secretData);
        return "Data encrypted and saved successfully";
    }


    // Fetch & Decrypt
    @GetMapping("/get/{id}")
    public String get(@PathVariable Long id) {
        return service.getDecryptedData(id);
    }
}
