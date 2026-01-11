package com.capstone.services;

import com.capstone.dao.*;
import com.capstone.models.Comment;
import com.capstone.models.Project;
import com.capstone.models.Scorecard;
import com.capstone.models.enums.ProjectStatus;
import com.capstone.utils.IdGenerator;
import com.capstone.utils.Session;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectService {

    private final ProjectDAO projectDAO = new ProjectDAO();
    private final CommentService commentService = new CommentService();
    private final ScorecardDAO scorecardDAO = new ScorecardDAO();
    private final CommentDAO commentDAO = new CommentDAO();

    public void submitProject(String projectId, String studentId) {
        Project project = projectDAO.findById(projectId);

        if (!project.getStudentId().equals(studentId)) {
            throw new RuntimeException("Unauthorized");
        }

        project.setStatus(ProjectStatus.SUBMITTED);
        projectDAO.update(project);
    }

    public List<Project> getAllProjects() {
        return projectDAO.findAll();
    }

    public void markCompleted(String projectId) {
        Project p = projectDAO.findById(projectId);
        p.setStatus(ProjectStatus.COMPLETED);
        projectDAO.update(p);
    }

    public List<Project> getProjectsForStudent(String studentId) {
        return projectDAO.findByStudent(studentId);
    }

    public List<Project> getProjectsForSupervisor(String supervisorId) {
        return projectDAO.findBySupervisor(supervisorId);
    }

    public List<Project> findByStudent(String studentId) {
        return projectDAO.findByStudent(studentId);
    }


    public void attachFile(String projectId, String fileName) {
        projectDAO.attachFile(projectId, fileName);
    }

    public void addComment(String projectId, String message) {
        Comment comment = new Comment(
                IdGenerator.id(),
                projectId,
                Session.getUser().getId(),
                null,                    // no parent comment
                message,
                LocalDateTime.now()
        );

        if (!commentDAO.save(comment)) {
            throw new RuntimeException("Failed to save comment");
        }
    }

    public void scoreProject(String projectId, int totalScore) {
        Scorecard scorecard = new Scorecard(
                IdGenerator.id(),
                projectId,
                Session.getUser().getId(),
                Session.getUser().getRole(),
                 0,
                0,
                0,
                0,
                totalScore,  // You can expand later
                LocalDateTime.now()
        );
        if (!scorecardDAO.save(scorecard)) {
            throw new RuntimeException("Failed to save score");
        }
    }
}
