package com.capstone.utils;

import com.capstone.models.User;
import com.capstone.models.enums.Role;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Guard {

    private static final Logger LOGGER =
            Logger.getLogger(Guard.class.getName());

    private Guard() {}

    public static void require(Role requiredRole) {
        User user = Session.getUser();

        if (user == null || user.getRole() != requiredRole) {
            LOGGER.warning("Unauthorized access attempt. Required role: "
                    + requiredRole);
            redirectToLogin();
        }
    }

    private static void redirectToLogin() {
        try {
            Stage stage = (Stage) Stage.getWindows()
                    .filtered(Window::isShowing)
                    .getFirst();

            stage.setScene(new Scene(
                    FXMLLoader.load(
                            Objects.requireNonNull(
                                    Guard.class.getResource("/fxml/login.fxml")
                            )
                    )
            ));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    "Failed to redirect to login screen", e);
        }
    }
}