package com.capstone.services;
import com.capstone.dao.UserDAO;
import com.capstone.models.User;
import com.capstone.utils.PasswordUtil;

import javax.naming.AuthenticationException;
import java.util.Optional;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User login(String identifier, String password) throws AuthenticationException {
        identifier = identifier.trim().toLowerCase();  // Trim and lower for consistency
        password = password.trim();  // Trim password

        Optional<User> userOpt = userDAO.findByEmail(identifier);

        // Unwrap the Optional: Throw if no user found (for invalid identifier)
        User user = userOpt.orElseThrow(() -> new AuthenticationException("Invalid username or email"));

        // Now 'user' is a plain User object, so you can call its methods
        if (user == null) {
            System.out.println("User not found for identifier: " + identifier);
            throw new RuntimeException("User not found");
        }

        String storedHash = user.getPasswordHash();  // Trim hash from DB
        System.out.println("Stored hash: " + storedHash);
        System.out.println("Input password: " + password);

        if (!PasswordUtil.verify(password, storedHash)) {
            System.out.println("Password verification failed");
            throw new AuthenticationException("Invalid password");
        }

        System.out.println("Login successful for user: " + user.getUsername());
        return user;
    }
}
