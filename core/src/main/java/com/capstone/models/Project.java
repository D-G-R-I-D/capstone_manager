package com.capstone.models;
import java.time.LocalDateTime;
import com.capstone.models.enums.*;
import com.capstone.models.enums.ProjectStatus;

//public class Project {
//    private String id;
//    private String proposalId;
//    private String studentId;
//    private String supervisorId;
//    private String seniorSupervisorId;
//    private String title;
//    private String description;
//    private ProjectStatus status; // PENDING, APPROVED, REJECTED, IN_PROGRESS, COMPLETED
//    private LocalDateTime createdAt;
//
//    public Project() {}
//
//    public Project(String id, String proposalId, String studentId,
//                   String supervisorId, String seniorSupervisorId,
//                   String title, String description, ProjectStatus status,LocalDateTime createdAt) {
//
//        this.id = id;
//        this.proposalId = proposalId;
//        this.studentId = studentId;
//        this.supervisorId = supervisorId;
//        this.seniorSupervisorId = seniorSupervisorId;
//        this.title = title;
//        this.description = description;
//        this.status = status;
//        this.createdAt = createdAt;
//    }

public class Project {
    private String id;
    private String title;
    private String studentId;
    private String supervisorId;
    private ProjectStatus status;
    private LocalDateTime createdAt;
    private String filePath;


    public Project(String id, String title, String studentId,
                   String supervisorId, ProjectStatus status,
                   LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.studentId = studentId;
        this.supervisorId = supervisorId;
        this.status = status;
        this.createdAt = createdAt;
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


//    public String getDescription() { return description; }
//    public void setDescription(String description) { this.description = description; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getFilePath() { return filePath; }

    public void setFilePath(String filePath) { this.filePath = filePath; }
}


//public class Project {
//    private String id;
//    private String studentId;
//    private String supervisorId;
//    private String seniorSupervisorId;
//    private String title;
//    private String description;
//    private String status; // PENDING, APPROVED, REJECTED, IN_PROGRESS, COMPLETED
//    private Timestamp submittedAt;
//
//    public Project() {}
//
//    public Project(String id, String studentId, String supervisorId, String seniorSupervisorId,
//                   String title, String description, String status, Timestamp submittedAt) {
//        this.id = id;
//        this.studentId = studentId;
//        this.supervisorId = supervisorId;
//        this.seniorSupervisorId = seniorSupervisorId;
//        this.title = title;
//        this.description = description;
//        this.status = status;
//        this.submittedAt = submittedAt;
//    }
//
//    // Getters & Setters
//    public String getId() { return id; }
//    public void setId(String id) { this.id = id; }
//
//    public String getStudentId() { return studentId; }
//    public void setStudentId(String studentId) { this.studentId = studentId; }
//
//    public String getSupervisorId() { return supervisorId; }
//    public void setSupervisorId(String supervisorId) { this.supervisorId = supervisorId; }
//
//    public String getSeniorSupervisorId() { return seniorSupervisorId; }
//    public void setSeniorSupervisorId(String seniorSupervisorId) { this.seniorSupervisorId = seniorSupervisorId; }
//
//    public String getTitle() { return title; }
//    public void setTitle(String title) { this.title = title; }
//
//    public String getDescription() { return description; }
//    public void setDescription(String description) { this.description = description; }

//    public String getStatus() { return status; }
//    public void setStatus(String status) { this.status = status; }
//
//    public Timestamp getSubmittedAt() { return submittedAt; }
//    public void setSubmittedAt(Timestamp submittedAt) { this.submittedAt = submittedAt; }
//}



//public class Project {
//    public enum Status { PROPOSED, APPROVED, IN_PROGRESS, COMPLETED, REJECTED }
//
//    private final String id;
//    private String title;
//    private String description;
//    private Status status = Status.PROPOSED;
//    private final String studentTeamId;
//    private String supervisorId;
//    private final List<Milestone> milestones = new ArrayList<>();
//    private final List<Comment> comments = new ArrayList<>();
//    private ScoreCard score;
//
//    public Project(String id, String title, String description, String studentTeamId){
//        this.id = id;
//        this.title = title;
//        this.description = description;
//        this.studentTeamId = studentTeamId;
//    }
//
//    public String getId(){ return id; }
//
//    public String getTitle(){ return title; }
//
//    public void setTitle(String title){ this.title = title; }
//
//    public String getDescription(){ return description; }
//
//    public void setDescription(String description){ this.description = description; }
//
//    public Status getStatus(){ return status; }
//
//    public void setStatus(Status status){ this.status = status; }
//
//    public String getStudentTeamId(){ return studentTeamId; }
//
//    public String getSupervisorId(){ return supervisorId; }
//
//    public void setSupervisorId(String sup){ this.supervisorId = sup; }
//
//    public List<Milestone> getMilestones(){ return Collections.unmodifiableList(milestones); }
//
//    public void addMilestone(Milestone m){ this.milestones.add(m); }
//
//    public List<Comment> getComments(){ return Collections.unmodifiableList(comments); }
//
//    public void addComment(Comment c){ this.comments.add(c); }
//
//    public Optional<ScoreCard> getScore(){ return Optional.ofNullable(score); }
//
//    public void setScore(ScoreCard s){ this.score = s; }
//}
