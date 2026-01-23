package com.example.securityandapi.repository;

import com.example.securityandapi.model.SecureData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecureDataRepo extends JpaRepository<SecureData, Long> {
}
