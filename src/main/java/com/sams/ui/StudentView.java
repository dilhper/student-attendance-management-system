package com.sams.ui;

import com.sams.model.Course;
import com.sams.model.Student;
import com.sams.model.User;
import com.sams.service.CourseService;
import com.sams.service.StudentService;
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
 * Student management view — full CRUD for student records.
 */
public class StudentView {

    private final User currentUser;
    private final StudentService studentService = new StudentService();
    private final CourseService courseService = new CourseService();
    private VBox root;
    private TableView<Student> table;
    private ObservableList<Student> studentList;

    public StudentView(User currentUser) {
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

        Label title = new Label("Student Management");
        title.getStyleClass().add("page-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Add Student");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> showStudentDialog(null));

        header.getChildren().addAll(title, spacer, addBtn);

        // ── Filters Row ──
        HBox filters = new HBox(12);
        filters.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Search by name or reg number...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(300);

        ComboBox<Course> courseFilter = new ComboBox<>();
        courseFilter.setPromptText("Filter by Course");
        courseFilter.setPrefWidth(250);
        List<Course> courses = courseService.getAllCourses();
        Course allCourse = new Course(0, "", "All Courses", "");
        courseFilter.getItems().add(allCourse);
        courseFilter.getItems().addAll(courses);
        courseFilter.setValue(allCourse);

        filters.getChildren().addAll(searchField, courseFilter);

        // ── Table ──
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Student, String> regCol = new TableColumn<>("Reg. Number");
        regCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRegistrationNumber()));
        regCol.setPrefWidth(130);

        TableColumn<Student, String> fnCol = new TableColumn<>("First Name");
        fnCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFirstName()));

        TableColumn<Student, String> lnCol = new TableColumn<>("Last Name");
        lnCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLastName()));

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));

        TableColumn<Student, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        phoneCol.setPrefWidth(130);

        TableColumn<Student, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCourseName()));

        TableColumn<Student, Void> actionsCol = new TableColumn<>("Actions");
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
                Student student = getTableView().getItems().get(getIndex());
                editBtn.setOnAction(e -> showStudentDialog(student));
                delBtn.setOnAction(e -> deleteStudent(student));
                HBox box = new HBox(8, editBtn, delBtn);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(regCol, fnCol, lnCol, emailCol, phoneCol, courseCol, actionsCol);

        // Load data & apply filters
        loadData();

        FilteredList<Student> filteredData = new FilteredList<>(studentList, p -> true);

        Runnable applyFilters = () -> {
            String search = searchField.getText();
            Course selCourse = courseFilter.getValue();
            filteredData.setPredicate(student -> {
                boolean matchSearch = true;
                if (search != null && !search.isEmpty()) {
                    String lc = search.toLowerCase();
                    matchSearch = student.getFirstName().toLowerCase().contains(lc)
                        || student.getLastName().toLowerCase().contains(lc)
                        || student.getRegistrationNumber().toLowerCase().contains(lc);
                }
                boolean matchCourse = selCourse == null || selCourse.getCourseId() == 0
                    || student.getCourseId() == selCourse.getCourseId();
                return matchSearch && matchCourse;
            });
        };

        searchField.textProperty().addListener((obs, o, n) -> applyFilters.run());
        courseFilter.valueProperty().addListener((obs, o, n) -> applyFilters.run());
        table.setItems(filteredData);

        root.getChildren().addAll(header, filters, table);
    }

    private void loadData() {
        studentList = FXCollections.observableArrayList(studentService.getAllStudents());
        table.setItems(studentList);
    }

    private void showStudentDialog(Student existing) {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Student" : "Edit Student");
        dialog.setHeaderText(existing == null ? "Register a new student" : "Edit student details");

        DialogPane pane = dialog.getDialogPane();
        pane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        pane.setStyle("-fx-background-color: #1a1a2e;");
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(16));

        TextField regField = new TextField(existing != null ? existing.getRegistrationNumber() : "");
        regField.setPromptText("e.g. STU-2024-017");
        TextField fnField = new TextField(existing != null ? existing.getFirstName() : "");
        TextField lnField = new TextField(existing != null ? existing.getLastName() : "");
        TextField emailField = new TextField(existing != null ? existing.getEmail() : "");
        TextField phoneField = new TextField(existing != null ? existing.getPhone() : "");

        ComboBox<Course> courseBox = new ComboBox<>();
        courseBox.getItems().addAll(courseService.getAllCourses());
        courseBox.setPromptText("Select Course");
        if (existing != null) {
            courseBox.getItems().stream()
                .filter(c -> c.getCourseId() == existing.getCourseId())
                .findFirst().ifPresent(courseBox::setValue);
        }

        String[] labels = {"Reg. Number", "First Name", "Last Name", "Email", "Phone", "Course"};
        Control[] fields = {regField, fnField, lnField, emailField, phoneField, courseBox};

        for (int i = 0; i < labels.length; i++) {
            Label l = new Label(labels[i]);
            l.getStyleClass().add("field-label");
            grid.add(l, 0, i);
            grid.add(fields[i], 1, i);
        }

        pane.setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                Student s = existing != null ? existing : new Student();
                s.setRegistrationNumber(regField.getText().trim());
                s.setFirstName(fnField.getText().trim());
                s.setLastName(lnField.getText().trim());
                s.setEmail(emailField.getText().trim());
                s.setPhone(phoneField.getText().trim());
                if (courseBox.getValue() != null) {
                    s.setCourseId(courseBox.getValue().getCourseId());
                }
                return s;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(student -> {
            boolean ok = existing != null ? studentService.updateStudent(student) : studentService.saveStudent(student);
            if (ok) {
                AlertHelper.showSuccess("Student saved successfully!");
                loadData();
            } else {
                AlertHelper.showError("Error", "Failed to save student. Check all required fields.");
            }
        });
    }

    private void deleteStudent(Student student) {
        if (AlertHelper.showConfirm("Delete Student",
                "Delete " + student.getFullName() + " (" + student.getRegistrationNumber() + ")?")) {
            if (studentService.deleteStudent(student.getStudentId())) {
                AlertHelper.showSuccess("Student deleted.");
                loadData();
            } else {
                AlertHelper.showError("Error", "Failed to delete student.");
            }
        }
    }
}
