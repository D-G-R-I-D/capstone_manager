package com.capstone.dao;
import com.capstone.models.Proposal;
import java.util.List;

public interface ProposalDAOinterface {
//    void save(Proposal proposal);
    Proposal findById(String id);
    List<Proposal> findByStudent(String studentId);
    List<Proposal> findByProject(String projectId);
    Proposal findLatestForProject(String projectId);
    List<Proposal> findAll();
    void update(Proposal proposal);
    boolean create(Proposal proposal);
}
