package com.example.securityandapi.repository;

import com.example.securityandapi.model.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepo extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByUsername(String username);

}

