package com.capstone.controllers;

import com.capstone.dao.MilestoneDAO;
import com.capstone.models.Milestone;
import com.capstone.models.Comment;
import com.capstone.models.enums.MilestoneStatus;
import com.capstone.services.ProjectService;
import com.capstone.services.CommentService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

public class MilestoneController {
    @FXML private VBox milestoneList; // Matches FXML fx:id="milestoneList"

    private final CommentService commentService = new CommentService();
    private final ProjectService projectService = new ProjectService();
    private final MilestoneDAO milestoneDAO = new MilestoneDAO();

    // Use this method to initialize data based on Project ID
    public void initData(String projectId) {
        // Fetch from DB using DAO or Service
        List<Milestone> milestones = milestoneDAO.findByProject(projectId);
        setMilestones(milestones);
    }

    public void setMilestones(List<Milestone> milestones) {
        milestoneList.getChildren().clear();

        for (Milestone m : milestones) {
            // --- 1. CREATE CARD CONTAINER ---
            VBox cardWrapper = new VBox(10);
            cardWrapper.getStyleClass().add("stat-card");
            cardWrapper.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-border-color: #e0e0e0;");

            HBox mainRow = new HBox(20);
            mainRow.setAlignment(Pos.CENTER_LEFT);

            // --- 2. STATUS INDICATOR ---
            Circle dot = new Circle(10);
            String color = switch (m.getStatus()) {
                case COMPLETED -> "#2ecc71";
                case IN_PROGRESS -> "#3498db";
                default -> "#555555";
            };
            dot.setFill(Color.web(color));

            // --- 3. INFO SECTION ---
            VBox info = new VBox(5);
            Label title = new Label(m.getTitle());
            title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label date = new Label("Deadline: " + m.getDeadline());
            date.setStyle("-fx-text-fill: grey;");
            info.getChildren().addAll(title, date);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button statusBtn = new Button();
            updateButtonStyle(statusBtn, m);

            statusBtn.setOnAction(e -> handleStatusChange(m, dot, statusBtn));

            mainRow.getChildren().addAll(dot, info, spacer, statusBtn);

            // --- 4. COMMENT SECTION ---
            VBox commentBox = new VBox(5);
            commentBox.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; -fx-background-radius: 5;");

            Label commentHeader = new Label("Feedback:");
            commentHeader.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold; -fx-font-size: 11px;");
            commentBox.getChildren().add(commentHeader);

            List<Comment> comments = commentService.getByProject(m.getProjectId());
            if (comments.isEmpty()) {
                Label noComment = new Label("No feedback yet.");
                noComment.setStyle("-fx-text-fill: gray; -fx-font-style: italic; -fx-font-size: 11px;");
                commentBox.getChildren().add(noComment);
            } else {
                for (Comment c : comments) {
                    // FIX: Changed .getContent() to .getMessage()
                    Label cLabel = new Label("💬 " + c.getMessage());
                    cLabel.setStyle("-fx-text-fill: #333; -fx-font-size: 12px;");
                    cLabel.setWrapText(true);
                    commentBox.getChildren().add(cLabel);
                }
            }

            cardWrapper.getChildren().addAll(mainRow, commentBox);
            milestoneList.getChildren().add(cardWrapper);
        }
    }

    private void handleStatusChange(Milestone m, Circle dot, Button btn) {
        if (m.getStatus() == MilestoneStatus.PENDING) {
            m.setStatus(MilestoneStatus.IN_PROGRESS);
            dot.setFill(Color.web("#3498db"));
        } else if (m.getStatus() == MilestoneStatus.IN_PROGRESS) {
            m.setStatus(MilestoneStatus.COMPLETED);
            dot.setFill(Color.web("#2ecc71"));
        }

        projectService.updateMilestoneStatus(m.getId(), m.getStatus());
        updateButtonStyle(btn, m);
    }

    private void updateButtonStyle(Button btn, Milestone m) {
        if (m.getStatus() == MilestoneStatus.COMPLETED) {
            btn.setText("Done");
            btn.setDisable(true);
            btn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        } else {
            btn.setText(m.getStatus() == MilestoneStatus.IN_PROGRESS ? "Finish" : "Start Task");
            btn.setDisable(false);
            btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        }
    }
}