package com.capstone.repository;

import com.capstone.models.Proposal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ProposalRepository {
    // Store proposals in memory (you can later replace this with database logic)
    public final Map<String, Proposal> proposals = new ConcurrentHashMap<>();

    /**
     * Save or update a proposal.
     */
    public Proposal save(Proposal p) {
        proposals.put(p.getId(), p);
        return p;
    }

    /**
     * Find a proposal by ID.
     */
    public Optional<Proposal> findById(String id) {
        return Optional.ofNullable(proposals.get(id));
    }

    /**
     * Find all proposals.
     */
    public List<Proposal> findAll() {
        return new ArrayList<>(proposals.values());
    }

    /**
     * Get all proposals for a specific project.
     */
    public List<Proposal> findByProjectId(String projectId) {
        return proposals.values().stream()
                .filter(p -> p.getProjectId().equals(projectId))
                .sorted(Comparator.comparing(Proposal::getSubmittedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Return the most recent proposal for a specific project.
     */
    public Optional<Proposal> findLatestByProjectId(String projectId) {
        return proposals.values().stream()
                .filter(p -> p.getProjectId().equals(projectId))
                .max(Comparator.comparing(Proposal::getSubmittedAt));
    }

    /**
     * Delete a proposal.
     */
    public void delete(String id) {
        proposals.remove(id);
    }

}
