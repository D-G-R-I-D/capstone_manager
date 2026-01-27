package com.capstone.services;
import com.capstone.dao.ProjectDAO;
import com.capstone.dao.ScorecardDAO;
import com.capstone.models.Project;
import com.capstone.models.Scorecard;
import com.capstone.models.enums.ProjectStatus;
import com.capstone.utils.IdGenerator;
import com.capstone.utils.Session;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class ScoreCardService {
    private final ScorecardDAO dao = new ScorecardDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final ScorecardDAO scorecardDAO = new ScorecardDAO();

    public void grade(Scorecard score) {
        score.setId(UUID.randomUUID().toString());
        dao.save(score);
    }

    public void markCompleted(String projectId) {
        Project p = projectDAO.findById(projectId);
        if (p != null) {
            p.setStatus(ProjectStatus.COMPLETED);
            projectDAO.update(p);
        }
    }

    public void scoreProject(@NotNull String projectId, int tech, int problem,
                             int presentation, int design) {
        int totalScore = tech + problem + presentation + design;

        Scorecard scorecard = new Scorecard(
                IdGenerator.generateId(),
                projectId,
                Session.getUser().getId(),
                Session.getUser().getRole(),
                false,
                tech,
                problem,
                presentation,
                design,
                totalScore,
                LocalDateTime.now()
        );
        if (!scorecardDAO.save(scorecard)) {
            throw new RuntimeException("Failed to save scorecard");
        }
    }
}
