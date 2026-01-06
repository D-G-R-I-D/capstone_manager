package com.capstone.services;
import com.capstone.dao.UserDAO;
import com.capstone.models.User;
import com.capstone.utils.PasswordUtil;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User login(String usernameOrEmail, String password) {
        User user = userDAO.findByUsernameOrEmail(usernameOrEmail);
        if (user == null) {
            System.out.println("User not found for: " + usernameOrEmail);  // Log to console
            throw new RuntimeException("User not found");
        }
        String storedHash = user.getPasswordHash();
        System.out.println("Stored hash: " + storedHash);  // Log hash
        System.out.println("Input password: " + password);  // Log input (temporary - remove for security)
        if (!PasswordUtil.verify(password, storedHash)) {
            System.out.println("Password verification failed");
            throw new RuntimeException("Invalid password");
        }
        return user;
    }
}
