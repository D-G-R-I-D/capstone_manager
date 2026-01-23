package com.capstone.controllers;

import com.capstone.javafxui.MainApp;
import com.capstone.models.Project;
import com.capstone.models.User;
import com.capstone.models.enums.ProjectStatus;
import com.capstone.services.CommentService;
import com.capstone.services.ProjectService;
import com.capstone.utils.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SupervisorDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<Project> projectTable; // ID must match FXML
    @FXML private TableColumn<Project, String> titleCol;
    @FXML private TableColumn<Project, String> statusCol;
    @FXML private TableColumn<Project, String> studentCol;
    @FXML private TableColumn<Project, Void> actionsCol;

    private final ProjectService projectService = new ProjectService();
    private final CommentService commentService = new CommentService();

    @FXML
    public void initialize() {
        User supervisor = Session.getUser();
        welcomeLabel.setText("Supervisor: " + supervisor.getUsername());

        // 1. Column Mapping
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().name()));
        // Note: You might need a method to get student name by ID, currently showing ID
        studentCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        // 2. Setup Actions
        setupActionButtons();

        // 3. Load Data
        loadData();
    }

    private void loadData() {
        User supervisor = Session.getUser();
        projectTable.setItems(FXCollections.observableArrayList(
                projectService.getProjectsForSupervisor(supervisor.getId())
        ));
    }

    private void setupActionButtons() {
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("✓");
            private final Button rejectBtn = new Button("✕");
            private final Button commentBtn = new Button("💬");
            private final HBox container = new HBox(5, approveBtn, rejectBtn, commentBtn);

            {
                approveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                rejectBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                commentBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

                approveBtn.setOnAction(e -> {
                    Project p = getTableView().getItems().get(getIndex());
                    projectService.approveProject(p.getId());
                    loadData();
                });

                rejectBtn.setOnAction(e -> {
                    Project p = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setHeaderText("Reason for rejection:");
                    dialog.showAndWait().ifPresent(reason -> {
                        projectService.rejectProject(p.getId(), reason);
                        loadData();
                    });
                });

                commentBtn.setOnAction(e -> handleAddComment(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Project p = getTableView().getItems().get(getIndex());
                    if (p.getStatus() == ProjectStatus.PENDING) {
                        setGraphic(container);
                    } else {
                        // Only show comment button if already active
                        setGraphic(commentBtn);
                    }
                }
            }
        });
    }

    private void handleAddComment(Project project) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Feedback");
        dialog.setHeaderText("Add comment for: " + project.getTitle());
        dialog.showAndWait().ifPresent(msg -> {
            if (!msg.trim().isEmpty()) {
                // Pass correct params: projectID, authorID, message, parentID (null)
                commentService.add(project.getId(), Session.getUser().getId(), msg.trim(), null);
                new Alert(Alert.AlertType.INFORMATION, "Comment sent!").show();
            }
        });
    }

    @FXML
    public void handleLogout() throws Exception {
        Session.logout();
        try {
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();

            // Save current window position and size
            double x = stage.getX();
            double y = stage.getY();
            double width = stage.getWidth();
            double height = stage.getHeight();

            MainApp mainApp = (MainApp) stage.getUserData();
            if (mainApp != null) {
                mainApp.showLoginScene();

                // Restore after switch
                stage.setX(x);
                stage.setY(y);
                stage.setWidth(width);
                stage.setHeight(height);
            } else {
                stage = (Stage) welcomeLabel.getScene().getWindow();
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/login.fxml")));
                stage.setScene(new Scene(root));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}