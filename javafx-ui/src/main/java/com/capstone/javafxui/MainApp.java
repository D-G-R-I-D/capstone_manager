package com.capstone.javafxui;

import com.capstone.models.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.Objects;

import atlantafx.base.theme.PrimerLight;  // Import here

public class MainApp extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

//        stage.setMinWidth(600);   // Minimum size
//        stage.setMinHeight(700);
//        stage.setWidth(800);      // Default open size
//        stage.setHeight(900);
        stage.setMaximized(true); // Opens full screen or maximized — best for presentation!
        // stage.setResizable(true); // Allow resize (default is true anyway)// Bring to front if minimized

        showLoginScene();  // Start with login

        stage.requestFocus();  // Ensure window stays focused
        stage.toFront();
        // Bring to front if minimized

        // Save current window position and size
        double x = stage.getX();
        double y = stage.getY();
        double width = stage.getWidth();
        double height = stage.getHeight();
    }

        // Optional: AtlantaFX theme (uncomment if you added the dependency)
        // Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
    public void showLoginScene() throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent authContainer = loader.load();

        StackPane root = new StackPane();
        root.getChildren().add(authContainer);

        // Center perfectly in middle
        StackPane.setAlignment(authContainer, Pos.CENTER);
//            StackPane.setAlignment(authContainer, Pos.TOP_CENTER);


        // Nice padding from edges
        //StackPane.setMargin(authContainer, new Insets(40));
        StackPane.setMargin(authContainer, new Insets(200, 50, 200, 50));

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm()
        );

        // THE FIX: Bind the HBox dimensions to the Scene dimensions
// Use casting if 'root' is declared as Parent
        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());

        primaryStage.setScene(scene);
        primaryStage.requestFocus();
        primaryStage.toFront();
        primaryStage.setTitle("Capstone Manager - Login");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(700);
        primaryStage.show();

        primaryStage.setUserData(this); // For controllers to call MainApp methods
    }

    public void showRegisterScene() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
        Parent authContainer = loader.load();

        StackPane root = new StackPane();
        root.getChildren().add(authContainer);

        // Same perfect centering
        StackPane.setAlignment(authContainer, Pos.CENTER);
        StackPane.setMargin(authContainer, new Insets(40));

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm()
        );

        primaryStage.setScene(scene);
        primaryStage.requestFocus();
        primaryStage.toFront();
        primaryStage.setTitle("Capstone Manager - Register");
    }

    public void showChangePasswordScene() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/passwordChange.fxml"));
        Parent authContainer = loader.load();

        StackPane root = new StackPane();
        root.getChildren().add(authContainer);

        // Same perfect centering
        StackPane.setAlignment(authContainer, Pos.CENTER);
        StackPane.setMargin(authContainer, new Insets(40));

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm()
        );

        primaryStage.setScene(scene);
        primaryStage.requestFocus();
        primaryStage.toFront();
        primaryStage.setTitle("Capstone Manager - Change Password");
    }

    public void showStudentDashboard() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student_dashboard.fxml"));
        Parent dashboardRoot = loader.load();

        StackPane root = new StackPane();
        root.getChildren().add(dashboardRoot);

        // Optional: Center if needed (most dashboards fill the screen, so maybe not)
        StackPane.setAlignment(dashboardRoot, Pos.TOP_CENTER);
        StackPane.setMargin(dashboardRoot, new Insets(0, 20, 50, 20));  //bottom 35 best if

        Scene scene = new Scene(root, 1200, 800);  // Larger size for dashboard
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm()
        );

        primaryStage.setScene(scene);
        primaryStage.setTitle("Student Dashboard");


    }


    public void showDashboard(User user) throws Exception {

        Stage stage = (Stage) primaryStage.getScene().getWindow();
        // Save current window position and size
        double x = stage.getX();
        double y = stage.getY();
        double width = stage.getWidth();
        double height = stage.getHeight();

        // 1. Determine the FXML path based on the Role Enum
        String fxmlPath = switch (user.getRole()) {
            case ADMIN -> "/fxml/admin_dashboard.fxml";
            case STUDENT -> "/fxml/student_dashboard.fxml";
            case SUPERVISOR -> "/fxml/supervisor_dashboard.fxml";
            case SENIOR_SUPERVISOR -> "/fxml/senior_supervisor_dashboard.fxml";
        };

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent dashboardRoot = loader.load();

        // 2. Wrap in a StackPane to manage layout/margins
        StackPane root = new StackPane(dashboardRoot);
        StackPane.setAlignment(dashboardRoot, Pos.TOP_CENTER);

        // Applying your requested margins (Top: 0, Right: 20, Bottom: 35, Left: 20)
        StackPane.setMargin(dashboardRoot, new Insets(0, 20, 35, 20));

        // 3. Setup the Scene
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm()
        );

        // Restore after switch
        stage.setX(x);
        stage.setY(y);
        stage.setWidth(width);
        stage.setHeight(height);

        // 4. Update the Stage
        primaryStage.setScene(scene);
        primaryStage.setTitle(user.getRole() + " Dashboard - " + user.getUsername());
        primaryStage.centerOnScreen(); // Good practice after resizing
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}