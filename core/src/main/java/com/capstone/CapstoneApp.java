package com.capstone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CapstoneApp  extends Application{
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Capstone Manager");
        stage.setScene(new Scene(
                FXMLLoader.load(getClass().getResource("/fxml/login.fxml"))
        ));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
