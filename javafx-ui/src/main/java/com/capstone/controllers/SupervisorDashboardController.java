package com.capstone.controllers;

import com.capstone.javafxui.MainApp;
import com.capstone.models.Project;
import com.capstone.models.User;
import com.capstone.models.enums.ProjectStatus;
import com.capstone.services.CommentService;
import com.capstone.services.ProjectService;
import com.capstone.utils.Session;
import javafx.animation.ScaleTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javafx.scene.chart.*;
import com.capstone.dao.ScorecardDAO;
import com.capstone.models.Scorecard;
import javafx.util.Duration;

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
    // Add these fields to the Controller class
    @FXML private Label pendingCountLabel;
    @FXML private Label activeCountLabel;
    @FXML private Label completedCountLabel;
    @FXML private PieChart statusPieChart;
    @FXML private BarChart<String, Number> scoreBarChart;
    @FXML private Circle profileCircle;

    private final ProjectService projectService = new ProjectService();
    private final CommentService commentService = new CommentService();
    // Data List for Filtering
    private final ObservableList<Project> projectList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/profilepic.jpeg")));
        profileCircle.setFill(new ImagePattern(img));
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
        loadCharts();
        setupSearchFilter();
    }

    private void loadCharts() {
        // 1. PIE CHART (Status)
        long pending = projectList.stream().filter(p -> p.getStatus() == ProjectStatus.PENDING).count();
        long active = projectList.stream().filter(p -> p.getStatus() == ProjectStatus.ACTIVE).count();
        long completed = projectList.stream().filter(p -> p.getStatus() == ProjectStatus.COMPLETED).count();
        long rejected = projectList.stream().filter(p -> p.getStatus() == ProjectStatus.REJECTED).count();

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Pending", pending),
                new PieChart.Data("Active", active),
                new PieChart.Data("Completed", completed),
                new PieChart.Data("Rejected", rejected)
        );
        statusPieChart.setData(pieData);
        // Add Colors
        for (PieChart.Data d : statusPieChart.getData()) {
            switch(d.getName()) {
//                case "Pending" -> d.getNode().setStyle("-fx-pie-color: #f1c40f;");
//                case "Active" -> d.getNode().setStyle("-fx-pie-color: #3498db;");
//                case "Completed" -> d.getNode().setStyle("-fx-pie-color: #2ecc71;");
//                case "Rejected" -> d.getNode().setStyle("-fx-pie-color: #e74c3c;");

                case "Active" -> d.getNode().setStyle("-fx-pie-color: #3498db;");    // Blue
                case "Pending" -> d.getNode().setStyle("-fx-pie-color: #f1c40f;");   // Yellow
                case "Completed" -> d.getNode().setStyle("-fx-pie-color: #2ecc71;"); // Green
                case "Rejected" -> d.getNode().setStyle("-fx-pie-color: #e74c3c;");  // Red
            }
        }

        // 2. BAR CHART (Scores)
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Final Grade (/80)");

        ScorecardDAO scorecardDAO = new ScorecardDAO();
        // Iterate through COMPLETED projects
        for (Project p : projectList) {
            if (p.getStatus() == ProjectStatus.COMPLETED) {

                List<Scorecard> scores = scorecardDAO.findByProject(p.getId());
                // You might need a method findByProject in ScorecardDAO
                // For now, let's assume you fetch the score, or we just visualize completion count
                // Simpler Approach for now: Just show project count per status if ScoreDAO is hard
                if (scores != null && !scores.isEmpty()) {
                    Scorecard s = scores.getFirst(); // Get the first scorecard
                    // X-Axis: Student ID, Y-Axis: Total Score
                    series.getData().add(new XYChart.Data<>(p.getStudentId(), s.getTotalScore()));
                } // Placeholder or fetch real score
            }
        }
        scoreBarChart.getData().clear();
        scoreBarChart.getData().add(series);

        for (PieChart.Data data : statusPieChart.getData()) {
            // Listen for when the visual Node is created/re-created
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    handlePieHover();
                    handlePieHoverExit();
                }
            });

            // Also run it immediately in case the node already exists
            if (data.getNode() != null) {
                handlePieHover();
                handlePieHoverExit();
            }
        }
    }

    @FXML
    private void handlePieHover() {
        // 1. Calculate the total sum of all pie slice values
        double total = statusPieChart.getData().stream()
                .mapToDouble(PieChart.Data::getPieValue)
                .sum();
        // Iterate through each data point in your PieChart
        for (PieChart.Data data : statusPieChart.getData()) {
            Node sliceNode = data.getNode();
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), sliceNode);

            // 1. Handle Mouse Hover Entrance
            sliceNode.addEventHandler(MouseEvent.MOUSE_ENTERED, _ -> {
                // 1. Define the Scale Transition
                scaleTransition.setToX(1.1); // Scale to 110% of original size
                scaleTransition.setToY(1.1);
                scaleTransition.playFromStart();
                sliceNode.setStyle("-fx-cursor: hand;"); // Change cursor to a hand pointer
                // Optional: show a tooltip or update a label with the value
//                System.out.println("Hovering over: " + data.getName());

                // Calculate percentage (e.g., 25.5%)
                double percentage = (data.getPieValue() / total) * 100;
                // Create a formatted string for the tooltip
                String tooltipText = String.format("%s: %.1f%%", data.getName(), percentage);
                Tooltip tooltip = new Tooltip(tooltipText);
                // Install it onto the data's node
                Tooltip.install(data.getNode(), tooltip);
                // Optional: Add a listener so the tooltip updates if data values change
                data.pieValueProperty().addListener((obs, oldVal, newVal) ->
//                    tooltip.setText(data.getName() + ": " + newVal)
                                tooltip.setText(tooltipText)
                );
                tooltip.setShowDelay(Duration.millis(200));

                String status = data.getName();
                switch (status) {
                    case "Active" -> data.getNode().setStyle("-fx-pie-color: #3498db;");    // Blue
                    case "Pending" -> data.getNode().setStyle("-fx-pie-color: #f1c40f;");   // Yellow
                    case "Completed" -> data.getNode().setStyle("-fx-pie-color: #2ecc71;"); // Green
                    case "Rejected" -> data.getNode().setStyle("-fx-pie-color: #e74c3c;");  // Red
                }
            });
        }
    }

    @FXML private void handlePieHoverExit() {
        // Iterate through each data point in your PieChart
        for (PieChart.Data data : statusPieChart.getData()) {
            Node sliceNode = data.getNode();
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), sliceNode);

            // 3. Add Mouse Exit Event Handler
            sliceNode.addEventHandler(MouseEvent.MOUSE_EXITED, _ -> {
                scaleTransition.setToX(1.0); // Return to 100% (original size)
                scaleTransition.setToY(1.0);
                scaleTransition.playFromStart();

                String status = data.getName();
                switch (status) {
                    case "Active" -> data.getNode().setStyle("-fx-pie-color: #3498db;");    // Blue
                    case "Pending" -> data.getNode().setStyle("-fx-pie-color: #f1c40f;");   // Yellow
                    case "Completed" -> data.getNode().setStyle("-fx-pie-color: #2ecc71;"); // Green
                    case "Rejected" -> data.getNode().setStyle("-fx-pie-color: #e74c3c;");  // Red
                }
            });
        }
    }

    private void loadData() {
        User supervisor = Session.getUser();
        projectList.setAll(projectService.getProjectsForSupervisor(supervisor.getId()));
//        projectTable.setItems(projectList);
        updateDashboardStats();
    }

    private void updateDashboardStats() {
        long pending = projectList.stream().filter(p -> p.getStatus() == ProjectStatus.PENDING).count();
        long active = projectList.stream().filter(p -> p.getStatus() == ProjectStatus.ACTIVE).count();
        long completed = projectList.stream().filter(p -> p.getStatus() == ProjectStatus.COMPLETED).count();

        if (pendingCountLabel != null) pendingCountLabel.setText(String.valueOf(pending));
        if (activeCountLabel != null) activeCountLabel.setText(String.valueOf(active));
        if (completedCountLabel != null) completedCountLabel.setText(String.valueOf(completed));
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
//                        centerBox.setAlignment(Pos.CENTER);
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