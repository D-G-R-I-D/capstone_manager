package com.capstone.controllers;

import com.capstone.models.Project;
import com.capstone.models.User;
import com.capstone.models.enums.Role;
import com.capstone.services.ProjectService;
import com.capstone.services.UserService;
import com.capstone.utils.Guard;
import com.capstone.utils.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.Objects;

public class AdminDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private ListView<String> projectList;
    @FXML private ListView<String> userList;

    private final ProjectService projectService = new ProjectService();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        // 🔐 Role-based guard
        Guard.require(Role.ADMIN);

        // 👤 Session user
        User admin = Session.getUser();
        if (admin == null) {
            throw new IllegalStateException("No session user");
        }

        welcomeLabel.setText("Welcome, " + admin.getUsername());

        // 📁 Projects
        projectService.getAllProjects()
                .forEach(p ->
                projectList.getItems().add(
                        p.getTitle() + " (" + p.getStatus().name() + ")"
                )
        );

        // 👥 Users
        userService.getAllUsers()
                .forEach(u ->
                userList.getItems().add(
                        u.getUsername() + " - " + u.getRole().name()
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
