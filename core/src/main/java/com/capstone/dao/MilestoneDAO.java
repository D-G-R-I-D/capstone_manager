package com.capstone.dao;

import com.capstone.models.Milestone;
import com.capstone.models.enums.MilestoneStatus;
import com.capstone.utils.DBConnection;

import java.sql.*;
import java.util.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

public class MilestoneDAO implements MilestoneDaointerface {
    private static final Logger LOGGER = Logger.getLogger(MilestoneDAO.class.getName());

    public boolean save(Milestone m) {
        String sql = """
            INSERT INTO milestones (id, project_id, title, deadline, status)
            VALUES (?,?,?,?,?)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, m.getId());
            ps.setString(2, m.getProjectId());
            ps.setString(3, m.getTitle());
            ps.setDate(4, Date.valueOf(m.getDeadline()));
            ps.setString(5, m.getStatus().name());

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            LOGGER.severe("Failed to save milestone: " + e.getMessage());}
        return false;
    }

    @Override
    public Milestone findById(String id) {
        String sql = "SELECT * FROM milestones WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Milestone(
                        rs.getString("id"),
                        rs.getString("project_id"),
                        rs.getString("title"),
                        rs.getDate("deadline").toLocalDate(),
                        MilestoneStatus.valueOf(rs.getString("status"))
                );
            }
        } catch (Exception e) { LOGGER.severe("Failed to find milestone by id: " + e.getMessage());}
        return null;
    }


    @Override
    public List<Milestone> findByProject(String projectId) {
        List<Milestone> list = new ArrayList<>();
        String sql = "SELECT * FROM milestones WHERE project_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Milestone(
                        rs.getString("id"),
                        rs.getString("project_id"),
                        rs.getString("title"),
                        rs.getDate("deadline").toLocalDate(),
                        MilestoneStatus.valueOf(rs.getString("status"))
                ));
            }
        } catch (Exception e) { LOGGER.severe("Failed to find milestone by project: " + e.getMessage());}
        return list;
    }

    @Override
    public void update(Milestone m) {
        String sql = "UPDATE milestones SET title=?, deadline=?, status=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setDate(2, Date.valueOf(m.getDeadline()));
            ps.setString(3, m.getStatus().name());
            ps.setString(4, m.getId());
            ps.executeUpdate();
        } catch (Exception e) { LOGGER.severe("Failed to update milestone: " + e.getMessage());}
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM milestones WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (Exception e) { LOGGER.severe("Failed to delete milestone: " + e.getMessage());}
    }
}

