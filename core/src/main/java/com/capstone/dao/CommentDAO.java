package com.capstone.dao;

//import javax.xml.stream.events.Comment;
import com.capstone.models.Comment;
import com.capstone.utils.DBConnection;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class CommentDAO implements CommentDAOinterface {
    private static final Logger LOGGER = Logger.getLogger(CommentDAO.class.getName());

    @Override
    public boolean save(Comment c) {
        String sql = """
            INSERT INTO comments (id, project_id, author_id, message, parent_id)
            VALUES (?,?,?,?,?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getId());
            ps.setString(2, c.getProjectId());
            ps.setString(3, c.getAuthorId());
            ps.setString(4, c.getMessage());
            ps.setString(5, c.getParentId()); // nullable

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            LOGGER.severe("Failed to save comment: " + e.getMessage());}
        return false;
    }

    @Override
    public Comment findById(String id) {
        String sql = "SELECT * FROM comments WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapComment(rs);
        } catch (Exception e) {
            LOGGER.severe("Failed to find comment by Id: " + e.getMessage());}
        return null;
    }

    @Override
    public List<Comment> findByProject(String projectId) {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE project_id=? AND parent_id IS NULL";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapComment(rs));
        } catch (Exception e) {
            LOGGER.severe("Failed to find comment by project: " + e.getMessage());}
        return list;
    }

    @Override
    public List<Comment> findReplies(String parentId) {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE parent_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, parentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapComment(rs));
        } catch (Exception e) {
            LOGGER.severe("Failed to find comment by replies: " + e.getMessage());}
        return list;
    }

    @Override
    public void delete(String id) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM comments WHERE id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.severe("Failed to comment by Id: " + e.getMessage());}
    }

    private Comment mapComment(ResultSet rs) throws SQLException {
        return new Comment(
                rs.getString("id"),
                rs.getString("project_id"),
                rs.getString("author_id"),
                rs.getString("message"),
                rs.getString("parent_id"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}

