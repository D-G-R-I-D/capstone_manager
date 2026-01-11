package com.capstone.controllers;

import com.capstone.dao.ScorecardDAO;
import com.capstone.models.Scorecard;
import com.capstone.utils.IdGenerator;
import com.capstone.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.time.LocalDateTime;

public class ScoreCardFormController {
    ScorecardDAO scorecardDAO = new ScorecardDAO();
    private String projectId;

    @FXML private Slider technicalSlider;
    @FXML private Slider problemSlider;
    @FXML private Slider presentationSlider;
    @FXML private Slider designSlider;
    @FXML
    private Label totalLabel;

    // In initialize or on slider change
    private void updateTotal() {
        int total = (int)(technicalSlider.getValue() + problemSlider.getValue() +
                presentationSlider.getValue() + designSlider.getValue());
        totalLabel.setText("Total: " + total);
    }

    @FXML
    private void saveScore() {
        Scorecard scorecard = new Scorecard(
                IdGenerator.id(),
                projectId,
                Session.getUser().getId(),
                Session.getUser().getRole(),
                (int)technicalSlider.getValue(),
                (int)problemSlider.getValue(),
                (int)presentationSlider.getValue(),
                (int)designSlider.getValue(),
                Integer.parseInt(totalLabel.getText().split(": ")[1]),
                LocalDateTime.now()
        );
        scorecardDAO.save(scorecard);
    }
}