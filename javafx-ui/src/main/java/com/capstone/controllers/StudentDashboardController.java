package com.capstone.controllers;

import com.capstone.javafxui.MainApp;
import com.capstone.models.Project;
import com.capstone.models.User;
import com.capstone.models.enums.MilestoneStatus;
import com.capstone.models.enums.ProjectStatus;
import com.capstone.models.enums.Role;
import com.capstone.services.ProjectService;
import com.capstone.utils.Guard;
import com.capstone.utils.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Objects;

public class StudentDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<Project> projectTable;
    @FXML private TableColumn<Project, String> titleCol;
    @FXML private TableColumn<Project, String> statusCol;
    @FXML private TableColumn<Project, String> progressCol;  // New
    @FXML private TableColumn<Project, String> actionsCol;    // New
    @FXML private Label activeProjectsLabel; // New
    @FXML private Label pendingMilestonesLabel; // New
    @FXML private Label completionRateLabel; // New
    @FXML private ProgressBar completionBar;

    private final ProjectService projectService = new ProjectService();

    @FXML
    public void initialize() {
        Guard.require(Role.STUDENT);
        User student = Session.getUser();
        if (student == null) {
            throw new IllegalStateException("No session user");
        }

        welcomeLabel.setText("WELCOME, " + student.getUsername().toUpperCase() + " (STUDENT)");

////        set like hover
//        welcomeLabel.setOnMouseEntered(_ ->
//                welcomeLabel.setStyle("""
//                                      -fx-font-family: 'Segoe UI'; -fx-font-size: 20px;
//                                      -fx-font-weight: bold; -fx-text-fill: #0d131f;
//                                      -fx-cursor: hand; -fx-scale-x: 1.05;
//                                      -fx-scale-y: 1.05;
//                                      """ /* Subtle zoom effect */
//        ));
//
//        welcomeLabel.setOnMouseExited(_ ->
//                // Return to the original "static" look
//                welcomeLabel.setStyle("""
//                                      -fx-font-family: 'Segoe UI'; -fx-font-size: 20px;
//                                      -fx-font-weight: bold; fx-cursor: default;
//                                      """
//                ));

        // Set up table columns
        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));
        progressCol.setCellValueFactory(cellData -> new SimpleStringProperty("In Progress")); // placeholder
        actionsCol.setCellValueFactory(cellData -> new SimpleStringProperty("View Details")); // placeholder

        // Load student's projects
        // Load projects
        ObservableList<Project> projects = FXCollections.observableArrayList(
                projectService.getProjectsForStudent(student.getId())
        );
        projectTable.setItems(projects);

        // Real active projects
        int activeCount = (int) projects.stream()
                .filter(p -> p.getStatus() == ProjectStatus.IN_PROGRESS || p.getStatus() == ProjectStatus.PENDING)
                .count();
        activeProjectsLabel.setText(String.valueOf(activeCount));

        // Real pending milestones
        long pendingMilestones = projects.stream()
                .flatMap(p -> p.getMilestones().stream())
                .filter(m -> m.getStatus() == MilestoneStatus.PENDING || m.getStatus() == MilestoneStatus.IN_PROGRESS)
                .count();
        pendingMilestonesLabel.setText(String.valueOf(pendingMilestones));

        // Real completion rate
        long totalMilestones = projects.stream()
                .mapToLong(p -> p.getMilestones().size())
                .sum();
        long completed = projects.stream()
                .flatMap(p -> p.getMilestones().stream())
                .filter(m -> m.getStatus() == MilestoneStatus.COMPLETED)
                .count();
        double completion = totalMilestones == 0 ? 0 : (completed * 100.0 / totalMilestones);
        completionRateLabel.setText(String.format("%.0f%%", completion));
        completionBar.setProgress(completion / 100.0);  // Set the progress bar
    }

    @FXML
    private void submitProposal() throws Exception {
        // Load proposal form
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(
                getClass().getResource("/fxml/proposal_form.fxml")
        ))));
    }

    @FXML
    private void viewMilestones() throws Exception {
        // Load milestones view
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(
                getClass().getResource("/fxml/milestones.fxml")
        ))));
    }

    @FXML
    private void viewGrades() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("My Grades");
        alert.setHeaderText("Grades & Feedback");
        alert.setContentText("Your grades and supervisor feedback will appear here once available.");
        alert.showAndWait();
        // Later: open a grades table or scorecard view
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

    // Add this method inside the class
    public Callback<TableColumn.CellDataFeatures<Project, String>, ObservableValue<String>> constantStringFactory(String value) {
        return cellData -> new SimpleStringProperty(value);
    }

    private void showProjectDetails(Project project) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/project_details.fxml.fxml"));
            Stage detailsStage = new Stage();
            detailsStage.setTitle("Project Details: " + project.getTitle());
            detailsStage.setScene(new Scene(loader.load()));
            ProjectDetailsController controller = loader.getController();
            controller.setProject(project);
            detailsStage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load project details");
            alert.show();
            e.printStackTrace();
        }
    }
}