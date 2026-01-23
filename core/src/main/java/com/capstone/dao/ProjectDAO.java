package com.capstone.dao;

import com.capstone.models.Project;
import com.capstone.models.enums.ProjectStatus;
import com.capstone.utils.DBConnection;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class ProjectDAO implements ProjectDAOInterface{
    private static final Logger LOGGER = Logger.getLogger(ProjectDAO.class.getName());

    public void save(Project project) {
        String sql = """
        INSERT INTO projects (id, title, student_id, supervisor_id, file_path, created_at, status)
        VALUES (?,?,?,?,?,?,?)
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, project.getId());
            stmt.setString(2, project.getTitle());
            stmt.setString(3, project.getStudentId());
            stmt.setString(4, project.getSupervisorId());
            stmt.setString(5, project.getFilePath());
            stmt.setString(6, project.getCreatedAt().toString());
            stmt.setString(7, project.getStatus().name());


            stmt.executeUpdate();
        } catch (Exception e) {
            LOGGER.severe("Failed to create project: " + e.getMessage());}
    }

    public void deleteProject(String projectId) {
        // 1. Get the file path before deleting the record
//        String filePath = getFilePathById(projectId);

        String query = "DELETE FROM projects WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, projectId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Project deleted successfully from database.");
            }
//            // 2. If DB delete was successful, delete the physical file
//            if (affectedRows > 0 && filePath != null) {
//                deletePhysicalFile(filePath);
//            }
        } catch (SQLException e) {
            LOGGER.severe("Error deleting project: " + e.getMessage());
        }
    }

//    public void getFilePathById() {
//        String query = "DELETE file_path FROM projects WHERE I"
//    }

    private void deletePhysicalFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public Project findById(String id) {
        String sql = "SELECT * FROM projects WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return mapProject(rs);

        } catch (Exception e) {
            LOGGER.severe("Failed to find project by Id: " + e.getMessage());}

        return null;
    }

    public void attachFile(String projectId, String filePath) {
        String sql = "UPDATE projects SET file_path = ?, status = ? WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, filePath);
            ps.setString(2, ProjectStatus.COMPLETED.name());
            ps.setString(3, projectId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFilePathById(String projectId) {
        String query = "SELECT file_path FROM projects WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("file_path");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
//    @Override
//    public void save(Project p) {
//        String sql = """
//        INSERT INTO projects
//        (id, title, student_id, supervisor_id, status, created_at)
//        VALUES (?,?,?,?,?,?)
//       """;
//        try (Connection c = DBConnection.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//
//            ps.setString(1, p.getId());
//            ps.setString(2, p.getTitle());
//            ps.setString(3, p.getStudentId());
//            ps.setString(4, p.getSupervisorId());
//            ps.setString(5, p.getStatus().name());
//            ps.setTimestamp(6, Timestamp.valueOf(p.getCreatedAt()));
//            ps.executeUpdate();
//        } catch (Exception e) {
//            LOGGER.severe("Failed to save project: " + e.getMessage());}
//    }

    @Override
    public boolean update(Project p) {
        String sql = "UPDATE projects SET status=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getStatus().name());
            ps.setString(2, p.getId());
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            LOGGER.severe("Failed to update project: " + e.getMessage());}
        return false;
    }

    @Override
    public List<Project> findByStudent(String studentId) {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE student_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapProject(rs));
            }
        } catch (Exception e) { LOGGER.severe("Failed to find project by student: " + e.getMessage());}
        return list;
    }

    @Override
    public List<Project> findAll() {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM projects";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapProject(rs));
            }
        } catch (Exception e) { LOGGER.severe("Failed to find all projects: " + e.getMessage());}
        return list;
    }

    @Override
    public List<Project> findBySupervisor(String supervisorId) {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE supervisor_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, supervisorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapProject(rs));
            }
        } catch (Exception e) {
            LOGGER.severe("Failed to find projects by supervisor: " + e.getMessage());
        }
        return list;
    }


    private Project mapProject(ResultSet rs) throws SQLException {
        return new Project(
                rs.getString("id"),
                rs.getString("title"),
                rs.getString("student_id"),
                rs.getString("supervisor_id"),
//                rs.getString("senior_supervisor_id"),
                rs.getString("file_path"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                ProjectStatus.valueOf(rs.getString("status"))
        );
    }
}
