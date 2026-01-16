package com.capstone.controllers;

import com.capstone.dao.ScorecardDAO;
import com.capstone.models.Scorecard;
import com.capstone.services.ScoreCardService;
import com.capstone.utils.IdGenerator;
import com.capstone.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.time.LocalDateTime;

public class ScoreProjectController {

    @FXML private Slider technicalSlider;
    @FXML private Slider problemSlider;
    @FXML private Slider presentationSlider;
    @FXML private Slider designSlider;
    @FXML private Label totalLabel;

    private String projectId;
    private final ScorecardDAO scorecardDAO = new ScorecardDAO();
    private final ScoreCardService scorecardService = new ScoreCardService();


    // This method is called from SupervisorDashboard when opening the form
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @FXML
    private void initialize() {
        technicalSlider.valueProperty().addListener((obs, old, newVal) -> updateTotal());
        problemSlider.valueProperty().addListener((obs, old, newVal) -> updateTotal());
        presentationSlider.valueProperty().addListener((obs, old, newVal) -> updateTotal());
        designSlider.valueProperty().addListener((obs, old, newVal) -> updateTotal());
        updateTotal();
    }
    // In initialize or on slider change
    private void updateTotal() {
        int total = (int)(technicalSlider.getValue() + problemSlider.getValue() +
                presentationSlider.getValue() + designSlider.getValue());
        totalLabel.setText("Total: " + total + "/100");
    }

    @FXML
    private void saveScore() {
        if (projectId == null || projectId.isEmpty()) {
            totalLabel.setText("Error: No project selected");
            return;
        }

        Scorecard scorecard = new Scorecard(
                IdGenerator.generateId(),
                projectId,
                Session.getUser().getId(),
                Session.getUser().getRole(),  // Role object, not string
                (int)technicalSlider.getValue(),
                (int)problemSlider.getValue(),
                (int)presentationSlider.getValue(),
                (int)designSlider.getValue(),
                Integer.parseInt(totalLabel.getText().split(": ")[1].split("/")[0].trim()),
                LocalDateTime.now()

                /*OR
                Scorecard scorecard = new Scorecard();
                scorecard.setId(IdGenerator.id());
                scorecard.setProjectId(projectId);
                scorecard.setGradedBy(Session.getUser().getId().toString());
                scorecard.setRole(Session.getUser().getRole());
                scorecard.setTechnicalDepth((int) technicalSlider.getValue());
                scorecard.setProblemSolving((int) problemSlider.getValue());
                scorecard.setPresentation((int) presentationSlider.getValue());
                scorecard.setDesign((int) designSlider.getValue());
                scorecard.setTotalScore(Integer.parseInt(totalLabel.getText().split(": ")[1].split("/")[0].trim()));
                scorecard.setCreatedAt(LocalDateTime.now());  // ← THIS IS THE KEY LINE*/
        );


        try {
            scorecardService.grade(scorecard);  // Use your existing grade() method
            totalLabel.setText("Score saved successfully!");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Score saved successfully!");
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save score: " + e.getMessage());
            alert.show();
            e.printStackTrace();
        }
        totalLabel.setText("Score saved successfully!");
    }
}