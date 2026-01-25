package com.capstone.controllers;

import com.capstone.models.Milestone;
import com.capstone.models.Project;
import com.capstone.models.enums.MilestoneStatus;
import com.capstone.models.enums.ProjectStatus;
import com.capstone.services.MilestoneService;
import com.capstone.services.ProjectService;
import com.capstone.utils.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

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

            if (statusPieChart != null) statusPieChart.setData(pieData);
            assert statusPieChart != null;
            statusPieChart.setData(pieData);

            // 4. Populate Bar Chart (Milestones)
            XYChart.Series<String, Number> seriesPlanned = new XYChart.Series<>();
            seriesPlanned.setName("Pending");

            XYChart.Series<String, Number> seriesDone = new XYChart.Series<>();
            seriesDone.setName("Completed");

            for (Project p : myProjects) {
                if (p.getStatus() == ProjectStatus.ACTIVE) {
                    // Fetch milestones for this project
                    List<Milestone> mList = milestoneService.getByProject(p.getId());
                    long doneCount = mList.stream().filter(m -> m.getStatus() == MilestoneStatus.COMPLETED).count();
                    long pendingCount = mList.stream().filter(m -> m.getStatus() != MilestoneStatus.PENDING).count();

                    System.out.println("DEBUG: Active=" + active + ", Pending=" + pending + ", Completed=" + completed);
                    seriesPlanned.getData().add(new XYChart.Data<>(p.getTitle(), pendingCount));
                    seriesDone.getData().add(new XYChart.Data<>(p.getTitle(), doneCount));
                }
            }
            if (milestoneBarChart != null && !seriesPlanned.getData().isEmpty()) {
                milestoneBarChart.getData().addAll(Arrays.asList(seriesPlanned, seriesDone));
            }
        } catch (Exception e) {
            System.err.println("Dashboard Initialization Error: " + e.getMessage());
            e.printStackTrace();
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
