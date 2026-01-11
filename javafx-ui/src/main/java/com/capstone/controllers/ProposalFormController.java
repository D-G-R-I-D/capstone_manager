package com.capstone.controllers;

import com.capstone.models.Proposal;
import com.capstone.services.ProposalService;
import com.capstone.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ProposalFormController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private Label fileLabel;
    @FXML private Label messageLabel;

    private final ProposalService proposalService = new ProposalService();
    private File selectedFile;

    @FXML
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.docx"));
        selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            fileLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    private void submitProposal() {
        try {
            Proposal proposal = new Proposal();
            proposal.setTitle(titleField.getText().trim());
            proposal.setDescription(descriptionArea.getText().trim());
            proposal.setStudentId(Session.getUser().getId());
            // Upload file if selected
            if (selectedFile != null) {
                proposal.setFilePath(selectedFile.getAbsolutePath());  // Save path or byte array in real app
            }

            proposalService.submitProposal(proposal);
            messageLabel.setText("Proposal submitted successfully!");
        } catch (Exception e) {
            messageLabel.setText("Submission failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
