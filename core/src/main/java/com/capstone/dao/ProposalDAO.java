package com.capstone.dao;

import com.capstone.models.Proposal;
import com.capstone.models.enums.ProposalStatus;
import com.capstone.utils.DBConnection;
import com.capstone.models.enums.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;


public class ProposalDAO implements ProposalDAOinterface{
    private static final Logger LOGGER = Logger.getLogger(ProposalDAO.class.getName());

    public boolean submitProposal(Proposal proposal) {
        String sql = "INSERT INTO proposals (id, student_id, title, description, status) VALUES (?,?,?,?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, proposal.getId());
            stmt.setString(2, proposal.getSubmittedBy());
            stmt.setString(4, proposal.getSummary());
            stmt.setString(5, proposal.getStatus().name());

            return stmt.executeUpdate() == 1;

        } catch (Exception e) { LOGGER.severe("Failed to submit proposal: " + e.getMessage());}

        return false;
    }

//    public void save(Proposal p) {
//        String sql = """
//        INSERT INTO proposals
//        (id, summary, submitted_by, status, submitted_at)
//        VALUES (?,?,?,?,?)
//    """;
//        try (Connection c = DBConnection.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//
//            ps.setString(1, p.getId());
//            ps.setString(2, p.getSummary());
//            ps.setString(3, p.getSubmittedBy());
//            ps.setString(4, p.getStatus().name());
//            ps.setTimestamp(5, Timestamp.valueOf(p.getSubmittedAt()));
//            ps.executeUpdate();
//        } catch (Exception e) {
//            LOGGER.severe("Failed to save proposal: " + e.getMessage());}
//    }

    @Override
    public boolean create(Proposal proposal) {
        String sql = """
        INSERT INTO proposals (id, title, description, student_id, file_path, status, submitted_at)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, proposal.getId()); // or UUID
            ps.setString(2, proposal.getTitle());
            ps.setString(3, proposal.getDescription());
            ps.setString(4, proposal.getStudentId());
            ps.setString(5, proposal.getFilePath());
            ps.setString(6, proposal.getStatus().name());
            ps.setTimestamp(7, Timestamp.valueOf(proposal.getSubmittedAt()));

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            LOGGER.severe("Failed to create proposal: " + e.getMessage());
            return false;
        }
    }


    public Proposal findById(String id) {
        String sql = "SELECT * FROM proposals WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapProposal(rs);

        } catch (Exception e) {
            LOGGER.severe("Failed to find proposal by Id: " + e.getMessage());}
        return null;
    }

    public boolean updateStatus(String proposalId, ProposalStatus status) {
        String sql = """  
                    UPDATE proposals
                    SET status=?, reviewed_by=?, project_id=?,
                        approved_by_supervisor=?, approved_by_senior=?
                    WHERE id=?
                """;

                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement(sql)) {

                        stmt.setString(1, status.name());
                        stmt.setString(2, proposalId);

                        return stmt.executeUpdate() == 1;

                    } catch (Exception e) { LOGGER.severe("Failed to update proposal status: " + e.getMessage());}

                    return false;
    }

    public void update(Proposal p) {
        String sql = """
            UPDATE proposals
            SET status=?, reviewed_by=?, project_id=?,
                approved_by_supervisor=?, approved_by_senior=?
            WHERE id=?
        """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getStatus().name());
            ps.setString(2, p.getReviewedBy());
            ps.setString(3, p.getProjectId());
            ps.setBoolean(4, p.isApprovedBySupervisor());
            ps.setBoolean(5, p.isApprovedBySenior());
            ps.setString(6, p.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.severe("Failed to update proposal: " + e.getMessage());}
    }


    public List<Proposal> getProposalsByStudent(String studentId) {
        List<Proposal> list = new ArrayList<>();
        String sql = "SELECT * FROM proposals WHERE student_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) list.add(mapProposal(rs));

        } catch (Exception e) { LOGGER.severe("Failed to get proposal by student: " + e.getMessage());}

        return list;
    }

    @Override
    public List<Proposal> findByStudent(String studentId) {
        List<Proposal> list = new ArrayList<>();
        String sql = "SELECT * FROM proposals WHERE submitted_by=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapProposal(rs));
        } catch (Exception e) { LOGGER.severe("Failed to find proposal by student: " + e.getMessage());}
        return list;
    }


    @Override
    public List<Proposal> findByProject(String projectId) {
        List<Proposal> list = new ArrayList<>();
        String sql = "SELECT * FROM proposals WHERE project_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapProposal(rs));
        } catch (Exception e) { LOGGER.severe("Failed to find proposal by project: " + e.getMessage());}
        return list;
    }

    @Override
    public Proposal findLatestForProject(String projectId) {
        String sql = """
        SELECT * FROM proposals
        WHERE project_id=?
        ORDER BY submitted_at DESC
        LIMIT 1
    """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapProposal(rs);
        } catch (Exception e) { LOGGER.severe("Failed to find latest proposal for project: " + e.getMessage());}
        return null;
    }

    @Override
    public List<Proposal> findAll() {
        List<Proposal> list = new ArrayList<>();
        String sql = "SELECT * FROM proposals";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapProposal(rs));
        } catch (Exception e) { LOGGER.severe("Failed to find all proposals: " + e.getMessage());}
        return list;
    }

    private Proposal mapProposal(ResultSet rs) throws Exception {
        Proposal p = new Proposal(
                rs.getString("id"),
                rs.getString("submitted_by"),
                rs.getString("summary"),
                ProposalStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("submitted_at").toLocalDateTime()
        );

        p.setProjectId(rs.getString("project_id"));
        p.setReviewedBy(rs.getString("reviewed_by"));
        p.setApprovedBySupervisor(rs.getBoolean("approved_by_supervisor"));
        p.setApprovedBySenior(rs.getBoolean("approved_by_senior"));
        return p;
    }

    public List<Proposal> findByStatus(ProposalStatus status) {
        List<Proposal> list = new ArrayList<>();
        String sql = "SELECT * FROM proposals WHERE status=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(mapProposal(rs));
        } catch (Exception e) {
            LOGGER.severe("Failed to find by Status: " + e.getMessage());
        }
        return list;
    }

    public List<Proposal> findBySupervisorAndStatus(
            String supervisorId, ProposalStatus status) {

        List<Proposal> list = new ArrayList<>();
        String sql = """
        SELECT * FROM proposals
        WHERE reviewed_by=? AND status=?
    """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, supervisorId);
            ps.setString(2, status.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(mapProposal(rs));
        } catch (Exception e) {
            LOGGER.severe("Failed to find by supervisor and status: " + e.getMessage());
        }
        return list;
    }

    public List<Proposal> findBySupervisor(
            String supervisorId) {

        List<Proposal> list = new ArrayList<>();
        String sql = """
        SELECT * FROM proposals
        WHERE reviewed_by=?
    """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, supervisorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(mapProposal(rs));
        } catch (Exception e) {
            LOGGER.severe("Failed to find by supervisor and status: " + e.getMessage());
        }
        return list;
    }
}
