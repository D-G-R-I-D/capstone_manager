package com.capstone.controllers;

import com.capstone.models.Project;
import com.capstone.models.User;
import com.capstone.models.enums.Role;
import com.capstone.services.ProjectService;
import com.capstone.services.UserService;
import com.capstone.utils.Guard;
import com.capstone.utils.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Optional;

public class SeniorSupervisorDashboardController {

    @FXML private Label welcomeLabel;

    @FXML private TableView<Project> projectTable;
    @FXML private TableColumn<Project, String> titleCol;
    @FXML private TableColumn<Project, String> supervisorCol;
    @FXML private TableColumn<Project, String> statusCol;

    private final ProjectService projectService = new ProjectService();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        Guard.require(Role.SENIOR_SUPERVISOR);

        User senior = Session.getUser();
        if (senior == null) {
            throw new IllegalStateException("No session user");
        }
        welcomeLabel.setText("Senior Supervisor: " + senior.getUsername());

        titleCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTitle())
        );

        supervisorCol.setCellValueFactory(cellData -> {
            String supervisorId = cellData.getValue().getSupervisorId();
            if (supervisorId == null || supervisorId.isEmpty()) {
                return new SimpleStringProperty("Unassigned");
            }

            Optional<User> supervisorOpt = userService.findById(supervisorId);
            String supervisorName = supervisorOpt
                    .map(User::getUsername)  // Clean way: if present, get username
                    .orElse("Unassigned");   // if not present or empty

            return new SimpleStringProperty(supervisorName);
        });

        statusCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatus().name())
        );

        projectTable.setItems(
                FXCollections.observableArrayList(
                        projectService.getAllProjects()
                )
        );
    }

    @FXML
    private void handleLogout() throws Exception {
        Session.logout();
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(new Scene(
                FXMLLoader.load(
                        Objects.requireNonNull(
                                getClass().getResource("/fxml/login.fxml")
                        )
                )
        ));
    }
}
