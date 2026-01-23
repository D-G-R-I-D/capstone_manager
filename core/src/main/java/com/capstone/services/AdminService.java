package com.capstone.services;
import com.capstone.ExceptionClass.UserNotFoundException;
import com.capstone.dao.*;
import com.capstone.dao.ProjectDAO;
import com.capstone.dao.ProposalDAO;
import com.capstone.dao.ScorecardDAO;
import com.capstone.dao.UserDAO;
import com.capstone.models.Project;
import com.capstone.models.Proposal;
import com.capstone.models.Scorecard;
import com.capstone.models.User;
import com.capstone.models.enums.ProjectStatus;
import com.capstone.models.enums.ProposalStatus;
import com.capstone.models.enums.Role;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Optional;

public class AdminService {
    private final UserDAO userDAO = new UserDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final ProposalDAO proposalDAO = new ProposalDAO();
    private final ScorecardDAO scorecardDAO = new ScorecardDAO();

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public void changeUserRole(String userId, Role role) {
        Optional<User> userOpt = userDAO.findById(userId);

        User user = userOpt.orElseThrow(() -> new UserNotFoundException("User not found with Id" + userId));

        user.setRole(role);
        userDAO.update(user);
    }

    public List<Project> getAllProjects() {
        return projectDAO.findAll();
    }

    public List<Proposal> getAllProposals() {
        return proposalDAO.findAll();
    }

    public List<Scorecard> getProjectScores(String projectId) {
        return scorecardDAO.findByProject(projectId);
    }

    public List<Scorecard> getAllProjectScores() {
        return scorecardDAO.findAllScores();
    }

    public void forceApproveProposal(String proposalId, String adminId) {
        Proposal p = proposalDAO.findById(proposalId);
        p.setStatus(ProposalStatus.APPROVED);
        p.setReviewedBy(adminId);
        p.setApprovedBySenior(true);
        proposalDAO.update(p);
    }

    public void forceUpdateProjectStatus(String projectId, ProjectStatus status) {
        Project p = projectDAO.findById(projectId);
        p.setStatus(status);
        projectDAO.update(p);
    }

    public void overrideScore(String scoreId, String adminId) {
        Scorecard score = scorecardDAO.findById(scoreId);
        score.setRole(Role.ADMIN);     // Admin override
        score.setGradedBy(adminId);
        scorecardDAO.update(score);
    }

}