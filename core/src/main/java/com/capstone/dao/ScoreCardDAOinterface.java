package com.capstone.dao;
import com.capstone.models.Scorecard;
import java.util.List;

public interface ScoreCardDAOinterface {
    boolean save(Scorecard scorecard);
    Scorecard findById(String id);
    List<Scorecard> findByProject(String projectId);
    List<Scorecard> findByGrader(String userId);
    void update(Scorecard scorecard);
}
