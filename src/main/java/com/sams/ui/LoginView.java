package com.sams.ui;

import com.sams.model.User;
import com.sams.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Login screen — first view shown when the application starts.
 */
public class LoginView {

    private final Stage stage;
    private final AuthService authService = new AuthService();
    private StackPane root;

    public LoginView(Stage stage) {
        this.stage = stage;
        buildUI();
    }

    public Parent getView() {
        return root;
    }

    private void buildUI() {
        root = new StackPane();
        root.getStyleClass().add("login-background");

        // Login card container
        VBox card = new VBox(16);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(420);
        card.setMaxHeight(520);
        card.setAlignment(Pos.CENTER);

        // Icon / Brand
        Label icon = new Label("📋");
        icon.setFont(Font.font(48));

        Label title = new Label("SAMS");
        title.getStyleClass().add("login-title");

        Label subtitle = new Label("Student Attendance Management System");
        subtitle.getStyleClass().add("login-subtitle");

        Region gap = new Region();
        gap.setPrefHeight(8);

        // Form fields
        Label userLabel = new Label("USERNAME");
        userLabel.getStyleClass().add("field-label");

        TextField userField = new TextField();
        userField.setPromptText("Enter your username");
        userField.setPrefHeight(42);

        Label passLabel = new Label("PASSWORD");
        passLabel.getStyleClass().add("field-label");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter your password");
        passField.setPrefHeight(42);

        // Error message
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#e74c3c"));
        errorLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Region gap2 = new Region();
        gap2.setPrefHeight(4);

        // Login button
        Button loginBtn = new Button("Sign In");
        loginBtn.getStyleClass().add("btn-primary");
        loginBtn.setPrefHeight(44);
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        // Credentials hint
        Label hint = new Label("Demo: admin / admin123  •  john.smith / lecturer123");
        hint.getStyleClass().add("label-muted");
        hint.setWrapText(true);
        hint.setAlignment(Pos.CENTER);

        // handle login submit
        Runnable doLogin = () -> {
            String username = userField.getText();
            String password = passField.getText();

            if (username.isBlank() || password.isBlank()) {
                errorLabel.setText("Please enter both username and password.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }

            User user = authService.login(username, password);
            if (user != null) {
                navigateToDashboard(user);
            } else {
                errorLabel.setText("Invalid username or password. Please try again.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                passField.clear();
            }
        };

        loginBtn.setOnAction(e -> doLogin.run());
        passField.setOnAction(e -> doLogin.run());
        userField.setOnAction(e -> passField.requestFocus());

        card.getChildren().addAll(
            icon, title, subtitle, gap,
            userLabel, userField,
            passLabel, passField,
            errorLabel, gap2,
            loginBtn, hint
        );

        root.getChildren().add(card);
    }

    private void navigateToDashboard(User user) {
        DashboardView dashboard = new DashboardView(stage, user);
        Scene scene = new Scene(dashboard.getView(), stage.getWidth(), stage.getHeight());
        scene.getStylesheets().add(
            getClass().getResource("/style.css").toExternalForm()
        );
        stage.setScene(scene);
    }
}
