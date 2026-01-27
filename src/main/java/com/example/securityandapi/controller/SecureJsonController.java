package com.example.securityandapi.controller;


import com.example.securityandapi.model.SecureJson;
import com.example.securityandapi.service.SecureJsonService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/secure-json")
public class SecureJsonController {

    private final SecureJsonService service;

    public SecureJsonController(SecureJsonService service) {
        this.service = service;
    }

    // POST → encrypt & store
    @PostMapping("/save")
    public SecureJson save(@RequestBody Map<String, Object> json) throws Exception {
        return service.saveJson(json);
    }

    // GET → decrypt & return
    @GetMapping("/get/{id}")
    public Map<String, Object> get(@PathVariable Long id) throws Exception {
        return service.getDecryptedJson(id);
    }

}
