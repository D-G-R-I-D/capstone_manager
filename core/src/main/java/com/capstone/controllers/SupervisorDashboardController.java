package com.capstone.controllers;

import com.capstone.models.Project;
import com.capstone.models.User;
import com.capstone.models.enums.Role;
import com.capstone.services.ProjectService;
import com.capstone.utils.Guard;
import com.capstone.utils.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SupervisorDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<Project> projectTable;
    @FXML private TableColumn<Project, String> titleCol;
    @FXML private TableColumn<Project, String> statusCol;

    private final ProjectService projectService = new ProjectService();

    @FXML
    public void initialize() {
        Guard.require(Role.SUPERVISOR);

        User supervisor = Session.getUser();
        if (supervisor == null) {
            throw new IllegalStateException("No session user");
        }
        welcomeLabel.setText("Supervisor: " + supervisor.getUsername());

        titleCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));
        statusCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus().name()));

        projectTable.setItems(
                FXCollections.observableArrayList(
                        projectService.getProjectsForSupervisor(supervisor.getId())
                )
        );
    }
}
