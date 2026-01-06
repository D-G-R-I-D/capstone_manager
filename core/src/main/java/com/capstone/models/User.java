package com.capstone.models;
import com.capstone.models.enums.*;
import com.capstone.models.enums.Role;

import java.time.LocalDateTime;

public class User {
    private String id;
    private String username;
    private String email;
    private String passwordHash;
    private Role role; // STUDENT, SUPERVISOR, SENIOR_SUPERVISOR, ADMIN
    private LocalDateTime createdAt;

    public User(String id, String username, String email, String passwordHash, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}

