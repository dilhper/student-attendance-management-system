package com.sams.ui;

import com.sams.model.User;
import com.sams.service.*;
import com.sams.ui.components.Sidebar;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Main application dashboard — sidebar navigation + dynamic content area.
 */
public class DashboardView {

    private final Stage stage;
    private final User currentUser;
    private BorderPane root;
    private StackPane contentArea;

    public DashboardView(Stage stage, User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
        buildUI();
    }

    public Parent getView() {
        return root;
    }

    private void buildUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #0F172A;");

        // setup sidebar navigation
        Sidebar sidebar = new Sidebar(currentUser, this::handleNavigation);
        root.setLeft(sidebar);

        // main content area
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        root.setCenter(contentArea);

        // Show dashboard home by default
        showDashboardHome();
    }

    private void handleNavigation(String viewId) {
        contentArea.getChildren().clear();
        switch (viewId) {
            case "dashboard" -> showDashboardHome();
            case "courses" -> contentArea.getChildren().add(new CourseView(currentUser).getView());
            case "students" -> contentArea.getChildren().add(new StudentView(currentUser).getView());
            case "lecturers" -> contentArea.getChildren().add(new LecturerView(currentUser).getView());
            case "schedule" -> contentArea.getChildren().add(new ClassSessionView(currentUser).getView());
            case "attendance" -> contentArea.getChildren().add(new AttendanceView(currentUser).getView());
            case "reports" -> contentArea.getChildren().add(new AttendanceReportView(currentUser).getView());
            case "logout" -> logout();
        }
    }

    private void showDashboardHome() {
        VBox home = new VBox(24);
        home.setPadding(new Insets(8));

        // Welcome message
        Label welcomeTitle = new Label("Welcome, " + currentUser.getUsername() + "!");
        welcomeTitle.getStyleClass().add("page-title");

        Label welcomeSub = new Label("Student Attendance Management System — Overview");
        welcomeSub.getStyleClass().add("page-subtitle");

        // dashboard statistics cards
        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER_LEFT);

        CourseService courseService = new CourseService();
        StudentService studentService = new StudentService();
        LecturerService lecturerService = new LecturerService();
        ClassSessionService sessionService = new ClassSessionService();

        statsRow.getChildren().addAll(
            createStatCard("📚", String.valueOf(courseService.getCourseCount()), "Total Courses"),
            createStatCard("🎓", String.valueOf(studentService.getStudentCount()), "Total Students"),
            createStatCard("👨‍🏫", String.valueOf(lecturerService.getLecturerCount()), "Total Lecturers"),
            createStatCard("📅", String.valueOf(sessionService.getSessionCount()), "Class Sessions")
        );

        // Quick info
        VBox infoCard = new VBox(12);
        infoCard.getStyleClass().add("card");

        Label infoTitle = new Label("Quick Guide");
        infoTitle.setStyle("-fx-text-fill: #14B8A6; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label infoText = new Label(
            currentUser.getRole().name().equals("ADMIN")
                ? "As an Administrator, you have full access to manage courses, students, lecturers, "
                  + "class schedules, and view attendance reports. Use the sidebar to navigate between modules."
                : "As a Lecturer, you can view your class schedule, mark attendance for your sessions, "
                  + "and view attendance reports. Use the sidebar to navigate."
        );
        infoText.setWrapText(true);
        infoText.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 14px; -fx-line-spacing: 4;");

        infoCard.getChildren().addAll(infoTitle, infoText);

        home.getChildren().addAll(welcomeTitle, welcomeSub, statsRow, infoCard);
        contentArea.getChildren().add(home);
        StackPane.setAlignment(home, Pos.TOP_LEFT);
    }

    private VBox createStatCard(String icon, String value, String label) {
        VBox card = new VBox(8);
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(200);
        card.setPrefHeight(140);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 28px;");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        Label descLabel = new Label(label);
        descLabel.getStyleClass().add("stat-label");

        card.getChildren().addAll(iconLabel, valueLabel, descLabel);
        return card;
    }

    private void logout() {
        LoginView loginView = new LoginView(stage);
        Scene scene = new Scene(loginView.getView(), stage.getWidth(), stage.getHeight());
        scene.getStylesheets().add(
            getClass().getResource("/style.css").toExternalForm()
        );
        stage.setScene(scene);
    }
}
