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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Class scheduling view — create, view, edit and delete class sessions.
 */
public class ClassSessionView {

    private final User currentUser;
    private final ClassSessionService sessionService = new ClassSessionService();
    private final SubjectService subjectService = new SubjectService();
    private final LecturerService lecturerService = new LecturerService();
    private final CourseService courseService = new CourseService();
    private VBox root;
    private TableView<ClassSession> table;
    private ObservableList<ClassSession> sessionList;

    public ClassSessionView(User currentUser) {
        this.currentUser = currentUser;
        buildUI();
    }

    public Parent getView() { return root; }

    private void buildUI() {
        root = new VBox(16);
        root.setPadding(new Insets(8));

        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;

        // ── Header ──
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(isAdmin ? "Class Schedule Management" : "My Class Schedule");
        title.getStyleClass().add("page-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer);

        if (isAdmin) {
            Button addBtn = new Button("+ Schedule Class");
            addBtn.getStyleClass().add("btn-primary");
            addBtn.setOnAction(e -> showSessionDialog(null));
            header.getChildren().add(addBtn);
        }

        // ── Table ──
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<ClassSession, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSessionDate().toString()));
        dateCol.setPrefWidth(110);

        TableColumn<ClassSession, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTimeSlot()));
        timeCol.setPrefWidth(130);

        TableColumn<ClassSession, String> subjCol = new TableColumn<>("Subject");
        subjCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getSubjectCode() + " — " + c.getValue().getSubjectName()));

        TableColumn<ClassSession, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCourseName()));

        TableColumn<ClassSession, String> lecCol = new TableColumn<>("Lecturer");
        lecCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLecturerName()));

        TableColumn<ClassSession, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRoom()));
        roomCol.setPrefWidth(100);

        table.getColumns().addAll(dateCol, timeCol, subjCol, courseCol, lecCol, roomCol);

        if (isAdmin) {
            TableColumn<ClassSession, Void> actionsCol = new TableColumn<>("Actions");
            actionsCol.setPrefWidth(180);
            actionsCol.setCellFactory(col -> new TableCell<>() {
                private final Button editBtn = new Button("✏ Edit");
                private final Button delBtn = new Button("🗑 Delete");
                {
                    editBtn.getStyleClass().add("btn-secondary");
                    delBtn.getStyleClass().add("btn-danger");
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) { setGraphic(null); return; }
                    ClassSession s = getTableView().getItems().get(getIndex());
                    editBtn.setOnAction(e -> showSessionDialog(s));
                    delBtn.setOnAction(e -> deleteSession(s));
                    setGraphic(new HBox(8, editBtn, delBtn));
                }
            });
            table.getColumns().add(actionsCol);
        }

        loadData();

        root.getChildren().addAll(header, table);
    }

    private void loadData() {
        List<ClassSession> sessions;
        if (currentUser.getRole() == UserRole.LECTURER && currentUser.getLecturerId() != null) {
            sessions = sessionService.getSessionsByLecturer(currentUser.getLecturerId());
        } else {
            sessions = sessionService.getAllSessions();
        }
        sessionList = FXCollections.observableArrayList(sessions);
        table.setItems(sessionList);
    }

    private void showSessionDialog(ClassSession existing) {
        Dialog<ClassSession> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Schedule New Class" : "Edit Class Session");

        DialogPane pane = dialog.getDialogPane();
        pane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        pane.setStyle("-fx-background-color: #1E293B;");
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(16));

        // Course selector (to filter subjects)
        ComboBox<Course> courseBox = new ComboBox<>();
        courseBox.getItems().addAll(courseService.getAllCourses());
        courseBox.setPromptText("Select Course");
        courseBox.setPrefWidth(300);

        // Subject selector (filtered by course)
        ComboBox<Subject> subjectBox = new ComboBox<>();
        subjectBox.setPromptText("Select Subject");
        subjectBox.setPrefWidth(300);

        courseBox.valueProperty().addListener((obs, old, newCourse) -> {
            subjectBox.getItems().clear();
            if (newCourse != null) {
                subjectBox.getItems().addAll(
                    subjectService.getSubjectsByCourse(newCourse.getCourseId())
                );
            }
        });

        // Lecturer selector
        ComboBox<Lecturer> lecturerBox = new ComboBox<>();
        lecturerBox.getItems().addAll(lecturerService.getAllLecturers());
        lecturerBox.setPromptText("Select Lecturer");
        lecturerBox.setPrefWidth(300);

        // Date picker
        DatePicker datePicker = new DatePicker(existing != null ? existing.getSessionDate() : LocalDate.now());

        // Time fields
        ComboBox<String> startTimeBox = new ComboBox<>();
        ComboBox<String> endTimeBox = new ComboBox<>();
        for (int h = 8; h <= 20; h++) {
            for (int m = 0; m < 60; m += 30) {
                String time = String.format("%02d:%02d", h, m);
                startTimeBox.getItems().add(time);
                endTimeBox.getItems().add(time);
            }
        }
        startTimeBox.setPromptText("Start Time");
        endTimeBox.setPromptText("End Time");

        TextField roomField = new TextField(existing != null ? existing.getRoom() : "");
        roomField.setPromptText("e.g. Room A101");

        // Pre-fill for edit mode
        if (existing != null) {
            courseBox.getItems().stream()
                .filter(c -> c.getCourseId() == existing.getCourseId())
                .findFirst().ifPresent(c -> {
                    courseBox.setValue(c);
                    subjectBox.getItems().addAll(
                        subjectService.getSubjectsByCourse(c.getCourseId())
                    );
                });
            subjectBox.getItems().stream()
                .filter(s -> s.getSubjectId() == existing.getSubjectId())
                .findFirst().ifPresent(subjectBox::setValue);
            lecturerBox.getItems().stream()
                .filter(l -> l.getLecturerId() == existing.getLecturerId())
                .findFirst().ifPresent(lecturerBox::setValue);
            startTimeBox.setValue(existing.getStartTime().toString().substring(0, 5));
            endTimeBox.setValue(existing.getEndTime().toString().substring(0, 5));
        }

        String[] labels = {"Course", "Subject", "Lecturer", "Date", "Start Time", "End Time", "Room"};
        Control[] fields = {courseBox, subjectBox, lecturerBox, datePicker, startTimeBox, endTimeBox, roomField};

        for (int i = 0; i < labels.length; i++) {
            Label l = new Label(labels[i]);
            l.getStyleClass().add("field-label");
            grid.add(l, 0, i);
            grid.add(fields[i], 1, i);
        }

        pane.setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                if (subjectBox.getValue() == null || lecturerBox.getValue() == null
                    || datePicker.getValue() == null || startTimeBox.getValue() == null
                    || endTimeBox.getValue() == null) {
                    AlertHelper.showError("Validation", "Please fill in all required fields.");
                    return null;
                }
                ClassSession s = existing != null ? existing : new ClassSession();
                s.setSubjectId(subjectBox.getValue().getSubjectId());
                s.setLecturerId(lecturerBox.getValue().getLecturerId());
                s.setSessionDate(datePicker.getValue());
                s.setStartTime(LocalTime.parse(startTimeBox.getValue()));
                s.setEndTime(LocalTime.parse(endTimeBox.getValue()));
                s.setRoom(roomField.getText().trim());
                return s;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(session -> {
            boolean ok = existing != null ? sessionService.updateSession(session) : sessionService.saveSession(session);
            if (ok) {
                AlertHelper.showSuccess("Class session saved!");
                loadData();
            } else {
                AlertHelper.showError("Error", "Failed to save session. Ensure end time is after start time.");
            }
        });
    }

    private void deleteSession(ClassSession session) {
        if (AlertHelper.showConfirm("Delete Session",
                "Delete the " + session.getSubjectName() + " session on " + session.getSessionDate() + "?")) {
            if (sessionService.deleteSession(session.getSessionId())) {
                AlertHelper.showSuccess("Session deleted.");
                loadData();
            } else {
                AlertHelper.showError("Error", "Failed to delete session.");
            }
        }
    }
}
