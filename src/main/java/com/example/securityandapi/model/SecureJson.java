package com.example.securityandapi.model;


import jakarta.persistence.*;

@Entity
@Table(name = "secure_json")
public class SecureJson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "encrypted_json", columnDefinition = "TEXT")
    private String encryptedJson;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEncryptedJson() { return encryptedJson; }
    public void setEncryptedJson(String encryptedJson) { this.encryptedJson = encryptedJson; }
}
