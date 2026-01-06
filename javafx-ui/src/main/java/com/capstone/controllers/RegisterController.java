package com.capstone.controllers;

import com.capstone.models.enums.Role;
import com.capstone.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Objects;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<Role> roleChoice;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        roleChoice.getItems().addAll(
                Role.STUDENT,
                Role.SUPERVISOR,
                Role.SENIOR_SUPERVISOR
        );
        roleChoice.setValue(Role.STUDENT);
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
            messageLabel.setText("Registration failed");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/login.fxml")))));
        } catch (Exception e) {
            messageLabel.setText("Error returning to login");
            e.printStackTrace();  // This will show in console
        }
    }
}
