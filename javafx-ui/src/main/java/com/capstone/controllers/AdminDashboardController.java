package com.capstone.controllers;

import com.capstone.javafxui.MainApp;
import com.capstone.models.Project;
import com.capstone.models.User;
import com.capstone.models.enums.Role;
import com.capstone.services.ProjectService;
import com.capstone.services.UserService;
import com.capstone.utils.Guard;
import com.capstone.utils.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import java.util.Objects;

public class AdminDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<User> userTable;
    @FXML private TableView<Project> projectTable;

    private final UserService userService = new UserService();
    private final ProjectService projectService = new ProjectService();

    @SuppressWarnings("unchecked")
    @FXML
    public void initialize() {
        Guard.require(Role.ADMIN);
        User admin = Session.getUser();
        if (admin == null) throw new IllegalStateException("No session user");

        welcomeLabel.setText("Admin Dashboard: " + admin.getUsername());

        // Users Table columns
        TableColumn<User, String> userNameCol = new TableColumn<>("Username");
        userNameCol.setPrefWidth(200);
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        // User table columns
        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setPrefWidth(150);
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<User, Void> userActionsCol = new TableColumn<>("Actions");
//        TableColumn<User, Void> userActionsCol = getUserVoidTableColumn();
        userActionsCol.setCellFactory(column -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            private final Button editBtn = new Button("Edit Role");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    deleteBtn.setOnAction(e -> deleteUser(user));
                    editBtn.setOnAction(e -> editUserRole(user));
                    setGraphic(new HBox(10, editBtn, deleteBtn));
                }
            }
        });

        userTable.getColumns().addAll(userNameCol, roleCol, userActionsCol);
        userTable.setItems(FXCollections.observableArrayList(userService.getAllUsers()));

        // Projects Table (similar setup)
        // Add columns and actions like delete/approve
        // ...
    }

    private TableColumn<User, Void> getUserVoidTableColumn() {
        TableColumn<User, Void> userActionsCol = new TableColumn<>("Actions");
        userActionsCol.setPrefWidth(150);
        userActionsCol.setCellFactory(column -> {
            return new TableCell<User, Void>() {
                private final Button deleteBtn = new Button("Delete");
                private final Button editBtn = new Button("Edit Role");

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        User user = getTableView().getItems().get(getIndex());
                        deleteBtn.setOnAction(event -> deleteUser(user));
                        editBtn.setOnAction(event -> editUserRole(user));
                        HBox buttons = new HBox(10, editBtn, deleteBtn);
                        setGraphic(buttons);
                        setText(null);
                    }
                }
            };
        });
        return userActionsCol;
    }

    private void deleteUser(User user) {
        // Confirm and delete
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete user " + user.getUsername() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userService.deleteUser(user.getId());
                userTable.getItems().remove(user);
            }
        });
    }

    private void editUserRole(User user) {
        TextInputDialog dialog = new TextInputDialog(user.getRole().name());
        dialog.setTitle("Edit Role");
        dialog.setHeaderText("Change role for " + user.getUsername());
        dialog.showAndWait().ifPresent(newRole -> {
            try {
                user.setRole(Role.valueOf(newRole.toUpperCase()));
                userService.updateUser(user);
                userTable.refresh();
            } catch (IllegalArgumentException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid role");
                alert.show();
            }
        });
    }

    @FXML
    private void handleLogout() throws Exception {
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();

        // Save current bounds
        double x = stage.getX();
        double y = stage.getY();
        double width = stage.getWidth();
        double height = stage.getHeight();

        Session.logout();

        MainApp mainApp = (MainApp) stage.getUserData();
        if (mainApp != null) {
            mainApp.showLoginScene();

            // Restore bounds after switch
            stage.setX(x);
            stage.setY(y);
            stage.setWidth(width);
            stage.setHeight(height);
            stage.requestFocus();
            stage.toFront();
        } else {
            // Fallback with bounds restore
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Capstone Manager - Login");

            stage.setX(x);
            stage.setY(y);
            stage.setWidth(width);
            stage.setHeight(height);
            stage.requestFocus();
            stage.toFront();
        }
    }
}