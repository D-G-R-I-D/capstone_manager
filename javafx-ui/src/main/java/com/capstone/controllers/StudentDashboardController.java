package com.capstone.controllers;

import com.capstone.javafxui.MainApp;
import com.capstone.models.User;
import com.capstone.models.enums.Role;
import com.capstone.services.ProjectService;
import com.capstone.utils.Guard;
import com.capstone.utils.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class StudentDashboardController {

    @FXML private BorderPane mainRoot; // Optional if using contentArea
    @FXML private StackPane contentArea; // Matches student_dashboard.fxml
    @FXML private Label studentName;
    @FXML private Circle profileCircle;

    @FXML
    public void initialize() {
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/profilepic.jpeg")));
        profileCircle.setFill(new ImagePattern(img));
        // Guard.require(Role.STUDENT); // Uncomment if Guard is active
        User student = Session.getUser();
        if (student != null) {
            studentName.setText(student.getUsername());
        }
        handleDashboardNav(); // Load default view

    }

    @FXML
    public void handleDashboardNav() {
        // Load a simple dashboard home if you have one, or just projects
        loadView("/fxml/dashboard_home.fxml");
    }

    @FXML
    public void handleProjectDetailsNav() {
        loadView("/fxml/my_projects.fxml");
    }

    @FXML
    public void handleMilestonesNav() {
        // This is a generic button, likely needs specific project context
        // Could redirect to projects page
        loadView("/fxml/milestones.fxml");
    }

    @FXML
    public void handleScoresNav() {
        // Loads the Student's view of their grades
        loadView("/fxml/student_scores.fxml");
    }

    @FXML
    public void handleMessagesNav() {
        // Loads the inbox
        loadView("/fxml/messages.fxml");
    }

    @FXML
    public void handleLogout() throws Exception {
        Session.logout();
       try {
           Stage stage = (Stage) contentArea.getScene().getWindow();

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
               stage = (Stage) contentArea.getScene().getWindow();
               Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/login.fxml")));
               stage.setScene(new Scene(root));
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
    }


    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof MyProjectsController) {
                ((MyProjectsController) controller).setMainController(this);
            }

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Could not load FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // Expose for child controllers
    public StackPane getContentArea() { return contentArea; }
}