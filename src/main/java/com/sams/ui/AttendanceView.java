package com.sams.ui;

import com.sams.model.*;
import com.sams.service.*;
import com.sams.ui.components.AlertHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Attendance marking view — lecturers select a session and mark attendance for each student.
 */
public class AttendanceView {

    private final User currentUser;
    private final ClassSessionService sessionService = new ClassSessionService();
    private final StudentService studentService = new StudentService();
    private final AttendanceService attendanceService = new AttendanceService();
    private VBox root;

    public AttendanceView(User currentUser) {
        this.currentUser = currentUser;
        buildUI();
    }

    public Parent getView() { return root; }

    private void buildUI() {
        root = new VBox(20);
        root.setPadding(new Insets(8));

        Label title = new Label("Mark Attendance");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Select a class session, then mark each student's attendance.");
        subtitle.getStyleClass().add("page-subtitle");

        // session dropdown selection
        HBox selectorRow = new HBox(12);
        selectorRow.setAlignment(Pos.CENTER_LEFT);

        Label sessionLabel = new Label("Class Session:");
        sessionLabel.getStyleClass().add("field-label");
        sessionLabel.setStyle("-fx-font-size: 14px;");

        ComboBox<ClassSession> sessionBox = new ComboBox<>();
        sessionBox.setPromptText("Select a class session");
        sessionBox.setPrefWidth(500);

        // Load sessions based on role
        List<ClassSession> sessions;
        if (currentUser.getRole() == UserRole.LECTURER && currentUser.getLecturerId() != null) {
            sessions = sessionService.getSessionsByLecturer(currentUser.getLecturerId());
        } else {
            sessions = sessionService.getAllSessions();
        }
        sessionBox.getItems().addAll(sessions);

        selectorRow.getChildren().addAll(sessionLabel, sessionBox);

        // attendance table card
        VBox attendanceCard = new VBox(12);
        attendanceCard.getStyleClass().add("card");
        attendanceCard.setVisible(false);
        attendanceCard.setManaged(false);
        VBox.setVgrow(attendanceCard, Priority.ALWAYS);

        Label attTitle = new Label("Student Attendance");
        attTitle.setStyle("-fx-text-fill: #14B8A6; -fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<AttendanceRow> attTable = new TableView<>();
        attTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(attTable, Priority.ALWAYS);

        TableColumn<AttendanceRow, String> regCol = new TableColumn<>("Reg. Number");
        regCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().registrationNumber));
        regCol.setPrefWidth(130);

        TableColumn<AttendanceRow, String> nameCol = new TableColumn<>("Student Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().studentName));

        TableColumn<AttendanceRow, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(160);
        statusCol.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<AttendanceStatus> comboBox = new ComboBox<>(
                FXCollections.observableArrayList(AttendanceStatus.values())
            );
            {
                comboBox.setMaxWidth(Double.MAX_VALUE);
                comboBox.setOnAction(e -> {
                    AttendanceRow row = getTableView().getItems().get(getIndex());
                    row.status = comboBox.getValue();
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                AttendanceRow row = getTableView().getItems().get(getIndex());
                comboBox.setValue(row.status);
                setGraphic(comboBox);
            }
        });

        attTable.getColumns().addAll(regCol, nameCol, statusCol);

        // Save button
        Button saveBtn = new Button("💾  Save Attendance");
        saveBtn.getStyleClass().add("btn-success");
        saveBtn.setStyle("-fx-font-size: 15px; -fx-padding: 12 32;");

        attendanceCard.getChildren().addAll(attTitle, attTable, saveBtn);

        // handle session selection change
        ObservableList<AttendanceRow> rows = FXCollections.observableArrayList();

        sessionBox.valueProperty().addListener((obs, old, selected) -> {
            if (selected == null) {
                attendanceCard.setVisible(false);
                attendanceCard.setManaged(false);
                return;
            }

            // Load students for this session's course
            List<Student> students = studentService.getStudentsBySession(selected.getSessionId());

            // Load existing attendance
            List<Attendance> existing = attendanceService.getAttendanceBySession(selected.getSessionId());
            Map<Integer, AttendanceStatus> existingMap = existing.stream()
                .collect(Collectors.toMap(Attendance::getStudentId, Attendance::getStatus));

            rows.clear();
            for (Student s : students) {
                AttendanceRow row = new AttendanceRow();
                row.studentId = s.getStudentId();
                row.registrationNumber = s.getRegistrationNumber();
                row.studentName = s.getFullName();
                row.status = existingMap.getOrDefault(s.getStudentId(), AttendanceStatus.PRESENT);
                rows.add(row);
            }

            attTable.setItems(rows);
            attTitle.setText("Attendance — " + selected.getSubjectName() + " (" + selected.getSessionDate() + ")");
            attendanceCard.setVisible(true);
            attendanceCard.setManaged(true);
        });

        // save attendance button action
        saveBtn.setOnAction(e -> {
            ClassSession selectedSession = sessionBox.getValue();
            if (selectedSession == null || rows.isEmpty()) {
                AlertHelper.showError("Error", "No session selected or no students to mark.");
                return;
            }

            List<Attendance> records = rows.stream().map(row -> {
                Attendance att = new Attendance();
                att.setSessionId(selectedSession.getSessionId());
                att.setStudentId(row.studentId);
                att.setStatus(row.status);
                return att;
            }).collect(Collectors.toList());

            if (attendanceService.saveAttendance(records)) {
                AlertHelper.showSuccess("Attendance saved successfully for " + records.size() + " students!");
            } else {
                AlertHelper.showError("Error", "Failed to save attendance.");
            }
        });

        root.getChildren().addAll(title, subtitle, selectorRow, attendanceCard);
    }

    /**
     * Internal helper class to hold per-row attendance data for the table.
     */
    private static class AttendanceRow {
        int studentId;
        String registrationNumber;
        String studentName;
        AttendanceStatus status;
    }
}
