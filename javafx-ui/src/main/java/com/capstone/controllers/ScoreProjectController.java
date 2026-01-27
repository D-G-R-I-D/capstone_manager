package com.capstone.controllers;

import com.capstone.models.Project;
import com.capstone.services.ProjectService;
import com.capstone.services.ScoreCardService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

public class ScoreProjectController {

    @FXML private Label projectTitleLabel;
    @FXML private Slider technicalSlider;
    @FXML private Slider problemSlider;
    @FXML private Slider presentationSlider;
    @FXML private Slider designSlider;
    @FXML private Label totalLabel;
    @FXML private Button saveBtn;

    private Project currentProject;
    private final ProjectService projectService = new ProjectService();

    @FXML
    public void initialize() {
        // Listeners to update the total score in real-time
        technicalSlider.valueProperty().addListener((obs, old, val) -> updateTotal());
        problemSlider.valueProperty().addListener((obs, old, val) -> updateTotal());
        presentationSlider.valueProperty().addListener((obs, old, val) -> updateTotal());
        designSlider.valueProperty().addListener((obs, old, val) -> updateTotal());
    }

    public void setProject(Project project) {
        this.currentProject = project;
        projectTitleLabel.setText("Grading: " + project.getTitle());
    }

    private void updateTotal() {
        int total = (int) (technicalSlider.getValue() + problemSlider.getValue() +
                presentationSlider.getValue() + designSlider.getValue());
        totalLabel.setText("Total Score: " + total + " / 80");
    }

    @FXML
    private void handleSave() {
        if (currentProject == null) return;

        // 1. Get Individual Values
        int techVal = (int) technicalSlider.getValue();
        int probVal = (int) problemSlider.getValue();
        int presVal = (int) presentationSlider.getValue();
        int desVal  = (int) designSlider.getValue();

//        int total = techVal + probVal + presVal + desVal;

        ScoreCardService scoreCardService = new ScoreCardService();
        // 1. Mark Project as COMPLETED
        scoreCardService.markCompleted(currentProject.getId());
        // 2. Save Score
        scoreCardService.scoreProject(
                currentProject.getId(),
                techVal,
                probVal,
                presVal,
                desVal
        );
        // 3. Close Window
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }
}