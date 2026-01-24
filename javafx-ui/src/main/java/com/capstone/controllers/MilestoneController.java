package com.capstone.controllers;

import com.capstone.dao.MilestoneDAO;
import com.capstone.models.Milestone;
import com.capstone.models.Comment;
import com.capstone.models.enums.MilestoneStatus;
import com.capstone.services.ProjectService;
import com.capstone.services.CommentService;
import com.capstone.utils.Session;
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
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MilestoneController {
    @FXML private VBox milestoneList; // The Timeline : Matches FXML fx:id="milestoneList"
    @FXML private VBox commentBox;     // NEW: The Feedback Wall
    @FXML private Label projectTitleLabel;

    private final CommentService commentService = new CommentService();
    private final ProjectService projectService = new ProjectService();
    private final MilestoneDAO milestoneDAO = new MilestoneDAO();
    private String currentProjectId;

    // Use this method to initialize data based on Project ID
    public void initData(String projectId) {
        // Fetch from DB using DAO or Service
        List<Milestone> milestones = milestoneDAO.findByProject(projectId);
        setMilestones(milestones);

        // 1. Load Milestones
        refreshMilestones();

        // 2. Load Comments (The Fix)
        refreshComments();
    }

    private void refreshMilestones() {
        milestoneList.getChildren().clear();
        List<Milestone> milestones = milestoneDAO.findByProject(currentProjectId);

        if (milestones.isEmpty()) {
            Label empty = new Label("No milestones created yet. Wait for Supervisor approval.");
            empty.setStyle("-fx-text-fill: grey; -fx-padding: 10;");
            milestoneList.getChildren().add(empty);
            return;
        }

        for (Milestone m : milestones) {
            // Simple Card for the Milestone
            HBox card = new HBox(15);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #eee; -fx-border-radius: 5;");

            Circle dot = new Circle(8);
            dot.setFill(m.getStatus() == MilestoneStatus.COMPLETED ? Color.web("#2ecc71") : Color.web("#bdc3c7"));

            VBox info = new VBox(2);
            Label title = new Label(m.getTitle());
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            Label date = new Label("Due: " + m.getDeadline());
            date.setStyle("-fx-text-fill: grey; -fx-font-size: 12px;");
            info.getChildren().addAll(title, date);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Status Button
            Button actionBtn = getActionBtn(m);

            card.getChildren().addAll(dot, info, spacer, actionBtn);
            milestoneList.getChildren().add(card);
        }
    }

    private @NotNull Button getActionBtn(Milestone m) {
        Button actionBtn = new Button();
        if (m.getStatus() == MilestoneStatus.COMPLETED) {
            actionBtn.setText("Done");
            actionBtn.setDisable(true);
            actionBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        } else {
            actionBtn.setText("Mark Done");
            actionBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
            actionBtn.setOnAction(e -> {
                projectService.updateMilestoneStatus(m.getId(), MilestoneStatus.COMPLETED);
                refreshMilestones(); // Refresh to show green dot
            });
        }
        return actionBtn;
    }

    private void refreshComments() {
        commentBox.getChildren().clear();
        List<Comment> comments = commentService.getByProject(currentProjectId);

        if (comments.isEmpty()) {
            Label noComm = new Label("No feedback from supervisor yet.");
            noComm.setStyle("-fx-text-fill: grey; -fx-font-style: italic;");
            commentBox.getChildren().add(noComm);
        } else {
            for (Comment c : comments) {
                // Determine if it's "Me" or "Supervisor" based on Session
                boolean isMe = c.getAuthorId().equals(Session.getUser().getId());

                VBox bubble = new VBox(5);
                bubble.setMaxWidth(400);

                Label msg = new Label(c.getMessage());
                msg.setWrapText(true);

                Label meta = new Label(c.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")));
                meta.setStyle("-fx-font-size: 10px; -fx-text-fill: #ecf0f1;");

                if (isMe) {
                    bubble.setAlignment(Pos.CENTER_RIGHT);
                    bubble.setStyle("-fx-background-color: #3498db; -fx-padding: 10; -fx-background-radius: 10 10 0 10;");
                    msg.setStyle("-fx-text-fill: white;");
                } else {
                    bubble.setAlignment(Pos.CENTER_LEFT);
                    bubble.setStyle("-fx-background-color: #95a5a6; -fx-padding: 10; -fx-background-radius: 10 10 10 0;");
                    msg.setStyle("-fx-text-fill: white;");
                }

                bubble.getChildren().addAll(msg, meta);

                // Alignment wrapper
                HBox row = new HBox();
                row.setAlignment(isMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
                row.getChildren().add(bubble);

                commentBox.getChildren().add(row);
            }
        }
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