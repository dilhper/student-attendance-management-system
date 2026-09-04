package com.sams.ui;

import com.sams.model.Course;
import com.sams.model.Subject;
import com.sams.model.User;
import com.sams.service.CourseService;
import com.sams.service.SubjectService;
import com.sams.ui.components.AlertHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Course management view — full CRUD for courses and their subjects.
 */
public class CourseView {

    private final User currentUser;
    private final CourseService courseService = new CourseService();
    private final SubjectService subjectService = new SubjectService();
    private VBox root;
    private TableView<Course> table;
    private ObservableList<Course> courseList;

    public CourseView(User currentUser) {
        this.currentUser = currentUser;
        buildUI();
    }

    public Parent getView() { return root; }

    private void buildUI() {
        root = new VBox(16);
        root.setPadding(new Insets(8));

        // ── Header ──
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Course Management");
        title.getStyleClass().add("page-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Add Course");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> showCourseDialog(null));

        header.getChildren().addAll(title, spacer, addBtn);

        // ── Search ──
        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Search courses...");
        searchField.getStyleClass().add("search-field");
        searchField.setMaxWidth(400);

        // ── Table ──
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Course, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCourseCode()));
        codeCol.setPrefWidth(120);

        TableColumn<Course, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCourseName()));

        TableColumn<Course, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

        TableColumn<Course, String> subCountCol = new TableColumn<>("Subjects");
        subCountCol.setCellValueFactory(c -> {
            int count = subjectService.getSubjectsByCourse(c.getValue().getCourseId()).size();
            return new SimpleStringProperty(String.valueOf(count));
        });
        subCountCol.setPrefWidth(100);

        TableColumn<Course, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(260);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏ Edit");
            private final Button subjBtn = new Button("📚 Subjects");
            private final Button delBtn = new Button("🗑 Delete");
            {
                editBtn.getStyleClass().add("btn-secondary");
                subjBtn.getStyleClass().add("btn-secondary");
                delBtn.getStyleClass().add("btn-danger");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Course course = getTableView().getItems().get(getIndex());
                    editBtn.setOnAction(e -> showCourseDialog(course));
                    subjBtn.setOnAction(e -> showSubjectsDialog(course));
                    delBtn.setOnAction(e -> deleteCourse(course));
                    HBox box = new HBox(8, editBtn, subjBtn, delBtn);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        table.getColumns().addAll(codeCol, nameCol, descCol, subCountCol, actionsCol);

        // Load data
        loadData();

        // Search filter
        FilteredList<Course> filteredData = new FilteredList<>(courseList, p -> true);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(course -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lc = newVal.toLowerCase();
                return course.getCourseCode().toLowerCase().contains(lc)
                    || course.getCourseName().toLowerCase().contains(lc);
            });
        });
        table.setItems(filteredData);

        root.getChildren().addAll(header, searchField, table);
    }

    private void loadData() {
        courseList = FXCollections.observableArrayList(courseService.getAllCourses());
        table.setItems(courseList);
    }

    private void showCourseDialog(Course existing) {
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Course" : "Edit Course");
        dialog.setHeaderText(existing == null ? "Create a new course" : "Edit course details");

        DialogPane pane = dialog.getDialogPane();
        pane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        pane.setStyle("-fx-background-color: #1E293B;");
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(16));

        TextField codeField = new TextField(existing != null ? existing.getCourseCode() : "");
        codeField.setPromptText("e.g. CS101");

        TextField nameField = new TextField(existing != null ? existing.getCourseName() : "");
        nameField.setPromptText("e.g. BSc Computer Science");

        TextArea descField = new TextArea(existing != null ? existing.getDescription() : "");
        descField.setPromptText("Course description...");
        descField.setPrefRowCount(3);

        Label codeLbl = new Label("Course Code");   codeLbl.getStyleClass().add("field-label");
        Label nameLbl = new Label("Course Name");    nameLbl.getStyleClass().add("field-label");
        Label descLbl = new Label("Description");    descLbl.getStyleClass().add("field-label");

        grid.add(codeLbl, 0, 0); grid.add(codeField, 1, 0);
        grid.add(nameLbl, 0, 1); grid.add(nameField, 1, 1);
        grid.add(descLbl, 0, 2); grid.add(descField, 1, 2);

        pane.setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                Course c = existing != null ? existing : new Course();
                c.setCourseCode(codeField.getText().trim());
                c.setCourseName(nameField.getText().trim());
                c.setDescription(descField.getText().trim());
                return c;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(course -> {
            boolean success;
            if (existing != null) {
                success = courseService.updateCourse(course);
            } else {
                success = courseService.saveCourse(course);
            }
            if (success) {
                AlertHelper.showSuccess("Course saved successfully!");
                loadData();
            } else {
                AlertHelper.showError("Error", "Failed to save course. Check that the code is unique and all fields are filled.");
            }
        });
    }

    private void showSubjectsDialog(Course course) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Subjects — " + course.getCourseCode());
        dialog.setHeaderText("Manage subjects for " + course.getCourseName());

        DialogPane pane = dialog.getDialogPane();
        pane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        pane.setStyle("-fx-background-color: #1E293B;");
        pane.getButtonTypes().add(ButtonType.CLOSE);
        pane.setPrefWidth(600);
        pane.setPrefHeight(450);

        VBox content = new VBox(12);
        content.setPadding(new Insets(8));

        // Subject table
        TableView<Subject> subTable = new TableView<>();
        subTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(subTable, Priority.ALWAYS);

        TableColumn<Subject, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubjectCode()));

        TableColumn<Subject, String> nameCol = new TableColumn<>("Subject Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubjectName()));

        TableColumn<Subject, Void> actCol = new TableColumn<>("Actions");
        actCol.setPrefWidth(180);
        actCol.setCellFactory(col -> new TableCell<>() {
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
                Subject sub = getTableView().getItems().get(getIndex());
                editBtn.setOnAction(e -> {
                    showSubjectForm(sub, course, subTable);
                });
                delBtn.setOnAction(e -> {
                    if (AlertHelper.showConfirm("Delete Subject", "Delete " + sub.getSubjectName() + "?")) {
                        subjectService.deleteSubject(sub.getSubjectId());
                        refreshSubjectTable(course, subTable);
                    }
                });
                setGraphic(new HBox(8, editBtn, delBtn));
            }
        });

        subTable.getColumns().addAll(codeCol, nameCol, actCol);
        refreshSubjectTable(course, subTable);

        Button addSubBtn = new Button("+ Add Subject");
        addSubBtn.getStyleClass().add("btn-primary");
        addSubBtn.setOnAction(e -> showSubjectForm(null, course, subTable));

        content.getChildren().addAll(addSubBtn, subTable);
        pane.setContent(content);
        dialog.showAndWait();

        // Refresh main course table to update subject count
        loadData();
    }

    private void showSubjectForm(Subject existing, Course course, TableView<Subject> subTable) {
        Dialog<Subject> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "Add Subject" : "Edit Subject");
        DialogPane dp = dlg.getDialogPane();
        dp.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        dp.setStyle("-fx-background-color: #1E293B;");
        dp.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane g = new GridPane();
        g.setHgap(12); g.setVgap(12); g.setPadding(new Insets(16));

        TextField codeF = new TextField(existing != null ? existing.getSubjectCode() : "");
        TextField nameF = new TextField(existing != null ? existing.getSubjectName() : "");

        Label cl = new Label("Subject Code"); cl.getStyleClass().add("field-label");
        Label nl = new Label("Subject Name"); nl.getStyleClass().add("field-label");

        g.add(cl, 0, 0); g.add(codeF, 1, 0);
        g.add(nl, 0, 1); g.add(nameF, 1, 1);
        dp.setContent(g);

        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                Subject s = existing != null ? existing : new Subject();
                s.setSubjectCode(codeF.getText().trim());
                s.setSubjectName(nameF.getText().trim());
                s.setCourseId(course.getCourseId());
                return s;
            }
            return null;
        });

        dlg.showAndWait().ifPresent(sub -> {
            boolean ok = existing != null ? subjectService.updateSubject(sub) : subjectService.saveSubject(sub);
            if (ok) {
                AlertHelper.showSuccess("Subject saved!");
                refreshSubjectTable(course, subTable);
            } else {
                AlertHelper.showError("Error", "Failed to save subject.");
            }
        });
    }

    private void refreshSubjectTable(Course course, TableView<Subject> subTable) {
        subTable.setItems(FXCollections.observableArrayList(
            subjectService.getSubjectsByCourse(course.getCourseId())
        ));
    }

    private void deleteCourse(Course course) {
        if (AlertHelper.showConfirm("Delete Course",
                "Are you sure you want to delete \"" + course.getCourseName() + "\"?\n"
                + "This will also delete all associated subjects, sessions, and attendance records.")) {
            if (courseService.deleteCourse(course.getCourseId())) {
                AlertHelper.showSuccess("Course deleted.");
                loadData();
            } else {
                AlertHelper.showError("Error", "Failed to delete course.");
            }
        }
    }
}
