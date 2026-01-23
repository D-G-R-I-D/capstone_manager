package com.capstone.controllers;

import com.capstone.javafxui.MainApp;
import com.capstone.models.User;
import com.capstone.services.AuthService;
import com.capstone.utils.Session;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import javafx.util.Duration;
import java.util.Objects;

public class LoginController {

    @FXML private VBox authContainer;
    @FXML private ImageView backgroundImage;
    @FXML private HBox mainRoot;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
//    @FXML private Button registerButton;

    private final AuthService authService = new AuthService();

    @FXML private Circle logoCircle;

    public void initialize() {

        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/capstone logo 1.jpg")));
        logoCircle.setFill(new ImagePattern(img));

        // 1. Resize logic for the background
        // 2. Safety check to prevent NullPointerExceptions
        if (authContainer != null) {
            authContainer.setOpacity(0); // Start invisible for the fade

            FadeTransition fadeIn = new FadeTransition(Duration.millis(1500), authContainer);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        }

       /* if (backgroundImage != null && mainRoot != null) {
            backgroundImage.fitWidthProperty().bind(mainRoot.widthProperty().divide(2));
            backgroundImage.fitHeightProperty().bind(mainRoot.heightProperty());
        }*/

        // Listener for Input Fields
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
    }


    @FXML
    private void handleLogin() {
        try {
            User user = authService.login(emailField.getText().trim(), passwordField.getText().trim());
            Session.setUser(user);

            // Clear error
            errorLabel.setText("");

            String fxmlPath = switch (user.getRole()) {
                case ADMIN -> "/fxml/admin_dashboard.fxml";
                case STUDENT -> "/fxml/student_dashboard.fxml";
                case SUPERVISOR -> "/fxml/supervisor_dashboard.fxml";
                case SENIOR_SUPERVISOR -> "/fxml/senior_supervisor_dashboard.fxml";
            };

//            // 2. ROUTING LOGIC (The Fix)
//            String fxmlPath;
//            String title = "";
//
//            switch (user.getRole()) {
//                case SUPERVISOR:
//                    fxmlPath = "/fxml/supervisor_dashboard.fxml";
//                    title = "Supervisor Dashboard";
//                    break;
//                case ADMIN:
//                    fxmlPath = "/fxml/admin_dashboard.fxml"; // Ensure this file exists!
//                    title = "Admin Dashboard";
//                    break;
//                case STUDENT:
//                default: // Default to student only if role matches or is unknown
//                    fxmlPath = "/fxml/student_dashboard.fxml";
//                    title = "Student Dashboard";
//                    break;
//            }

            // Switch to dashboard using MainApp method
            MainApp mainApp = (MainApp) emailField.getScene().getWindow().getUserData();
            if (mainApp != null) {
                mainApp.showDashboard(user);
            } else {
                // Fallback (shouldn't happen if MainApp setUserData is working)
                Stage stage = (Stage) emailField.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
                stage.setScene(scene);
                String titleText = switch (user.getRole()) {
                    case ADMIN -> "ADMIN DASHBOARD";
                    case STUDENT -> "STUDENT DASHBOARD";
                    case SUPERVISOR -> "SUPERVISOR DASHBOARD";
                    case SENIOR_SUPERVISOR -> "SENIOR_SUPERVISOR DASHBOARD";
                };
                stage.setTitle(titleText);
            }
        } catch (Exception e) {
            errorLabel.setText("! Invalid username or password");
            e.printStackTrace();
        }

//            Stage stage = (Stage) emailField.getScene().getWindow();
//            stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(
//                    getClass().getResource(fxmlPath)
//            ))));
//            stage.setMaximized(true);  // Optional: open dashboard maximized
//
//        } catch (Exception e) {
//            errorLabel.setText("! Invalid username or password");
//            e.printStackTrace();
//        }
    }

    // 🔥 CAN BE PUBLIC
    @FXML
    private void goToRegister() throws Exception {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();

            // Save current window position and size
            double x = stage.getX();
            double y = stage.getY();
            double width = stage.getWidth();
            double height = stage.getHeight();

        MainApp mainApp = (MainApp) emailField.getScene().getWindow().getUserData();
        if (mainApp != null) {
            mainApp.showRegisterScene();  // Uses the centered method
            stage.setX(x);
            stage.setY(y);
            stage.setWidth(width);
            stage.setHeight(height);
            // Restore after switch
        } else {
            // Fallback (rare)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    Objects.requireNonNull (Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm()));
            stage.setScene(scene);
            stage.setTitle("Capstone Manager - Register");

            // Restore bounds
        }
            stage.setX(x);
            stage.setY(y);
            stage.setWidth(width);
            stage.setHeight(height);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void goToChangePasswordScene() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();

            // Save current window position and size
            double x = stage.getX();
            double y = stage.getY();
            double width = stage.getWidth();
            double height = stage.getHeight();

            MainApp mainApp = (MainApp) emailField.getScene().getWindow().getUserData();
            if (mainApp != null) {
                mainApp.showChangePasswordScene();  // Uses the centered method

                // Restore after switch

            } else {
                // Fallback (rare)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(
                        Objects.requireNonNull (Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm()));
                stage.setScene(scene);
                stage.setTitle("Capstone Manager - Change Password");

                // Restore bounds
            }
            stage.setX(x);
            stage.setY(y);
            stage.setWidth(width);
            stage.setHeight(height);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
