package com.capstone.controllers;

import com.capstone.javafxui.MainApp;
import com.capstone.models.Project;
import com.capstone.models.User;
import com.capstone.models.enums.ProjectStatus;
import com.capstone.services.CommentService;
import com.capstone.services.ProjectService;
import com.capstone.utils.Session;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class SupervisorDashboardController {

    @FXML private TableColumn<Project, String> numCol;    // Numbering
    @FXML private Label welcomeLabel;
    @FXML private TextField searchField;
    @FXML private TableView<Project> projectTable; // ID must match FXML
    @FXML private TableColumn<Project, String> titleCol;
    @FXML private TableColumn<Project, String> statusCol;
    @FXML private TableColumn<Project, String> studentCol;
    @FXML private TableColumn<Project, Void> fileCol;
    @FXML private TableColumn<Project, Void> actionsCol;

    private final ProjectService projectService = new ProjectService();
    private final CommentService commentService = new CommentService();
    // Data List for Filtering
    private final ObservableList<Project> projectList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        User supervisor = Session.getUser();
        welcomeLabel.setText("Supervisor: " + supervisor.getUsername());

        // 1. Column Mapping
        // Numbering Column Logic
        numCol.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(projectTable.getItems().indexOf(column.getValue()) + 1 + ""));
        numCol.setSortable(false);
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().name()));
        // Note: You might need a method to get student name by ID, currently showing ID
        studentCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        // 2. Setup Actions
        setupFileColumn();
        setupActionButtons();
        // 3. Load Data
        loadData();
        setupSearchFilter();
    }

    private void loadData() {
        User supervisor = Session.getUser();
        projectList.setAll(projectService.getProjectsForSupervisor(supervisor.getId()));
//        projectTable.setItems(projectList);
    }

    private void setupSearchFilter() {
        // Wrap the ObservableList in a FilteredList (initially display all data)
        FilteredList<Project> filteredData = new FilteredList<>(projectList, p -> true);

        // Set the filter Predicate whenever the filter changes.
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(project -> {
                // If filter text is empty, display all projects.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return project.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                        project.getStudentId().toLowerCase().contains(lowerCaseFilter) ||
                         project.getStatus().toString().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Wrap the FilteredList in a SortedList.
        SortedList<Project> sortedData = new SortedList<>(filteredData);
        // Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(projectTable.comparatorProperty());
        // Add sorted (and filtered) data to the table.
        projectTable.setItems(sortedData);
    }

    private void setupFileColumn() {
        fileCol.setCellFactory(param -> new TableCell<>() {
            private final Button fileBtn = new Button("📎"); // Paperclip Icon
            {
                fileBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2c3e50; -fx-font-size: 16px; -fx-cursor: hand;");
                fileBtn.setTooltip(new Tooltip("Open Attached File"));
                fileBtn.setOnAction(e -> {
                    Project p = getTableView().getItems().get(getIndex());
                    openFile(p.getFilePath());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Project p = getTableView().getItems().get(getIndex());
                    // Only show icon if file path exists
                    if (p.getFilePath() != null && !p.getFilePath().isEmpty()) {
                        setGraphic(fileBtn);
                    } else {
                        setGraphic(new Label("-")); // Dash if no file
                    }
                }
            }
        });
    }

    private void setupActionButtons() {
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("✓");
            private final Button rejectBtn = new Button("✕");
            private final Button commentBtn = new Button("💬");
            private final Button scoreBtn = new Button("★"); // The Score Button
            private final HBox containerPending = new HBox(5, approveBtn, rejectBtn, commentBtn);
            private final HBox containerActive = new HBox(5, scoreBtn, commentBtn); // Ac

            {

                // *** THE FIX: CENTER THE BUTTONS ***
                containerPending.setAlignment(Pos.CENTER);
                containerActive.setAlignment(Pos.CENTER);

                approveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-cursor: hand;");
                rejectBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                commentBtn.setStyle("-fx-background-color: #0096c9; -fx-text-fill: white; -fx-cursor: hand;");
                scoreBtn.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
                scoreBtn.setTooltip(new Tooltip("Grade & Complete Project"));

                approveBtn.setOnAction(e -> {
                    Project p = getTableView().getItems().get(getIndex());

                    // Create the Confirmation Alert
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Approve Project");
                    alert.setHeaderText("Confirm Approval of: " + p.getTitle() + "?");
                    alert.setContentText("One time action.");

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        // 1. Delete from Database
                        projectService.approveProject(p.getId());

                        projectService.approveProject(p.getId());
                        loadData();
                    }
                });

                rejectBtn.setOnAction(e -> {
                    Project p = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setHeaderText("Reason for rejection:");
                    dialog.showAndWait().ifPresent(reason -> {
                        // Create the Confirmation Alert
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Reject Project");
                        alert.setHeaderText("Are you sure you want to reject: " + p.getTitle() + "?");
                        alert.setContentText("This action cannot be undone.");
                        Optional<ButtonType> result = alert.showAndWait();

                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            // 1. Delete from Database
                            projectService.rejectProject(p.getId(), reason);
                            loadData();
                        }
                    });
                });

                commentBtn.setOnAction(e -> handleAddComment(getTableView().getItems().get(getIndex())));
                // SCORE ACTION: Opens the popup
                scoreBtn.setOnAction(e -> handleScoreProject(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Project p = getTableView().getItems().get(getIndex());
                    if (p.getStatus() == ProjectStatus.PENDING) {
                        setGraphic(containerPending);
                    }// IF ACTIVE: Show Score Button (The Star)
                    else if (p.getStatus() == ProjectStatus.ACTIVE) {
                        setGraphic(containerActive);
                    } else {
                        // Only show comment button if already active
                        commentBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
                        HBox centerBox = new HBox(commentBtn);
                        centerBox.setAlignment(Pos.CENTER);
                        setGraphic(centerBox);
                    }
                }
            }
        });
    }

    //Helper Method to open the popup
    private void handleScoreProject(Project project) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/score_project.fxml"));
            Parent root = loader.load();

            ScoreProjectController controller = loader.getController();
            controller.setProject(project); // Pass project to popup

            Stage stage = new Stage();
            stage.setTitle("Grade Project: " + project.getTitle());
            stage.setScene(new Scene(root));
            stage.setWidth(250);
            stage.showAndWait(); // Wait for supervisor to finish grading

            loadData(); // Refresh table to show it moved to COMPLETED
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void openFile(String path) {
        if (path == null) return;
        try {
            File file = new File(path);
            if (file.exists()) java.awt.Desktop.getDesktop().open(file);
        } catch (IOException e) { e.printStackTrace(); }
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