package com.capstone.controllers;

import com.capstone.models.Project;
import com.capstone.models.enums.ProjectStatus;
import com.capstone.services.ProjectService;
import com.capstone.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.List;

public class DashboardHomeController {

    @FXML private VBox rejectedBox;
    @FXML private VBox activeBox;
    @FXML private VBox pendingBox;
    @FXML private VBox completedBox;
    @FXML private Label welcomeLabel;
    @FXML private Label activeCountLabel;
    @FXML private Label pendingCountLabel;
    @FXML private Label completedCountLabel;
    @FXML private Label rejectedCountLabel;
    private final ProjectService projectService = new ProjectService();

    @FXML
    public void initialize() {
        String username = Session.getUser().getUsername();
        welcomeLabel.setText("Welcome back, " + username + "!");

        // Load Real Stats
        List<Project> myProjects = projectService.getProjectsForStudent(Session.getUser().getId());

        long active = myProjects.stream().filter(p -> p.getStatus() == ProjectStatus.ACTIVE).count();
        long pending = myProjects.stream().filter(p -> p.getStatus() == ProjectStatus.PENDING).count();
        long completed = myProjects.stream().filter(p -> p.getStatus() == ProjectStatus.COMPLETED).count();
        long rejected = myProjects.stream().filter(p -> p.getStatus() == ProjectStatus.REJECTED).count();

        activeCountLabel.setText(String.valueOf(active));
        pendingCountLabel.setText(String.valueOf(pending));
        completedCountLabel.setText(String.valueOf(completed));
        rejectedCountLabel.setText(String.valueOf(rejected));
    }

    @FXML
    private void handleHover(MouseEvent event) {
        VBox source = (VBox) event.getSource();
        String currentStyle = source.getStyle();

        // Change the 0.4 opacity to 0.7 to make it look highlighted
        source.setStyle(currentStyle.replace("0.7)", "0.9)"));

        // Optional: Make it lift slightly
        source.setTranslateY(-5);
    }

    @FXML
    private void handleExit(MouseEvent event) {
        VBox source = (VBox) event.getSource();
        String currentStyle = source.getStyle();

        // Change it back to 0.4
        source.setStyle(currentStyle.replace("0.9)", "0.7)"));

        // Reset position
        source.setTranslateY(0);
    }
}