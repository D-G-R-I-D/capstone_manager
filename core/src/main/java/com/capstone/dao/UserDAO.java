package com.capstone.dao;

import com.capstone.models.User;
import com.capstone.models.enums.*;
import com.capstone.models.enums.Role;
import com.capstone.utils.DBConnection;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;



public class UserDAO implements UserDAOinterface {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    @Override
    public boolean createUser(User user) {
        String sql = """
        INSERT INTO users (id, username, email, password_hash, role)
        VALUES (?,?,?,?,?)
    """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, user.getId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash()); // HASH ONLY
            ps.setString(5, user.getRole().name());

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            LOGGER.severe("Failed to create user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return mapUser(rs);

        } catch (Exception e) {
            LOGGER.severe("Failed to find user by username: " + e.getMessage());}

        return null;
    }

    @Override
    public User findById(String id){
        String sql = "SELECT * FROM users WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return mapUser(rs);

        } catch (Exception e) {LOGGER.severe("Failed to find user by Id: " + e.getMessage());}

        return null;
    }


    @Override
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) list.add(mapUser(rs));

        } catch (Exception e) { LOGGER.severe("Failed to get all users: " + e.getMessage());}

        return list;
    }

    @Override
    public void update(User user) {
        String sql = """
            UPDATE users
            SET username=?, email=?, password_hash=?, role=?
            WHERE id=?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.severe("Failed to update user: " + e.getMessage());
        }
    }

    public boolean deleteUser(String id) {
        String sql = "DELETE FROM users WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() == 1;

        } catch (Exception e) { LOGGER.severe("Failed to delete user: " + e.getMessage());}

        return false;
    }

    private User mapUser(ResultSet rs) throws Exception {
        return new User(
                rs.getString("id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                Role.valueOf(rs.getString("role"))
        );
    }

    public User findByUsernameOrEmail(String usernameOrEmail) {
        String sql = "SELECT * FROM users WHERE username=? OR email =?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return mapUser(rs);

        } catch (Exception e) { LOGGER.severe("Failed to find user by username or by email: " + e.getMessage());}

        return null;
    }
}

