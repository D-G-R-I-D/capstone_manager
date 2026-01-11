package com.capstone.controllers;

import com.capstone.models.Milestone;
import com.capstone.models.Project;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.beans.property.SimpleStringProperty;
import org.jetbrains.annotations.NotNull;

public class ProjectDetailsController {

    @FXML private Label titleLabel;
    @FXML private Label statusLabel;
    @FXML private TextArea descriptionArea;
    @FXML private TableView<Milestone> milestonesTable;
    @FXML private TableColumn<Milestone, String> nameCol;
    @FXML private TableColumn<Milestone, String> dueDateCol;
    @FXML private TableColumn<Milestone, String> statusCol;

    public void setProject(@NotNull Project project) {
        titleLabel.setText(project.getTitle());
        statusLabel.setText("Status: " + project.getStatus().name());
        descriptionArea.setText(
                project.getDescription() != null ? project.getDescription() : "No description available"
        );

        // Milestones table
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        dueDateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeadline().toString()));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));

        milestonesTable.setItems(FXCollections.observableArrayList(project.getMilestones()));
    }
}
