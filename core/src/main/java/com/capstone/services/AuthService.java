package com.capstone.services;
import com.capstone.dao.UserDAO;
import com.capstone.models.User;
import com.capstone.utils.PasswordUtil;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User login(String usernameOrEmail, String password) {
        User user = userDAO.findByUsernameOrEmail(usernameOrEmail);

        if (user == null)
            throw new RuntimeException("User not found");

        if (!PasswordUtil.verify(password, user.getPasswordHash()))
            throw new RuntimeException("Invalid password");

        return user;
    }
}
