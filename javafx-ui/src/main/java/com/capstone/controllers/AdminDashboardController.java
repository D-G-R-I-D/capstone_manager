package com.capstone.controllers;

import com.capstone.dao.UserDAO;
import com.capstone.javafxui.MainApp;
import com.capstone.models.Project;
import com.capstone.models.User;
import com.capstone.models.enums.ProjectStatus;
import com.capstone.models.enums.Role;
import com.capstone.services.ProjectService;
import com.capstone.services.UserService;
import com.capstone.utils.Guard;
import com.capstone.utils.Session;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class AdminDashboardController {
    private static final Logger LOGGER = Logger.getLogger(AdminDashboardController.class.getName());
    @FXML private Circle profileCircle;
    @FXML private Label welcomeLabel;
    @FXML private TextField searchUserField;
    @FXML private Button btnSettings;
    @FXML private TextField searchProjectField;
    @FXML private Label roleLabel;

    @FXML private Label pageTitle;
    @FXML private Label adminNameLabel;

    // Stats
    @FXML private Label totalUsersLabel;
    @FXML private Label activeProjectsLabel;
    @FXML private Label pendingLabel;
    @FXML private Label rejectedLabel;
    @FXML private Label completedLabel;

    // Views
    @FXML private VBox usersView;
    @FXML private VBox projectsView;
    @FXML private Button btnUsers;
    @FXML private Button btnProjects;

    // User Table
    @FXML private TableColumn<User, String> UnumCol;    // Numbering
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> userIdCol;
    @FXML private TableColumn<User, String> userNameCol;
    @FXML private TableColumn<User, String> userEmailCol;
    @FXML private TableColumn<User, String> userRoleCol;

    // Project Table
    @FXML private TableColumn<Project, String> PnumCol;    // Numbering
    @FXML private TableView<Project> projectTable;
    @FXML private TableColumn<Project, String> projTitleCol;
    @FXML private TableColumn<Project, String> projStudentCol;
    @FXML private TableColumn<Project, String> projStatusCol;

    private final UserDAO userDAO = new UserDAO();
    UserService userService = new UserService();
    private final ProjectService projectService = new ProjectService();
    // Store master lists to filter against
    private final ObservableList<User> masterUserList = FXCollections.observableArrayList();
    private final ObservableList<Project> masterProjectList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        Guard.require(Role.ADMIN);
//        if(Session.getUser() != null) roleLabel.setText(Session.getUser().getUsername());
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/profilepic.jpeg")));
        profileCircle.setFill(new ImagePattern(img));

        // Setup Tables
        // Numbering Column Logic
        UnumCol.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(userTable.getItems().indexOf(column.getValue()) + 1 + ""));
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        userEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        userRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Numbering Column Logic
        PnumCol.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(projectTable.getItems().indexOf(column.getValue()) + 1 + ""));
        projTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        projStudentCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        projStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        refreshMasterData();
        setupUserFilter();
        setupProjectFilter();
        loadData();
    }

    private void loadData() {
        // 1. Load Lists
        List<User> users = userService.getAllUsers();
        List<Project> projects = projectService.getAllProjects(); // Ensure this method exists

//        userTable.setItems(FXCollections.observableArrayList(users));
//        projectTable.setItems(FXCollections.observableArrayList(projects));

        // 2. Update Stats Cards
        totalUsersLabel.setText(String.valueOf(users.size()));
        activeProjectsLabel.setText(String.valueOf(projects.stream().filter(p -> p.getStatus() == ProjectStatus.ACTIVE).count()));
        pendingLabel.setText(String.valueOf(projects.stream().filter(p -> p.getStatus() == ProjectStatus.PENDING).count()));
        rejectedLabel.setText(String.valueOf(projects.stream().filter(p -> p.getStatus() == ProjectStatus.REJECTED).count()));
        completedLabel.setText(String.valueOf(projects.stream().filter(p -> p.getStatus() == ProjectStatus.COMPLETED).count()));
    }

    private void refreshMasterData() {
        masterUserList.setAll(userService.getAllUsers());
        masterProjectList.setAll(projectService.getAllProjects());
    }

    // --- FILTER LOGIC FOR USERS ---
    private void setupUserFilter() {
        // 1. Wrap the ObservableList in a FilteredList (initially show all)
        FilteredList<User> filteredUsers = new FilteredList<>(masterUserList, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        searchUserField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredUsers.setPredicate(user -> {
                // If filter text is empty, display all users.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return user.getUsername().toLowerCase().contains(lowerCaseFilter) ||
                        user.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                        user.getRole().toString().toLowerCase().contains(lowerCaseFilter);// Does not match.
            });
        });

        // 3. Wrap in SortedList
        SortedList<User> sortedUsers = new SortedList<>(filteredUsers);
        sortedUsers.comparatorProperty().bind(userTable.comparatorProperty());

        // 4. Add to Table
        userTable.setItems(sortedUsers);
    }

    // --- FILTER LOGIC FOR PROJECTS ---
    private void setupProjectFilter() {
        FilteredList<Project> filteredProjects = new FilteredList<>(masterProjectList, p -> true);

        searchProjectField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProjects.setPredicate(project -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();

                return project.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                        project.getStudentId().toLowerCase().contains(lowerCaseFilter) ||
                        project.getStatus().toString().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Project> sortedProjects = new SortedList<>(filteredProjects);
        sortedProjects.comparatorProperty().bind(projectTable.comparatorProperty());

        projectTable.setItems(sortedProjects);
    }

    // ... (Rest of your Delete/Switch View logic) ...


    @FXML
    private void switchView(ActionEvent event) {
        // Simple View Switcher Logic
        Button clicked = (Button) event.getSource();

        // Reset Styles
        btnUsers.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 10;");
        btnProjects.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 10;");
        btnSettings.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 10;");

        // Set Active Style
        clicked.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 10;");

        if (clicked == btnUsers) {
            usersView.setVisible(true);
            projectsView.setVisible(false);
            pageTitle.setText("User Management");
        } else if (clicked == btnProjects) {
            usersView.setVisible(false);
            projectsView.setVisible(true);
            pageTitle.setText("All Projects Overview");
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Confirm Dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete user " + selected.getUsername() + "? This cannot be undone.", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                 userDAO.deleteUser(selected.getId());
                loadData();
            }
        }
    }

    @FXML
    public void handleLogout() throws Exception {
        Session.logout();
        try {
            Stage stage = (Stage) adminNameLabel.getScene().getWindow();

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
                stage = (Stage) adminNameLabel.getScene().getWindow();
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/login.fxml")));
                stage.setScene(new Scene(root));
            }
        } catch (Exception e) {
            LOGGER.severe("Could not handle logout" + e.getMessage());
        }
    }
}