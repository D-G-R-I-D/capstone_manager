package com.capstone.controllers;

import com.capstone.models.Project;
import com.capstone.models.User;
import com.capstone.models.enums.Role;
import com.capstone.services.ProjectService;
import com.capstone.utils.Guard;
import com.capstone.utils.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Objects;

public class StudentDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<Project> projectTable;
    @FXML private TableColumn<Project, String> titleCol;
    @FXML private TableColumn<Project, String> statusCol;

    private final ProjectService projectService = new ProjectService();

    @FXML
    public void initialize() {
        Guard.require(Role.STUDENT);

        User student = Session.getUser();
        if (student == null) {
            throw new IllegalStateException("No session user");
        }
        welcomeLabel.setText("Welcome, " + student.getUsername());

        titleCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));
        statusCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus().name()));


        projectTable.setItems(
                FXCollections.observableArrayList(
                        projectService.getProjectsForStudent(student.getId())
                )
        );
    }

    @FXML
    private void handleLogout() throws Exception {
        Session.logout();
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(new Scene(
                FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/login.fxml")))
        ));
    }
}
