package com.capstone.services;

import com.capstone.ExceptionClass.DatabaseException;
import com.capstone.ExceptionClass.UserNotFoundException;
import com.capstone.dao.UserDAO;
import com.capstone.models.User;
import com.capstone.models.enums.Role;
import com.capstone.utils.PasswordUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public User register(String username, String email, @NotNull String password, Role role) {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        String hashed = PasswordUtil.hash(password.trim());
        System.out.println("NEW HASH for " + username + ": " + hashed);  // Debug
        User user = new User(
                UUID.randomUUID().toString(),
                username.trim(),
                email.trim(),
                hashed,
                role
        );

        try {
            userDAO.createUser(user);
        } catch (DatabaseException e) {
            throw new RuntimeException("Failed to save user to database: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during registration", e);
        }

        return user;
    }

    public void updateRole(String userId, Role newRole) {
        Optional<User> userOpt = userDAO.findById(userId);
        // Unwrap the Optional: Throw if no user found (for invalid identifier)
        User user = userOpt
                .orElseThrow(() -> new UserNotFoundException("User not found with Id" + userId));

        user.setRole(newRole);
        userDAO.update(user);           //update() throws DatabaseException on real failure
    }

    public void changePassword(String userId, String newPassword) {
        Optional<User> userOpt = userDAO.findById(userId);
//        if (userOpt.isEmpty()) {
//            throw new RuntimeException("User not found");
//        }

        User user = userOpt.orElseThrow(() -> new UserNotFoundException("User not found with Id" + userId));

        String hashed = PasswordUtil.hash(newPassword);
        user.setPasswordHash(hashed);
        userDAO.update(user);
    }

    public Optional<User> findById(String id) {
        return userDAO.findById(id);
    }

    // For callers that expect user to exist by id
    public User getUserById(String id) {
        return userDAO.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    // For callers that expect user to exist
    public User getUserByEmail(String identifier) {
        return userDAO.findByEmail(identifier)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + identifier));
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public void deleteUser(String Id) {
        userDAO.deleteUser(Id);  // Implement delete in DAO
    }

    public void updateUser(User user) {
        userDAO.update(user);  // Implement update in DAO (role change)
    }
}