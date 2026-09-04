package com.sams;

import com.sams.ui.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Main class to launch the SAMS desktop application
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // initialize login screen
        LoginView loginView = new LoginView(primaryStage);

        Scene scene = new Scene(loginView.getView(), 1280, 800);
        scene.getStylesheets().add(
            getClass().getResource("/style.css").toExternalForm()
        );

        primaryStage.setTitle("Student Attendance Management System (SAMS)");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
