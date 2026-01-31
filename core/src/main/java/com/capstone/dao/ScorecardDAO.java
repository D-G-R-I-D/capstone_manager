package com.capstone.dao;

import com.capstone.models.Scorecard;
import com.capstone.models.enums.Role;
import com.capstone.utils.DBConnection;
import java.util.*;
import java.sql.*;
import java.util.logging.Logger;


public class ScorecardDAO implements ScoreCardDAOinterface {
    private static final Logger LOGGER = Logger.getLogger(ScorecardDAO.class.getName());

    public boolean save(Scorecard s) {
        String sql = """
            INSERT INTO scorecards
            (id, project_id, graded_by, role, override,
             technical_depth, problem_solving,
             presentation, design, total_score, graded_at)
            VALUES (?,?,?,?,?,?,?,?,?,?,?)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, s.getId());
            ps.setString(2, s.getProjectId());
            ps.setString(3, s.getGradedBy());
            ps.setString(4, s.getRole().name()); //(String.valueOf(s.getRole()));*****************
            ps.setBoolean(5, s.isOverride());
            ps.setInt(6, s.getTechnicalDepth());
            ps.setInt(7, s.getProblemSolving());
            ps.setInt(8, s.getPresentation());
            ps.setInt(9, s.getDesign());
            ps.setInt(10, s.getTotalScore());
            ps.setTimestamp(11, Timestamp.valueOf(s.getGradedAt()));  // ← Add this

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            LOGGER.severe("Failed to save scorecard: " + e.getMessage());;
        }
        return false;
    }

    @Override
    public Scorecard findById(String id) {
        String sql = "SELECT * FROM scorecards WHERE id=? OR projectId=?"; //******************
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapScorecard(rs);
        } catch (Exception e) { LOGGER.severe("Failed to find scorecard by Id: " + e.getMessage());}
        return null;
    }

    @Override
    public List<Scorecard> findByProject(String projectId) {
        List<Scorecard> list = new ArrayList<>();
        String sql = "SELECT * FROM scorecards WHERE project_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapScorecard(rs));
        } catch (Exception e) { LOGGER.severe("Failed to find scorecard by project: " + e.getMessage());}
        return list;
    }

    @Override
    public List<Scorecard> findByGrader(String userId) {
        List<Scorecard> list = new ArrayList<>();
        String sql = "SELECT * FROM scorecards WHERE graded_by=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapScorecard(rs));
        } catch (Exception e) { LOGGER.severe("Failed to find score card by grader: " + e.getMessage());}
        return list;
    }

    @Override
    public void update(Scorecard s) {
        String sql = "UPDATE scorecards SET total_score=? WHERE id=? OR project_id=?"; //*****************
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, s.getTotalScore());
            ps.setString(2, s.getId());
            ps.setString(3, s.getId());
            ps.executeUpdate();
        } catch (Exception e) { LOGGER.severe("Failed to find update scorecard: " + e.getMessage());}
    }

    public List<Scorecard>findAllScores() {
        List<Scorecard> list = new ArrayList<>();
        String sql = "SELECT * FROM scorecards";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapScorecard(rs));
        } catch (Exception e) { LOGGER.severe("Failed to find all scores / scorecards: " + e.getMessage());}
        return list;
    }

    private Scorecard mapScorecard(ResultSet rs) throws Exception {
        return new Scorecard(
                rs.getString("id"),
                rs.getString("project_id"),
                rs.getString("graded_by"),
                Role.valueOf(rs.getString("role")),
                rs.getBoolean("override"),
                rs.getInt("technical_depth"),
                rs.getInt("problem_solving"),
                rs.getInt("presentation"),
                rs.getInt("design"),
                rs.getInt("total_score"),
                rs.getTimestamp("graded_at").toLocalDateTime()
        );
    }
}
