package com.capstone.models;
import com.capstone.models.enums.*;
import com.capstone.models.enums.Role;

import java.time.LocalDateTime;

public class Scorecard {
    private String id;
    private String projectId;
    private String gradedBy;
    private Role role; // SUPERVISOR, SENIOR_SUPERVISOR, ADMIN
    private boolean override;


    private int technicalDepth;
    private int problemSolving;
    private int presentation;
    private int design;
    private int totalScore;

    private LocalDateTime gradedAt;

    public Scorecard(String id, String projectId, String gradedBy, Role role,
                     int technicalDepth, int problemSolving, int presentation,
                     int design, int totalScore, LocalDateTime gradedAt) {

        this.id = id;
        this.projectId = projectId;
        this.gradedBy = gradedBy;
        this.role = role;
        this.technicalDepth = technicalDepth;
        this.problemSolving = problemSolving;
        this.presentation = presentation;
        this.design = design;
        this.totalScore = totalScore;
        this.gradedAt = gradedAt;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getGradedBy() { return gradedBy; }
    public void setGradedBy(String gradedBy) { this.gradedBy = gradedBy; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public int getTechnicalDepth() { return technicalDepth; }
    public void setTechnicalDepth(int technicalDepth) { this.technicalDepth = technicalDepth; }

    public int getProblemSolving() { return problemSolving; }
    public void setProblemSolving(int problemSolving) { this.problemSolving = problemSolving; }

    public int getPresentation() { return presentation; }
    public void setPresentation(int presentation) { this.presentation = presentation; }

    public int getDesign() { return design; }
    public void setDesign(int design) { this.design = design; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public LocalDateTime getGradedAt() { return gradedAt; }
    public void setGradedAt(LocalDateTime gradedAt) { this.gradedAt = gradedAt; }

    public void setOverride(boolean b) {b = true; }
}
