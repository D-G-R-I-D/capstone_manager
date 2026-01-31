package com.capstone.controllers;

import com.capstone.models.Milestone;
import com.capstone.models.Project;
import com.capstone.models.enums.MilestoneStatus;
import com.capstone.models.enums.ProjectStatus;
import com.capstone.services.MilestoneService;
import com.capstone.services.ProjectService;
import com.capstone.utils.Session;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardHomeController {

    @FXML private VBox rejectedBox;
    @FXML private VBox activeBox;
    @FXML private VBox pendingBox;
    @FXML private VBox completedBox;
    @FXML private Label welcomeLabel;
    @FXML private Label activeCountLabel;
    @FXML private Label pendingCountLabel;
    @FXML private Label completedCountLabel;
    @FXML private Label rejectedCountLabel;
    @FXML private PieChart statusPieChart;
    @FXML private BarChart<String, Number> milestoneBarChart;
    private final ProjectService projectService = new ProjectService();
    private final MilestoneService milestoneService = new MilestoneService(); // Ensure this exists

    @FXML
    public void initialize() {
        try {
            String username = Session.getUser().getUsername();
            welcomeLabel.setText("Welcome back, " + username + "!");

            // Load Real Stats
            assert Session.getUser() != null;
            List<Project> myProjects = projectService.getProjectsForStudent(Session.getUser().getId());
            if (myProjects == null) myProjects = new ArrayList<>(); // Prevent crash if null

            long active = myProjects.stream().filter(p -> p.getStatus() == ProjectStatus.ACTIVE).count();
            long pending = myProjects.stream().filter(p -> p.getStatus() == ProjectStatus.PENDING).count();
            long completed = myProjects.stream().filter(p -> p.getStatus() == ProjectStatus.COMPLETED).count();
            long rejected = myProjects.stream().filter(p -> p.getStatus() == ProjectStatus.REJECTED).count();

            // 4. Update Labels
            if(activeCountLabel != null) activeCountLabel.setText(String.valueOf(active));
            if(pendingCountLabel != null) pendingCountLabel.setText(String.valueOf(pending));
            if(completedCountLabel != null) completedCountLabel.setText(String.valueOf(completed));
            if(rejectedCountLabel != null) rejectedCountLabel.setText(String.valueOf(rejected));


            // 3. Populate Pie Chart
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                    new PieChart.Data("Active", active),
                    new PieChart.Data("Pending", pending),
                    new PieChart.Data("Completed", completed),
                    new PieChart.Data("Rejected", rejected)
            );

            /*                                Or
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            if (active > 0) pieData.add(new PieChart.Data("Active", active));
            if (pending > 0) pieData.add(new PieChart.Data("Pending", pending));
            if (completed > 0) pieData.add(new PieChart.Data("Completed", completed));
            if (rejected > 0) pieData.add(new PieChart.Data("Rejected", rejected));
            */

            if (statusPieChart != null)
                statusPieChart.setData(pieData);
            assert statusPieChart != null;
            statusPieChart.setData(pieData);

            // APPLY COLORS AFTER DATA IS SET
            for (PieChart.Data data : statusPieChart.getData()) {
                String status = data.getName();
                switch (status) {
                    case "Active" -> data.getNode().setStyle("-fx-pie-color: #3498db;");    // Blue
                    case "Pending" -> data.getNode().setStyle("-fx-pie-color: #f1c40f;");   // Yellow
                    case "Completed" -> data.getNode().setStyle("-fx-pie-color: #2ecc71;"); // Green
                    case "Rejected" -> data.getNode().setStyle("-fx-pie-color: #e74c3c;");  // Red
                }
            }


            // 4. Populate Bar Chart (Milestones)
            XYChart.Series<String, Number> seriesPlanned = new XYChart.Series<>();
            seriesPlanned.setName("Pending");

            XYChart.Series<String, Number> seriesDone = new XYChart.Series<>();
            seriesDone.setName("Completed");

            boolean hasData = false;
            for (Project p : myProjects) {
                if (p.getStatus() == ProjectStatus.ACTIVE || p.getStatus() == ProjectStatus.COMPLETED) {
                    // Fetch milestones for this project
                    List<Milestone> mList = milestoneService.getByProject(p.getId());
//                    if (mList.isEmpty()) continue;
                    long doneCount;
                    long pendingCount;
                    if (p.getStatus() == ProjectStatus.COMPLETED) {
                        doneCount = (mList.isEmpty()) ? 4 : mList.size(); // Default to 4 if empty
                        pendingCount = 0;
                    } else {
                        doneCount = mList.stream().filter(m -> m.getStatus() == MilestoneStatus.COMPLETED).count();
                        pendingCount = mList.stream().filter(m -> m.getStatus() != MilestoneStatus.PENDING).count();
                    }

                    System.out.println("DEBUG: Active=" + active + ", Pending=" + pending + ", Completed=" + completed);
                    seriesPlanned.getData().add(new XYChart.Data<>(p.getTitle(), pendingCount));
                    seriesDone.getData().add(new XYChart.Data<>(p.getTitle(), doneCount));
                    hasData = true;
                }
            }
            if (milestoneBarChart != null) {
                milestoneBarChart.getData().clear();
                milestoneBarChart.setAnimated(false); // Fixes rendering bugs on reload
                if (hasData) {
                    milestoneBarChart.getData().addAll(Arrays.asList(seriesPlanned, seriesDone));
                }
            }
        } catch (Exception e) {
            System.err.println("Dashboard Initialization Error: " + e.getMessage());
            e.printStackTrace();
        }


        for (PieChart.Data data : statusPieChart.getData()) {
            // Listen for when the visual Node is created/re-created
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    handlePieHover();
                    handlePieHoverExit();
                }
            });

            // Also run it immediately in case the node already exists
            if (data.getNode() != null) {
                handlePieHover();
                handlePieHoverExit();
            }
        }

    }

    @FXML
    private void handlePieHover() {
        // 1. Calculate the total sum of all pie slice values
        double total = statusPieChart.getData().stream()
                .mapToDouble(PieChart.Data::getPieValue)
                .sum();
        // Iterate through each data point in your PieChart
        for (PieChart.Data data : statusPieChart.getData()) {
            Node sliceNode = data.getNode();
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), sliceNode);

            // 1. Handle Mouse Hover Entrance
            sliceNode.addEventHandler(MouseEvent.MOUSE_ENTERED, _ -> {
                // 1. Define the Scale Transition
                scaleTransition.setToX(1.1); // Scale to 110% of original size
                scaleTransition.setToY(1.1);
                scaleTransition.playFromStart();
                sliceNode.setStyle("-fx-cursor: hand;"); // Change cursor to a hand pointer
                // Optional: show a tooltip or update a label with the value
//                System.out.println("Hovering over: " + data.getName());

                // Calculate percentage (e.g., 25.5%)
                double percentage = (data.getPieValue() / total) * 100;
                // Create a formatted string for the tooltip
                String tooltipText = String.format("%s: %.1f%%", data.getName(), percentage);
                Tooltip tooltip = new Tooltip(tooltipText);
                // Install it onto the data's node
                Tooltip.install(data.getNode(), tooltip);
                // Optional: Add a listener so the tooltip updates if data values change
                data.pieValueProperty().addListener((obs, oldVal, newVal) ->
//                    tooltip.setText(data.getName() + ": " + newVal)
                                tooltip.setText(tooltipText)
                );
                tooltip.setShowDelay(Duration.millis(200));

                String status = data.getName();
                switch (status) {
                    case "Active" -> data.getNode().setStyle("-fx-pie-color: #3498db;");    // Blue
                    case "Pending" -> data.getNode().setStyle("-fx-pie-color: #f1c40f;");   // Yellow
                    case "Completed" -> data.getNode().setStyle("-fx-pie-color: #2ecc71;"); // Green
                    case "Rejected" -> data.getNode().setStyle("-fx-pie-color: #e74c3c;");  // Red
                }
            });
        }
    }

    @FXML private void handlePieHoverExit() {
        // Iterate through each data point in your PieChart
        for (PieChart.Data data : statusPieChart.getData()) {
            Node sliceNode = data.getNode();
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), sliceNode);

            // 3. Add Mouse Exit Event Handler
            sliceNode.addEventHandler(MouseEvent.MOUSE_EXITED, _ -> {
                scaleTransition.setToX(1.0); // Return to 100% (original size)
                scaleTransition.setToY(1.0);
                scaleTransition.playFromStart();

                String status = data.getName();
                switch (status) {
                    case "Active" -> data.getNode().setStyle("-fx-pie-color: #3498db;");    // Blue
                    case "Pending" -> data.getNode().setStyle("-fx-pie-color: #f1c40f;");   // Yellow
                    case "Completed" -> data.getNode().setStyle("-fx-pie-color: #2ecc71;"); // Green
                    case "Rejected" -> data.getNode().setStyle("-fx-pie-color: #e74c3c;");  // Red
                }
            });
        }
    }

    @FXML
    private void handleHover(MouseEvent event) {
        VBox source = (VBox) event.getSource();
        String currentStyle = source.getStyle();

        // Change the 0.4 opacity to 0.7 to make it look highlighted
        source.setStyle(currentStyle.replace("0.9)", "1.0)"));

        // Optional: Make it lift slightly
        source.setTranslateY(-5);
    }

    @FXML
    private void handleExit(MouseEvent event) {
        VBox source = (VBox) event.getSource();
        String currentStyle = source.getStyle();

        // Change it back to 0.4
        source.setStyle(currentStyle.replace("1.0)", "0.9)"));

        // Reset position
        source.setTranslateY(0);
    }
}
