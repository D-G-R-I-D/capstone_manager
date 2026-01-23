package com.capstone.models;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.capstone.models.enums.ProjectStatus;

public class Project {
    private String id;
    private String title;
    private String studentId;
    private String supervisorId;
    private ProjectStatus status;
    private LocalDateTime createdAt;
    private String filePath;
    private String description;
    private List<Milestone> milestones;             //= new ArrayList<>();  // Initialize to avoid null


    public Project(String id, String title, String studentId,
                   String supervisorId, String filePath,
                   LocalDateTime createdAt, ProjectStatus status) {
        this.id = id;
        this.title = title;
        this.studentId = studentId;
        this.supervisorId = supervisorId;
        this.filePath = filePath;
        this.createdAt = createdAt;
        this.status = status;
    }

//}


    // getters + setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getSupervisorId() { return supervisorId; }
    public void setSupervisorId(String supervisorId) { this.supervisorId = supervisorId; }

//    public String getSeniorSupervisorId() { return seniorSupervisorId; }
//    public void setSeniorSupervisorId(String seniorSupervisorId) { this.seniorSupervisorId = seniorSupervisorId; }


    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getFilePath() { return filePath; }

    public void setFilePath(String filePath) { this.filePath = filePath; }

    public List<Milestone> getMilestones() {
        return milestones != null ? milestones : Collections.emptyList();
    }

    public void setMilestones() {
    }
}