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
}


//public class Proposal {
//
//    public enum Status { PENDING, SUBMITTED, APPROVED, REJECTED }
//    private final String id;
//    private final String projectId;
//    private final String summary;
//    private final String submittedBy;
//    private Status status = Status.PENDING;
//    private final LocalDateTime submittedAt;
//    private LocalDateTime reviewedAt;
//    private String reviewedBy;
//
//    private String decisionNote; // optional text the supervisor might add
//    private String rejectionNote;
//
//    public Proposal(String id, String projectId, String summary, String submittedBy) {
//        this.id = id;
//        this.projectId = projectId;
//        this.summary = summary;
//        this.submittedBy = submittedBy;
//        this.reviewedAt = null;
//        this.reviewedBy = null;
//        this.submittedAt = LocalDateTime.now();
//    }
//
//    public String getId(){ return id; }
//
//    public String getProjectId(){ return projectId; }
//
//    public String getSummary(){ return summary; }
//
//    public String getSubmittedBy(){ return submittedBy; }
//
//    public Status getStatus(){ return status; }
//
//    public void setStatus(Status status){ this.status = status; }
//
//    public LocalDateTime getSubmittedAt() {
//        return submittedAt;
//    }
//
//    public LocalDateTime getReviewedAt() {
//        return reviewedAt;
//    }
//
//    public String getReviewerId() {
//        return reviewedBy;
//    }
//
//    public void setReviewedBy(String reviewedBy) {
//        this.reviewedBy = this.reviewedBy;
//    }
//
//
//    public String getRejectionNote() { return rejectionNote; }
//
//    public void approve(String supervisorId, String note) {
//        this.status = Status.APPROVED;
//        this.decisionNote = note;
//        this.reviewedBy = supervisorId;
//        this.reviewedAt = LocalDateTime.now();
//    }
//
//    public void approve(String supervisorId) {
//        approve(supervisorId, null);
//    }
//
//    public void reject(String supervisorId, String note) {
//        this.status = Status.REJECTED;
//        this.rejectionNote = note;
//        this.reviewedBy = supervisorId;
//        this.reviewedAt = LocalDateTime.now();
//    }
//
//    public void reject(String supervisorId) {
//        reject(supervisorId, null);
//    }
//
//    public String getDecisionNote(){ return decisionNote; }
//
//    @Override
//    public String toString() {
//        return "Proposal{" +
//                "id='" + id + '\'' +
//                ", projectId='" + projectId + '\'' +
//                ", status=" + status +
//                ", submittedBy='" + submittedBy + '\'' +
//                ", reviewedBy='" + reviewedBy + '\'' +
//                ", summary='" + summary + '\'' +
//                '}';
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id);
//    }
//

//    public Proposal withStatus(Proposal.Status status) {
//        return new Proposal(this.id(), this.projectId(), this.summary(), this.studentId(),
//                status, this.submittedAt(), this.reviewedAt(), this.reviewedBy());
//    }
//
//    public Proposal withReviewedBy(String reviewerId) {
//        return new Proposal(this.id(), this.projectId(), this.summary(), this.studentId(),
//                this.status(), this.submittedAt(), this.reviewedAt(), reviewerId);
//    }
//
//    public Proposal withReviewedAt(LocalDateTime time) {
//        return new Proposal(this.id(), this.projectId(), this.summary(), this.studentId(),
//                this.status(), this.submittedAt(), time, this.reviewedBy());
//    }


//}
