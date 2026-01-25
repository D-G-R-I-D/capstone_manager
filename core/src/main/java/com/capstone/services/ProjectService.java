package com.capstone.services;

import com.capstone.dao.*;
import com.capstone.models.*;
import com.capstone.models.enums.MilestoneStatus;
import com.capstone.models.enums.ProjectStatus;
import com.capstone.utils.IdGenerator;
import com.capstone.utils.Session;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ProjectService {

    private final ProjectDAO projectDAO = new ProjectDAO();
    private final CommentDAO commentDAO = new CommentDAO();
    private final ScorecardDAO scorecardDAO = new ScorecardDAO();
    private final MilestoneDAO milestoneDAO = new MilestoneDAO();

    // ==========================================
    // 1. STUDENT LOGIC
    // ==========================================

    public void submitProject(String title, String supervisorId, String filePath) {
        Project project = new Project(
                IdGenerator.generateId(),
                title,
                Session.getUser().getId(),
                supervisorId,
                filePath,
                LocalDateTime.now(),
                ProjectStatus.PENDING
        );
        projectDAO.save(project);
    }

    public List<Project> getProjectsForStudent(String studentId) {
        return projectDAO.findByStudent(studentId);
    }

    // ==========================================
    // 2. SUPERVISOR LOGIC
    // ==========================================

    public List<Project> getProjectsForSupervisor(String supervisorId) {
        return projectDAO.findBySupervisor(supervisorId);
    }

    public void approveProject(String projectId) {
        Project p = projectDAO.findById(projectId);
        if (p != null) {
            p.setStatus(ProjectStatus.ACTIVE);
            projectDAO.update(p);
            // Optional: Create default milestones here if needed
            createDefaultMilestones(projectId);
        }
    }
    private void createDefaultMilestones(String projectId) {
        // Create 4 standard milestones for every new project

        MilestoneService milestoneService = new MilestoneService();

        milestoneService.create(projectId,  "Chapter 1: Introduction & Proposal", LocalDate.now().plusWeeks(2));
        milestoneService.create(projectId, "Chapter 2: Literature Review", LocalDate.now().plusWeeks(4));
        milestoneService.create(projectId, "Chapter 3: System Design & Methodology", LocalDate.now().plusWeeks(6));
        milestoneService.create(projectId, "Final Implementation & Testing", LocalDate.now().plusWeeks(10));
    }

    public void rejectProject(String projectId, String reason) {
        Project p = projectDAO.findById(projectId);
        if (p != null) {
            p.setStatus(ProjectStatus.REJECTED);
            projectDAO.update(p);

            Comment comment = new Comment(
                    IdGenerator.generateId(),
                    projectId,
                    Session.getUser().getId(),
                    null,
                    "PROJECT REJECTED: " + reason,
                    LocalDateTime.now()
            );
            commentDAO.save(comment);
        }
    }

    // ==========================================
    // 3. SENIOR SUPERVISOR / ADMIN LOGIC (THE FIX)
    // ==========================================

    /**
     * FIX: This is the method your SeniorSupervisorDashboardController was looking for.
     */
    public List<Project> getAllProjects() {
        return projectDAO.findAll();
    }

    // ==========================================
    // 4. SCORING & UTILITIES
    // ==========================================

    public void scoreProject(String projectId, int totalScore) {
        Scorecard scorecard = new Scorecard(
                IdGenerator.generateId(),
                projectId,
                Session.getUser().getId(),
                Session.getUser().getRole(),
                0, 0, 0, 0,
                totalScore,
                LocalDateTime.now()
        );
        if (!scorecardDAO.save(scorecard)) {
            throw new RuntimeException("Failed to save score");
        }
    }

    public void updateMilestoneStatus(String milestoneId, MilestoneStatus newStatus) {
        milestoneDAO.updateMilestoneStatus(milestoneId, newStatus);
    }

    public void deleteProject(String projectId) {
        projectDAO.deleteProject(projectId);
    }

    public void attachFile(String projectId, String fileName) {
        // Implementation depends on DAO capability
    }

    public void saveProject(@NotNull Project project) {
        if (project.getTitle() == null || project.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Project title is required");
        }
        if (project.getSupervisorId().isEmpty()) {
            throw new IllegalArgumentException("Assigned Supervisor is required");
        }
        project.setStatus(ProjectStatus.PENDING);
        project.setCreatedAt(LocalDateTime.now());
        projectDAO.save(project);
    }
}