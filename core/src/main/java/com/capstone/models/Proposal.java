package com.capstone.models;
import java.time.LocalDateTime;

import com.capstone.models.enums.*;
import com.capstone.models.enums.ProposalStatus;

    public class Proposal {
        private String id;
        private String projectId;          // nullable
        private String title;
        private String summary;
        private String submittedBy;
        private String reviewedBy;
        private ProposalStatus status;
        private boolean approvedBySupervisor;
        private boolean approvedBySenior;
        private LocalDateTime submittedAt;
        private String description;
        private String studentId;
        private String filePath;
        private byte[] fileData;  // Store file bytes


        public Proposal(String id, String submittedBy, String summary,
                        ProposalStatus status, LocalDateTime submittedAt) {
            this.id = id;
            this.submittedBy = submittedBy;
            this.summary = summary;
            this.status = status;
            this.submittedAt = submittedAt;
        }

        public Proposal() {

        }

        // getters + setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

//    public String getSupervisorId() { return supervisorId; }
//    public void setSupervisorId(String supervisorId) { this.supervisorId = supervisorId; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return description; }

    public void setStudentId(String  studentId) { this.studentId = studentId; }
    public String getStudentId() { return studentId; }

    public void setFilePath(String filePath) { this.filePath = filePath; } // or byte[] for real file storage
    public String getFilePath() { return filePath; }

    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public ProposalStatus getStatus() { return status; }
    public void setStatus(ProposalStatus status) { this.status = status; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public String getProjectId() { return this.projectId; }

    public String getReviewedBy() { return reviewedBy; }

    public boolean isApprovedBySupervisor() { return approvedBySupervisor; }

    public void setApprovedBySupervisor(boolean approvedBySupervisor) {
        this.approvedBySupervisor = approvedBySupervisor; }

    public boolean isApprovedBySenior() { return approvedBySenior; }

    public void setApprovedBySenior(boolean approvedBySenior) {
        this.approvedBySenior = approvedBySenior; }

    public void setProjectId(String projectId) { this.projectId = projectId; }

    public void setReviewedBy(String reviewedBy) {  }

    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }

    }