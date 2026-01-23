package com.capstone.controllers;

import com.capstone.javafxui.MainApp;
import com.capstone.models.enums.Role;
import com.capstone.services.UserService;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class ChangePasswordController {

    @FXML private Region strengthBar;
    @FXML private Label strengthLabel;
    @FXML HBox mainRoot;
    @FXML VBox authContainer;
    @FXML private ImageView backgroundImage;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    //    @FXML private ComboBox<Role> roleChoice;
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
//        usernameField.textProperty().addListener((obs, oldText, newText) -> {
//            if (newText.isEmpty()) {
//                usernameField.setStyle("-fx-font-weight: normal;");
//            } else {
//                usernameField.setStyle("-fx-font-weight: bold;");
//            }
//        });

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

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateStrengthMeter(newValue);
        });

        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                passwordField.setStyle("-fx-font-weight: normal;");
                confirmPasswordField.setStyle("-fx-font-weight: normal;");
            } else {
                passwordField.setStyle("-fx-font-weight: bold;");
                confirmPasswordField.setStyle("-fx-font-weight: bold;");
            }
        });

//        roleChoice.getItems().addAll(
//                Role.STUDENT,
//                Role.SUPERVISOR,
//                Role.SENIOR_SUPERVISOR
//        );
//        roleChoice.setValue(Role.STUDENT); // can remove if not auto wanted on the page
    }

    private void updateStrengthMeter(String password) {
        if (password.isEmpty()) {
            strengthBar.setMaxWidth(0);
            strengthLabel.setText("");
            strengthBar.setStyle("-fx-background-color: white;");
        } else if (password.length() < 10 ) {
            strengthBar.setMaxWidth(100);
            strengthBar.setStyle("-fx-background-color: #ff4d4d;"); // Red
            strengthLabel.setText("Weak");
            strengthLabel.setStyle("-fx-text-fill: #ff4d4d");
        } else if (password.length() < 12) {
            strengthBar.setMaxWidth(200);
            strengthBar.setStyle("-fx-background-color: #ffcc00;"); // Yellow
            strengthLabel.setText("Fair");
            strengthLabel.setStyle("-fx-text-fill: #ffcc00");
        } else {
            strengthBar.setMaxWidth(300);
            strengthBar.setStyle("-fx-background-color: #2ecc71;"); // Green
            strengthLabel.setText("Strong");
            strengthLabel.setStyle("-fx-text-fill: #2ecc71");
        }
    }

    @FXML
    private void handlePasswordChange() {
       /* if (emailField.getText().trim().isEmpty() && passwordField.getText().trim().isEmpty() && confirmPasswordField.getText().trim().isEmpty()) {
            messageLabel.setText("Fields can not be empty");
        }*/
        if (!emailField.getText().trim().isEmpty()) {
            if (emailField.getText().trim().matches("^(?i)[\\w-]+@[\\w-]+\\.[A-Za-z]{2,}$")) {
                if (!passwordField.getText().trim().isEmpty()) {
                    if (!confirmPasswordField.getText().trim().isEmpty()) {
                        if (passwordField.getText().trim().matches("^.{5,64}$")) /*  Or -> (?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}*/ {
                            if (confirmPasswordField.getText().trim().equals(passwordField.getText().trim())) try {
                                userService.changePassword(
//                                      usernameField.getText(),
                                        userService.getUserByEmail(emailField.getText().trim()).getId(), // Or:  ->  String.valueOf(userService.getUserByEmail(emailField.getText()).getId())
                                        passwordField.getText()
//                                      roleChoice.getValue()
                                );
                                messageLabel.setText("Password changed successfully!");
                                emailField.setText("");
                                passwordField.setText("");
                                confirmPasswordField.setText("");
                            } catch (Exception e) {
                                messageLabel.setText("! Password change failed: User doesn't exist");
                                emailField.setText("");
                                passwordField.setText("");
                                confirmPasswordField.setText("");
                            }
                            else {
                                messageLabel.setText("! Password doesn't match");
                                passwordField.setText("");
                                confirmPasswordField.setText("");
                            }
                        } else {
                            messageLabel.setText("Password must be 12 or more characters");
                            passwordField.setText("");
                            confirmPasswordField.setText("");
                        }
                    } else {
                        messageLabel.setText("! Confirm password");
                    }
                } else {
                    messageLabel.setText("! Input New password");
                }
            } else {
                messageLabel.setText("! Input a valid email");
                emailField.setText("");
            }
        } else {
            messageLabel.setText("! Email field cannot be empty");
        }
    }

    @FXML
    private void goToLogin() throws Exception{

        try {
            Stage stage = (Stage) emailField.getScene().getWindow();

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