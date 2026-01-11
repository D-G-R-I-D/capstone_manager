package com.capstone.services;
import com.capstone.dao.ProposalDAO;
import com.capstone.dao.ProjectDAO;
import com.capstone.models.Proposal;
import com.capstone.models.Project;
import com.capstone.models.enums.ProposalStatus;
import com.capstone.models.enums.ProjectStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProposalService {
    private final ProposalDAO proposalDAO = new ProposalDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();

    // Student submits proposal
    public void submitProposal(Proposal proposal) {
        // Validate
        if (proposal.getTitle() == null || proposal.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Proposal title is required");
        }

        // Set defaults
        proposal.setStatus(ProposalStatus.PENDING);
        proposal.setSubmittedAt(LocalDateTime.now());

        // Save to DB
        proposalDAO.create(proposal);
    }

    // Supervisor approves proposal → creates project
    public void approve(String proposalId, String supervisorId) {

        Proposal p = proposalDAO.findById(proposalId);

        Project project = new Project(
                UUID.randomUUID().toString(),
                "Capstone Project",
                p.getSubmittedBy(),
                supervisorId,
                ProjectStatus.IN_PROGRESS,
                LocalDateTime.now()
        );

        projectDAO.save(project);

        p.setStatus(ProposalStatus.APPROVED);
        p.setReviewedBy(supervisorId);         // ✅ WHO approved
        p.setProjectId(project.getId());
        p.setApprovedBySupervisor(true);

        proposalDAO.update(p);
    }


    public List<Proposal> findByStudent(String studentId) {
        return proposalDAO.findByStudent(studentId);
    }

    public List<Proposal> findPendingBySupervisor(String supervisorId) {
        return proposalDAO.findBySupervisorAndStatus(
                supervisorId, ProposalStatus.PENDING
        );
    }

    public List<Proposal> findApprovedBySupervisor() {
        return proposalDAO.findByStatus(ProposalStatus.APPROVED);
    }

    public List<Proposal> findApprovedBySupervisor(String supervisorId) {
        return proposalDAO.findBySupervisorAndStatus(
                supervisorId,ProposalStatus.APPROVED);
    }

    public List<Proposal> findAll() {
        return proposalDAO.findAll();
    }

    public void seniorApprove(String proposalId, String seniorId) {
        Proposal p = proposalDAO.findById(proposalId);

        p.setApprovedBySenior(true);
        p.setReviewedBy(seniorId);
        p.setStatus(ProposalStatus.APPROVED);

        proposalDAO.update(p);
    }

}
