package com.sams;

import com.sams.ui.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main entry point for the Student Attendance Management System.
 * Launches the JavaFX application and displays the login screen.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        LoginView loginView = new LoginView(primaryStage);

        Scene scene = new Scene(loginView.getView(), 1280, 800);
        scene.getStylesheets().add(
            getClass().getResource("/style.css").toExternalForm()
        );

        primaryStage.setTitle("SAMS — Student Attendance Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
