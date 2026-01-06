package com.capstone.services;
import com.capstone.dao.ScorecardDAO;
import com.capstone.models.Scorecard;

import java.util.UUID;

public class ScoreCardService {
    private final ScorecardDAO dao = new ScorecardDAO();

    public void grade(Scorecard score) {
        score.setId(UUID.randomUUID().toString());
        dao.save(score);
    }
}
