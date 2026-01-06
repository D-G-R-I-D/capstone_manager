package com.capstone.dao;
import com.capstone.models.User;

import java.util.List;

public interface UserDAOinterface {
    boolean createUser(User user);
    User findById(String id);
    User findByUsername(String username);
    List<User> getAllUsers();
    void update(User user);
    boolean deleteUser(String id);
    User findByUsernameOrEmail(String usernameOrEmail);
}
