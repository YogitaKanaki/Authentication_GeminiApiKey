package com.example.securityandapi.service;

import com.example.securityandapi.model.SecureData;
import com.example.securityandapi.repository.SecureDataRepo;
import com.example.securityandapi.util.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecureDataService {

    @Autowired
    private SecureDataRepo repo;

    // SAVE encrypted data
    public SecureData saveData(String rawData) {
        SecureData data = new SecureData();
        data.setSecretData(AESUtil.encrypt(rawData));
        return repo.save(data);
    }

    // FETCH decrypted data
    public String getDecryptedData(Long id) {
        SecureData data = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Data not found"));

        return AESUtil.decrypt(data.getSecretData());
    }
}
