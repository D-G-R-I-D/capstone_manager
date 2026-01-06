package com.capstone.controllers;

import com.capstone.models.Project;
import com.capstone.models.User;
import com.capstone.models.enums.Role;
import com.capstone.services.ProjectService;
import com.capstone.utils.Guard;
import com.capstone.utils.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    private final ProjectService projectService = new ProjectService();

    @FXML
    public void initialize() {
        Guard.require(Role.STUDENT);
        User student = Session.getUser();
        if (student == null) throw new IllegalStateException("No session user");

        welcomeLabel.setText("Welcome, " + student.getUsername() + " (Student)");

        // Real columns (bound to Project properties)
        titleCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTitle()));
        statusCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().name()));

        // Placeholder columns (constant text)
        progressCol.setCellValueFactory(cellData -> new SimpleStringProperty("In Progress"));
        actionsCol.setCellValueFactory(cellData -> new SimpleStringProperty("View Details"));

        // Load data
        projectTable.setItems(FXCollections.observableArrayList(
                projectService.getProjectsForStudent(student.getId())
        ));
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
    private void handleLogout() throws Exception {
        Session.logout();
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(
                getClass().getResource("/fxml/login.fxml")
        ))));
    }

    // Add this method inside the class
    public Callback<TableColumn.CellDataFeatures<Project, String>, ObservableValue<String>> constantStringFactory(String value) {
        return cellData -> new SimpleStringProperty(value);
    }
}