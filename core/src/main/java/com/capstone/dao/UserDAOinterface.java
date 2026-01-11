package com.capstone.dao;
import com.capstone.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDAOinterface {
    void createUser(User user);
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    List<User> getAllUsers();
    void update(User user);
    boolean deleteUser(String id);
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);
}
