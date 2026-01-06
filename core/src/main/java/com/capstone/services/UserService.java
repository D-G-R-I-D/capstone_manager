package com.capstone.services;

import com.capstone.dao.UserDAO;
import com.capstone.models.User;
import com.capstone.models.enums.Role;
import com.capstone.utils.PasswordUtil;

import java.util.List;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public User register(String username, String email, String password, Role role) {
        String hashed = PasswordUtil.hash(password);

        User user = new User(
                UUID.randomUUID().toString(),
                username,
                email,
                hashed,
                role
        );

        userDAO.createUser(user);
        return user;
    }

    public void updateRole(String userId, Role newRole) {
        User user = userDAO.findById(userId);
        user.setRole(newRole);
        userDAO.update(user);
    }

    public void changePassword(String userId, String newPassword) {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String hashed = PasswordUtil.hash(newPassword);
        user.setPasswordHash(hashed);
        userDAO.update(user);
    }

    public User findById(String id) {
        return userDAO.findById(id);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
}
