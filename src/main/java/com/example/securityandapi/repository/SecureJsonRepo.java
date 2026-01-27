package com.example.securityandapi.repository;

import com.example.securityandapi.model.SecureJson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecureJsonRepo extends JpaRepository<SecureJson, Long> {
}
