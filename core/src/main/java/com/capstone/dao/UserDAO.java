package com.capstone.dao;

import com.capstone.ExceptionClass.DatabaseException;
import com.capstone.models.User;
import com.capstone.models.enums.Role;
import com.capstone.utils.DBConnection;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;



public class UserDAO implements UserDAOinterface {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    @Override
    public void createUser(User user) {
        String sql = """
        INSERT INTO users (id, username, email, password_hash, role)
        VALUES (?,?,?,?,?)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, user.getId());            // e.g., UUID.randomUUID().toString()
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash().trim()); // HASH ONLY
            ps.setString(5, user.getRole().name());

            int rowsAffected = ps.executeUpdate();
//            ps.executeUpdate();
            if (rowsAffected == 0) {
                LOGGER.warning("User creation failed: no rows inserted");
                throw new DatabaseException("Failed to create user: no rows inserted", null);
            }
            LOGGER.info("User created successfully with ID: " + user.getId());

        } catch (SQLException e) {
            LOGGER.severe("Failed to create user: " + e.getMessage());
            throw new DatabaseException("Failed to create user : email already exists ", e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
            }

        } catch (SQLException e) {
            LOGGER.severe("Failed to find user by username: " + e.getMessage());}

        return Optional.empty();
    }

    @Override
    public Optional<User> findById(String id){
        String sql = "SELECT * FROM users WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
            }

        } catch (SQLException e) {LOGGER.severe("Failed to find user by Id: " + e.getMessage());}

        return Optional.empty();
    }


    @Override
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) list.add(mapUser(rs));

        } catch (SQLException e) { LOGGER.severe("Failed to get all users: " + e.getMessage());}

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

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new DatabaseException("No user updated — user with ID. " + user.getId() + " may not exist", null);
            }

        } catch (SQLException e) {
            LOGGER.severe("Failed to update user: " + e.getMessage());
            throw new DatabaseException("Failed to update");
        }
    }

    @Override
    public boolean deleteUser(String id) {
        String sql = "DELETE FROM users WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() == 1;

        } catch (SQLException e) { LOGGER.severe("Failed to delete user: " + e.getMessage());}

        return false;
    }

        private User mapUser(ResultSet rs) throws SQLException {
            return (new User(
                    rs.getString("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    Role.valueOf(rs.getString("role").toUpperCase())
            ));
        }

    @Override
    public Optional<User> findByUsernameOrEmail(String identifier) {
        String sql = """
            SELECT id, username, email, password_hash, role 
            FROM users 
            WHERE LOWER(username) = LOWER(?) 
               OR LOWER(email) = LOWER(?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));  // mapUser returns User
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Database error while searching for user: " + e.getMessage());
            throw new DatabaseException("Failed to retrieve user by username or email", e);
            // Consider whether to rethrow as a custom exception or return Optional<User>
        }
        return Optional.empty();
    }
}

