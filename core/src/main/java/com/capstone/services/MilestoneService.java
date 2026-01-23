package com.capstone.services;
import com.capstone.dao.MilestoneDAO;
import com.capstone.models.Milestone;
import com.capstone.models.enums.MilestoneStatus;

import java.time.LocalDate;
import java.util.UUID;

public class MilestoneService {
    private final MilestoneDAO dao = new MilestoneDAO();

    public void create(String projectId, String title, LocalDate deadline) {
        Milestone m = new Milestone(
                UUID.randomUUID().toString(),
                projectId,
                title,
                deadline,
                MilestoneStatus.PENDING
        );
        dao.save(m);
    }

}