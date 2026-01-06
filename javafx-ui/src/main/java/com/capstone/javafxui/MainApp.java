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
        // Set AtlantaFX theme FIRST (before any scene)
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load());

        // Add your custom CSS (optional if using AtlantaFX fully)
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/app.css")).toExternalForm());

        stage.setTitle("Capstone Manager");
        stage.setWidth(500);
        stage.setHeight(700);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}