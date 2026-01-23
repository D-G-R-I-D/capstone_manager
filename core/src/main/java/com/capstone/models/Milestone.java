package com.capstone.models;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.capstone.models.enums.MilestoneStatus;

public class Milestone {
    private String id;
    private String projectId;
    private String title;
    private LocalDate deadline;
    private MilestoneStatus status; // NOT_STARTED, IN_PROGRESS, COMPLETED
    private String description;
    private List<Milestone> milestones = new ArrayList<>();
    private boolean completed; // The status field
    private boolean isInProgress;

    public Milestone() {}

    public Milestone(String id, String projectId, String title,
                     LocalDate deadline, MilestoneStatus status) {

        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.deadline = deadline;
        this.status = status;
    }

    // getters + setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public MilestoneStatus getStatus() { return status; }
    public void setStatus(MilestoneStatus status) { this.status = status; }

    public List<Milestone> getMilestones() {
        return milestones;
    }
    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }

    public boolean isCompleted() {
        return this.status == MilestoneStatus.COMPLETED;
    }

    // Optional: A helper to check if it's currently being worked on
    public boolean isInProgress() {
        return this.status == MilestoneStatus.IN_PROGRESS;
    }
}



//public class Milestone {
//    private String id;
//    private String projectId;
//    private String title;
//    private Date deadline;
//    private String status; // NOT_STARTED, IN_PROGRESS, COMPLETED
//
//    public Milestone() {}
//
//    public Milestone(String id, String projectId, String title, Date deadline, String status) {
//        this.id = id;
//        this.projectId = projectId;
//        this.title = title;
//        this.deadline = deadline;
//        this.status = status;
//    }
//
//    // Getters & Setters
//    public String getId() { return id; }
//    public void setId(String id) { this.id = id; }
//
//    public String getProjectId() { return projectId; }
//    public void setProjectId(String projectId) { this.projectId = projectId; }
//
//    public String getTitle() { return title; }
//    public void setTitle(String title) { this.title = title; }
//
//    public Date getDeadline() { return deadline; }
//    public void setDeadline(Date deadline) { this.deadline = deadline; }
//
//    public String getStatus() { return status; }
//    public void setStatus(String status) { this.status = status; }
//}



//public class Milestone {
//    public enum Status { NOT_STARTED, IN_PROGRESS, COMPLETED }
//    private final String id;
//    private String title;
//    private LocalDate deadline;
//    private Status status = Status.NOT_STARTED;
//
//    public Milestone(String id, String title, LocalDate deadline) {
//        this.id = id;
//        this.title = title;
//        this.deadline = deadline;
//    }
//
//    public String getId(){ return id; }
//
//    public String getTitle(){ return title; }
//
//    public void setTitle(String title){ this.title = title; }
//
//    public void setDeadline(LocalDate deadline){ this.deadline = deadline; }
//
//    public LocalDate getDeadline(){ return deadline; }
//
//    public Status getStatus(){ return status; }
//
//    public void setStatus(Status status){ this.status = status; }
//}