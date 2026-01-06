package com.capstone.controllers;

import com.capstone.models.User;
import com.capstone.services.AuthService;
import com.capstone.utils.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.util.Objects;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
//    @FXML private Button registerButton;

    private final AuthService authService = new AuthService();

    @FXML
    public void handleLogin() {
        try {
            User user = authService.login(usernameField.getText(), passwordField.getText());
            Session.setUser(user);

            String fxmlPath = switch (user.getRole()) {
                case ADMIN -> "/fxml/admin_dashboard.fxml";
                case STUDENT -> "/fxml/student_dashboard.fxml";
                case SUPERVISOR -> "/fxml/supervisor_dashboard.fxml";
                case SENIOR_SUPERVISOR -> "/fxml/senior_supervisor_dashboard.fxml";
            };

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource(fxmlPath)
            ))));
            stage.setMaximized(true);  // Optional: open dashboard maximized

        } catch (Exception e) {
            errorLabel.setText("Invalid username or password");
            e.printStackTrace();
        }
    }

    // 🔥 MUST BE PUBLIC
    @FXML
    public void goToRegister() {
        try {
            // Test alert to confirm click
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Create Account clicked! Loading register...");
            alert.showAndWait();

            // Rest of your code...
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(
                    FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/register.fxml")))
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @FXML
//    private void initialize() {
//        registerButton.setOnAction(e -> goToRegister());
//    }
}
