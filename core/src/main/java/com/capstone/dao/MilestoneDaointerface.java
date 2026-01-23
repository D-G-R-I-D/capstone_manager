package com.capstone.dao;
import com.capstone.models.Milestone;
import java.util.List;

public interface MilestoneDaointerface {
    boolean save(Milestone milestone);
    Milestone findById(String id);
    List<Milestone> findByProject(String projectId);
    void update(Milestone milestone);
    void delete(String id);
}