package com.capstone.models;
import java.time.LocalDateTime;

public class Comment {
    private String id;
    private String projectId;
    private String authorId;
    private String parentId; // null = top-level comment || // null for main comment
    private String message;
    private LocalDateTime createdAt;

    public Comment() {}

    public Comment(String id, String projectId, String authorId,
                   String parentId, String message, LocalDateTime createdAt) {

        this.id = id;
        this.projectId = projectId;
        this.authorId = authorId;
        this.parentId = parentId;
        this.message = message;
        this.createdAt = createdAt;
    }

    // getters + setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}


//public class Comment {
//    private String id;
//    private String projectId;
//    private String authorId;
//    private String parentId; // null for main comment
//    private String message;
//    private Timestamp createdAt;
//
//    public Comment() {}
//
//    public Comment(String id, String projectId, String authorId, String parentId,
//                   String message, Timestamp createdAt) {
//        this.id = id;
//        this.projectId = projectId;
//        this.authorId = authorId;
//        this.parentId = parentId;
//        this.message = message;
//        this.createdAt = createdAt;
//    }
//
//    // Getters & Setters
//    public String getId() { return id; }
//    public void setId(String id) { this.id = id; }
//
//    public String getProjectId() { return projectId; }
//    public void setProjectId(String projectId) { this.projectId = projectId; }
//
//    public String getAuthorId() { return authorId; }
//    public void setAuthorId(String authorId) { this.authorId = authorId; }
//
//    public String getParentId() { return parentId; }
//    public void setParentId(String parentId) { this.parentId = parentId; }
//
//    public String getMessage() { return message; }
//    public void setMessage(String message) { this.message = message; }
//
//    public Timestamp getCreatedAt() { return createdAt; }
//    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
//}



//public class Comment {
//    private final String id;
//    private final String projectId;
//    private final String authorId;
//    private final String message;
//    private final LocalDateTime createdAt = LocalDateTime.now();
//
//    public Comment(String id, String projectId, String authorId, String message, LocalDateTime createdAt){
//        this.id = id;
//        this.projectId = projectId;
//        this.authorId = authorId;
//        this.message = message;
//    }
//
//    public String getId(){ return id; }
//
//    public String getProjectId(){ return projectId; }
//
//    public String getAuthorId(){ return authorId; }
//
//    public String getMessage(){ return message; }
//
//    public LocalDateTime getCreatedAt(){ return createdAt; }
//}