package com.capstone.services;

import com.capstone.dao.ProjectDAO;
import com.capstone.models.Project;
import com.capstone.models.enums.ProjectStatus;

import java.util.List;

public class ProjectService {

    private final ProjectDAO projectDAO = new ProjectDAO();

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

}
