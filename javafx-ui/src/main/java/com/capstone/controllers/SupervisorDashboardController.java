package com.capstone.controllers;

import com.capstone.javafxui.MainApp;
import com.capstone.models.Project;
import com.capstone.models.User;
import com.capstone.models.enums.Role;
import com.capstone.services.CommentService;
import com.capstone.services.ProjectService;
import com.capstone.utils.Guard;
import com.capstone.utils.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Objects;

public class SupervisorDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<Project> projectTable;
    @FXML private TableColumn<Project, String> titleCol;
    @FXML private TableColumn<Project, String> statusCol;
    @FXML private TableColumn<Project, String> studentCol;
    @FXML private TableColumn<Project, Void> actionsCol;

    private final ProjectService projectService = new ProjectService();
    private final CommentService commentService = new CommentService();

    @FXML
    public void initialize() {
        Guard.require(Role.SUPERVISOR);
        User supervisor = Session.getUser();
        if (supervisor == null) throw new IllegalStateException("No session user");

        welcomeLabel.setText("Supervisor Dashboard: " + supervisor.getUsername());

        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));
        studentCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                // Fetch student name from ID
                "Student " + cellData.getValue().getStudentId()
        ));

        actionsCol.setCellFactory(column -> {
            return new TableCell<Project, Void>() {
                private final Button commentBtn = new Button("Add Comment");
                private final Button scoreBtn = new Button("Score");

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Project project = getTableView().getItems().get(getIndex());
                        commentBtn.setOnAction(event -> addComment(project));
                        scoreBtn.setOnAction(event -> addScore(project));
                        HBox buttons = new HBox(10, commentBtn, scoreBtn);
                        setGraphic(buttons);
                        setText(null);
                    }
                }
            };
        });

        projectTable.setItems(FXCollections.observableArrayList(
                projectService.getProjectsForSupervisor(supervisor.getId())
        ));
    }

//    private void addComment(Project project) {
//        TextInputDialog dialog = new TextInputDialog();
//        dialog.setTitle("Add Comment");
//        dialog.setHeaderText("Add comment to project: " + project.getTitle());
//        dialog.showAndWait().ifPresent(comment -> {
//            // Call service to save comment
//            projectService.addComment(project.getId(), comment);
//            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Comment added!");
//            alert.show();
//        });
//    }

    @FXML
    private void addComment(Project project) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Comment");
        dialog.setHeaderText("Comment on: " + project.getTitle());
        dialog.showAndWait().ifPresent(message -> {
            if (!message.trim().isEmpty()) {
                commentService.add(
                        project.getId(),
                        Session.getUser().getId(),
                        message.trim(),
                        null  // parentId
                );
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Comment added!");
                alert.show();
            }
        });
    }

//    private void addScore(Project project) {
//        TextInputDialog dialog = new TextInputDialog();
//        dialog.setTitle("Score Project");
//        dialog.setHeaderText("Enter score (0-100) for: " + project.getTitle());
//        dialog.showAndWait().ifPresent(scoreStr -> {
//            try {
//                int score = Integer.parseInt(scoreStr);
//                projectService.scoreProject(project.getId(), score);
//                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Score saved!");
//                alert.show();
//            } catch (NumberFormatException e) {
//                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid score");
//                alert.show();
//            }
//        });
//    }

    private void addScore(Project project) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/score_project.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Score Project: " + project.getTitle());
            stage.setScene(new Scene(loader.load()));

            ScoreProjectController controller = loader.getController();
            controller.setProjectId(project.getId());  // Pass project ID

            stage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open scoring form");
            alert.show();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() throws Exception {
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();

        // Save current bounds
        double x = stage.getX();
        double y = stage.getY();
        double width = stage.getWidth();
        double height = stage.getHeight();

        Session.logout();

        MainApp mainApp = (MainApp) stage.getUserData();
        if (mainApp != null) {
            mainApp.showLoginScene();

            // Restore bounds after switch
            stage.setX(x);
            stage.setY(y);
            stage.setWidth(width);
            stage.setHeight(height);
            stage.requestFocus();
            stage.toFront();
        } else {
            // Fallback with bounds restore
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Capstone Manager - Login");

            stage.setX(x);
            stage.setY(y);
            stage.setWidth(width);
            stage.setHeight(height);
            stage.requestFocus();
            stage.toFront();
        }
    }
}