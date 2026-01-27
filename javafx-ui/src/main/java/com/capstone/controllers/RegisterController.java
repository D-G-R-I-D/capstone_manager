package com.capstone.controllers;

import com.capstone.javafxui.MainApp;
import com.capstone.models.enums.Role;
import com.capstone.services.UserService;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class RegisterController {

    @FXML HBox mainRoot;
    @FXML VBox authContainer;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Role> roleChoice;
    @FXML private Label messageLabel;
    @FXML private Circle logoCircle;

    private final UserService userService = new UserService();


    @FXML
    public void initialize() {

        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/capstone logo 1.jpg")));
        logoCircle.setFill(new ImagePattern(img));

        // 2. Safety check to prevent NullPointerExceptions
        if (authContainer != null) {
            authContainer.setOpacity(0); // Start invisible for the fade

            FadeTransition fadeIn = new FadeTransition(Duration.millis(1500), authContainer);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        }

        // Listener for Input Fields
        usernameField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty()) {
                usernameField.setStyle("-fx-font-weight: normal;");
            } else {
                usernameField.setStyle("-fx-font-weight: bold;");
            }
        });

        emailField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty()) {
                emailField.setStyle("-fx-font-weight: normal;");
            } else {
                emailField.setStyle("-fx-font-weight: bold;");
            }
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                passwordField.setStyle("-fx-font-weight: normal;");
            } else {
                passwordField.setStyle("-fx-font-weight: bold;");
            }
        });

        roleChoice.getItems().addAll(
                Role.STUDENT,
                Role.SUPERVISOR
//                Role.SENIOR_SUPERVISOR
        );
        roleChoice.setValue(Role.STUDENT); // can remove if not auto wanted on the page
    }

    @FXML
    private void handleRegister() {
        try {
            userService.register(
                    usernameField.getText(),
                    emailField.getText(),
                    passwordField.getText(),
                    roleChoice.getValue()
            );
            messageLabel.setText("Registration successful!");
        } catch (Exception e) {
            messageLabel.setText("! Registration failed");
        }
    }

    @FXML
    private void goToLogin() throws Exception{

        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();

            // Save current window position and size
            double x = stage.getX();
            double y = stage.getY();
            double width = stage.getWidth();
            double height = stage.getHeight();

            MainApp mainApp = (MainApp) stage.getUserData();
            if (mainApp != null) {
                mainApp.showLoginScene();  // Uses the centered method

                // Restore after switch
            }
            else {
                // Fallback (rare, but saf)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(
                        Objects.requireNonNull (Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm()));
                stage.setScene(scene);
                stage.setTitle("Capstone Manager - Login");

                // Restore bounds
            }
            stage.setX(x);
            stage.setY(y);
            stage.setWidth(width);
            stage.setHeight(height);
        } catch (Exception e) {
            messageLabel.setText("Error returning to login");
            e.printStackTrace();  // This will show in console
        }
    }
}
