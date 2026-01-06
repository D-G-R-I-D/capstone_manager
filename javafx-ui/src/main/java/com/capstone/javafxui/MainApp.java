package com.capstone.javafxui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;

import atlantafx.base.theme.PrimerLight;  // Import here

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Optional: AtlantaFX theme (uncomment if you added the dependency)
        // Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load());

        // Load your custom CSS
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm()
        );

        stage.setTitle("Capstone Manager");
        stage.setMinWidth(600);   // Minimum size
        stage.setMinHeight(700);
        stage.setWidth(800);      // Default open size
        stage.setHeight(900);
        stage.setMaximized(true); // Opens full screen or maximized — best for presentation!
        // stage.setResizable(true); // Allow resize (default is true anyway)

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}