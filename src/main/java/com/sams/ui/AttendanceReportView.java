package com.sams.ui;

import com.sams.model.*;
import com.sams.service.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Attendance reporting view — filterable by student, subject, and date range.
 */
public class AttendanceReportView {

    private final User currentUser;
    private final AttendanceService attendanceService = new AttendanceService();
    private final StudentService studentService = new StudentService();
    private final SubjectService subjectService = new SubjectService();
    private VBox root;
    private TableView<Attendance> table;
    private Label summaryLabel;

    public AttendanceReportView(User currentUser) {
        this.currentUser = currentUser;
        buildUI();
    }

    public Parent getView() { return root; }

    private void buildUI() {
        root = new VBox(16);
        root.setPadding(new Insets(8));

        Label title = new Label("Attendance Reports");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Filter and view attendance records across students, subjects, and dates.");
        subtitle.getStyleClass().add("page-subtitle");

        // ── Filter Controls ──
        VBox filterCard = new VBox(12);
        filterCard.getStyleClass().add("card");

        Label filterTitle = new Label("Filter Options");
        filterTitle.setStyle("-fx-text-fill: #e94560; -fx-font-size: 16px; -fx-font-weight: bold;");

        HBox filtersRow1 = new HBox(16);
        filtersRow1.setAlignment(Pos.CENTER_LEFT);

        // Student filter
        VBox studentFilterBox = new VBox(4);
        Label studentLbl = new Label("Student");
        studentLbl.getStyleClass().add("field-label");
        ComboBox<Student> studentFilter = new ComboBox<>();
        studentFilter.setPromptText("All Students");
        studentFilter.setPrefWidth(250);
        Student allStudent = new Student();
        allStudent.setStudentId(0);
        allStudent.setRegistrationNumber("");
        allStudent.setFirstName("All");
        allStudent.setLastName("Students");
        studentFilter.getItems().add(allStudent);
        studentFilter.getItems().addAll(studentService.getAllStudents());
        studentFilter.setValue(allStudent);
        studentFilterBox.getChildren().addAll(studentLbl, studentFilter);

        // Subject filter
        VBox subjectFilterBox = new VBox(4);
        Label subjectLbl = new Label("Subject");
        subjectLbl.getStyleClass().add("field-label");
        ComboBox<Subject> subjectFilter = new ComboBox<>();
        subjectFilter.setPromptText("All Subjects");
        subjectFilter.setPrefWidth(250);
        Subject allSubject = new Subject();
        allSubject.setSubjectId(0);
        allSubject.setSubjectCode("");
        allSubject.setSubjectName("All Subjects");
        subjectFilter.getItems().add(allSubject);
        subjectFilter.getItems().addAll(subjectService.getAllSubjects());
        subjectFilter.setValue(allSubject);
        subjectFilterBox.getChildren().addAll(subjectLbl, subjectFilter);

        filtersRow1.getChildren().addAll(studentFilterBox, subjectFilterBox);

        HBox filtersRow2 = new HBox(16);
        filtersRow2.setAlignment(Pos.CENTER_LEFT);

        // Date range
        VBox fromBox = new VBox(4);
        Label fromLbl = new Label("Date From");
        fromLbl.getStyleClass().add("field-label");
        DatePicker dateFrom = new DatePicker();
        dateFrom.setPromptText("Start Date");
        dateFrom.setPrefWidth(180);
        fromBox.getChildren().addAll(fromLbl, dateFrom);

        VBox toBox = new VBox(4);
        Label toLbl = new Label("Date To");
        toLbl.getStyleClass().add("field-label");
        DatePicker dateTo = new DatePicker();
        dateTo.setPromptText("End Date");
        dateTo.setPrefWidth(180);
        toBox.getChildren().addAll(toLbl, dateTo);

        Button applyBtn = new Button("🔍  Apply Filters");
        applyBtn.getStyleClass().add("btn-primary");
        applyBtn.setStyle("-fx-padding: 10 24;");

        Button clearBtn = new Button("Clear");
        clearBtn.getStyleClass().add("btn-secondary");

        Region fSpacer = new Region();
        HBox.setHgrow(fSpacer, Priority.ALWAYS);

        filtersRow2.getChildren().addAll(fromBox, toBox, fSpacer, applyBtn, clearBtn);

        filterCard.getChildren().addAll(filterTitle, filtersRow1, filtersRow2);

        // ── Summary Stats ──
        HBox statsRow = new HBox(16);
        statsRow.setAlignment(Pos.CENTER_LEFT);
        summaryLabel = new Label("Apply filters to view attendance data.");
        summaryLabel.setStyle("-fx-text-fill: #8888aa; -fx-font-size: 14px;");
        statsRow.getChildren().add(summaryLabel);

        // ── Results Table ──
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Attendance, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSessionDate()));
        dateCol.setPrefWidth(100);

        TableColumn<Attendance, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTimeSlot()));
        timeCol.setPrefWidth(130);

        TableColumn<Attendance, String> regCol = new TableColumn<>("Reg. No.");
        regCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRegistrationNumber()));
        regCol.setPrefWidth(120);

        TableColumn<Attendance, String> nameCol = new TableColumn<>("Student");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentName()));

        TableColumn<Attendance, String> subjCol = new TableColumn<>("Subject");
        subjCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubjectName()));

        TableColumn<Attendance, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCourseName()));

        TableColumn<Attendance, String> lecCol = new TableColumn<>("Lecturer");
        lecCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLecturerName()));

        TableColumn<Attendance, Void> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                Attendance att = getTableRow().getItem();
                Label badge = new Label(att.getStatus().name());
                switch (att.getStatus()) {
                    case PRESENT -> badge.getStyleClass().add("badge-present");
                    case ABSENT -> badge.getStyleClass().add("badge-absent");
                    case LATE -> badge.getStyleClass().add("badge-late");
                }
                setGraphic(badge);
            }
        });

        table.getColumns().addAll(dateCol, timeCol, regCol, nameCol, subjCol, courseCol, lecCol, statusCol);

        // Load initial data (all records)
        applyFilters(null, null, null, null);

        // ── Actions ──
        applyBtn.setOnAction(e -> {
            Integer sId = studentFilter.getValue() != null && studentFilter.getValue().getStudentId() > 0
                ? studentFilter.getValue().getStudentId() : null;
            Integer subId = subjectFilter.getValue() != null && subjectFilter.getValue().getSubjectId() > 0
                ? subjectFilter.getValue().getSubjectId() : null;
            LocalDate from = dateFrom.getValue();
            LocalDate to = dateTo.getValue();
            applyFilters(sId, subId, from, to);
        });

        clearBtn.setOnAction(e -> {
            studentFilter.setValue(allStudent);
            subjectFilter.setValue(allSubject);
            dateFrom.setValue(null);
            dateTo.setValue(null);
            applyFilters(null, null, null, null);
        });

        root.getChildren().addAll(title, subtitle, filterCard, statsRow, table);
    }

    private void applyFilters(Integer studentId, Integer subjectId,
                               LocalDate dateFrom, LocalDate dateTo) {
        List<Attendance> results = attendanceService.getAttendanceReport(
            studentId, subjectId, dateFrom, dateTo
        );

        table.setItems(FXCollections.observableArrayList(results));

        // Calculate summary
        long total = results.size();
        long present = results.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long absent = results.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        long late = results.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();

        if (total > 0) {
            double presentPct = (present * 100.0) / total;
            double absentPct = (absent * 100.0) / total;
            double latePct = (late * 100.0) / total;
            summaryLabel.setText(String.format(
                "📊  Total Records: %d  |  ✅ Present: %d (%.1f%%)  |  ❌ Absent: %d (%.1f%%)  |  ⏰ Late: %d (%.1f%%)",
                total, present, presentPct, absent, absentPct, late, latePct
            ));
            summaryLabel.setStyle("-fx-text-fill: #ccccdd; -fx-font-size: 14px;");
        } else {
            summaryLabel.setText("No attendance records found for the selected filters.");
            summaryLabel.setStyle("-fx-text-fill: #8888aa; -fx-font-size: 14px;");
        }
    }
}
