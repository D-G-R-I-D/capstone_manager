package com.capstone.controllers;

import com.capstone.models.Proposal;
import com.capstone.services.ProposalService;
import com.capstone.utils.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class ProposalFormController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private Label fileLabel;
    @FXML private Label messageLabel;

    private final ProposalService proposalService = new ProposalService();
    private byte[] fileData;

    @FXML
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Word Files", "*.docx")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            fileLabel.setText(selectedFile.getName());
            try {
                fileData = Files.readAllBytes(selectedFile.toPath());  // Read bytes once
                messageLabel.setText("File selected: " + selectedFile.getName());
            } catch (IOException e) {
                messageLabel.setText("Failed to read file: " + e.getMessage());
                fileData = null;
            }
        }
    }

    @FXML
    private void submitProposal() {
        try {
            Proposal proposal = new Proposal();
            proposal.setTitle(titleField.getText().trim());
            proposal.setDescription(descriptionArea.getText().trim());
            proposal.setStudentId(Session.getUser().getId());
            proposal.setFileData(fileData);  // Now resolves! Saves bytes to DB

            System.out.println("File bytes length: " + (fileData != null ? fileData.length : 0));   // debug log:
            proposalService.submitProposal(proposal);
            messageLabel.setText("Proposal submitted successfully!");
            // Optional: clear form
            titleField.clear();
            descriptionArea.clear();
            fileLabel.setText("No file selected");
            fileData = null;
        } catch (Exception e) {
            messageLabel.setText("Submission failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource("/fxml/student_dashboard.fxml")
            ))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
