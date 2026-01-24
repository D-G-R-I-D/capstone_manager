package com.capstone.controllers;

import com.capstone.models.Project;
import com.capstone.models.User;
import com.capstone.models.enums.ProjectStatus;
import com.capstone.models.enums.Role;
import com.capstone.services.ProjectService;
import com.capstone.services.UserService;
import com.capstone.utils.Session;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class MyProjectsController {

    @FXML private TableView<Project> projectTable;
    @FXML private TableColumn<Project, String> numCol;
    @FXML private TableColumn<Project, String> colName;
    @FXML private TableColumn<Project, ProjectStatus> colStatus; // Kept as ProjectStatus
    @FXML private TableColumn<Project, LocalDateTime> colDate;
    @FXML private TableColumn<Project, Void> colFile;
    @FXML private TableColumn<Project, Void> colAction;

    @FXML private TextField searchField;
    @FXML private Label taskCountLabel;

    private final ObservableList<Project> projectList = FXCollections.observableArrayList();
    private FilteredList<Project> filteredData;
    private final ProjectService projectService = new ProjectService();
    private StudentDashboardController mainController;
    private File selectedFile;

    @FXML
    public void initialize() {
        // 1. Column Mapping
        // Numbering
        numCol.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(projectTable.getItems().indexOf(column.getValue()) + 1 + ""));
        numCol.setSortable(false);
        colName.setCellValueFactory(new PropertyValueFactory<>("title"));
        // FIX: Handle the Enum display correctly
        colStatus.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // 2. Custom Cell Factories
        setupFileColumn();
        setupActionButtonsColumn();

        // 3. Load Data
        refreshData();
        setupSearchFilter();
        projectTable.setItems(filteredData);
    }

    private void setupSearchFilter() {
        // 4. Search Filter Logic
        filteredData = new FilteredList<>(projectList, p -> true);
        searchField.textProperty().addListener((obs, old, newValue) -> {
            filteredData.setPredicate(project -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String filter = newValue.toLowerCase();
                return project.getTitle().toLowerCase().contains(filter) ||
                        project.getStatus().toString().toLowerCase().contains(filter) ||
                        project.getCreatedAt().toString().toLowerCase().contains(filter);
            });
            updateTaskCount();
        });
        projectTable.setItems(filteredData);
    }

    private void refreshData() {
        User student = Session.getUser();
        projectList.setAll(projectService.getProjectsForStudent(student.getId()));
        updateTaskCount();
    }

    private void setupFileColumn() {
        colFile.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("📁");
            {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2c3e50; -fx-cursor: hand; -fx-alignment: CENTER; ");
                btn.setOnAction(e -> openFile(getTableView().getItems().get(getIndex()).getFilePath()));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().get(getIndex()).getFilePath() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void setupActionButtonsColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("View / Timeline");
            private final Button deleteBtn = new Button("Delete");
            private final HBox container = new HBox(10, viewBtn, deleteBtn);
            {
                container.setAlignment(Pos.CENTER);
                viewBtn.getStyleClass().add("view-button");
                deleteBtn.getStyleClass().add("delete-button");

                viewBtn.setOnAction(e -> handleViewProject(getTableView().getItems().get(getIndex())));

                deleteBtn.setOnAction(e -> {
                    Project p = getTableView().getItems().get(getIndex());

                    // Create the Confirmation Alert
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Project");
                    alert.setHeaderText("Are you sure you want to delete: " + p.getTitle() + "?");
                    alert.setContentText("This action cannot be undone.");
                    // Show the dialog and wait for the user to click a button
                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        // 1. Delete from Database
                        projectService.deleteProject(p.getId());

                        // 2. Remove from UI list
                        projectList.remove(p);
                        updateTaskCount();
                        refreshData(); // Refresh list properly
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void updateTaskCount() {
        if(taskCountLabel != null)
            taskCountLabel.setText("Total Projects: " + (filteredData != null ? filteredData.size() : projectList.size()));
    }

    private void openFile(String path) {
        if (path == null) return;
        try {
            File file = new File(path);
            if (file.exists()) java.awt.Desktop.getDesktop().open(file);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleNewProject() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Create New Project");

        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 25; -fx-alignment: center; -fx-background-color: #f4f4f4;");

        TextField titleIn = new TextField(); titleIn.setPromptText("Project Title");
        TextField emailIn = new TextField(); emailIn.setPromptText("Supervisor Email");
        Label fileLabel = new Label("No file attached");

        Button fileBtn = new Button("Attach Document");
        fileBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            selectedFile = fc.showOpenDialog(popup);
            if(selectedFile != null) fileLabel.setText(selectedFile.getName());
        });

        Button saveBtn = new Button("Save Project");
        saveBtn.setOnAction(e -> processProjectCreation(titleIn.getText(), emailIn.getText(), popup));

        root.getChildren().addAll(new Label("New Project Details"), titleIn, emailIn, fileBtn, fileLabel, saveBtn);
        popup.setScene(new Scene(root, 400, 450));
        popup.show();
    }

    private void processProjectCreation(String title, String email, Stage stage) {
        if (title.isEmpty() || email.isEmpty()) return;

        UserService userService = new UserService();
        User supervisor = userService.getUserByEmail(email.trim().toLowerCase());

        if (supervisor != null && supervisor.getRole() == Role.SUPERVISOR) {
            try {
                String savedPath = (selectedFile != null) ? saveFileLocally(selectedFile) : null;
                // Note: Make sure Project constructor matches this signature
                Project newProject = new Project(
                        UUID.randomUUID().toString(),
                        title,
                        Session.getUser().getId(),
                        supervisor.getId(),
                        savedPath, // filePath
                        LocalDateTime.now(),
                        ProjectStatus.PENDING
                );

                projectService.saveProject(newProject);
                refreshData(); // Refresh the list
                stage.close();
                selectedFile = null;
            } catch (Exception ex) { ex.printStackTrace(); }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Supervisor not found or invalid role.");
            alert.show();
        }
    }

    private String saveFileLocally(File file) throws IOException {
        File dir = new File("uploads/projects/");
        if (!dir.exists()) dir.mkdirs();
        File dest = new File(dir, System.currentTimeMillis() + "_" + file.getName());
        Files.copy(file.toPath(), dest.toPath());
        return dest.getAbsolutePath();
    }

    private void handleViewProject(Project selected) {
        if (mainController == null) {
            System.err.println("Main Controller is null!");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/milestones.fxml"));
            Parent root = loader.load();

            MilestoneController controller = loader.getController();
            // FIX: Pass the Project ID to fetch milestones
            controller.initData(selected.getId());

            // FIX: Use mainController to swap view
            mainController.getContentArea().getChildren().setAll(root);

        } catch (IOException ex) { ex.printStackTrace(); }
    }

    public void setMainController(StudentDashboardController controller) {
        this.mainController = controller;
    }
}