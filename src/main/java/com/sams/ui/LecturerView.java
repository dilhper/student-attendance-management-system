package com.sams.ui;

import com.sams.model.Lecturer;
import com.sams.model.Subject;
import com.sams.model.User;
import com.sams.service.LecturerService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lecturer management view — full CRUD and subject assignment management.
 */
public class LecturerView {

    private final User currentUser;
    private final LecturerService lecturerService = new LecturerService();
    private final SubjectService subjectService = new SubjectService();
    private VBox root;
    private TableView<Lecturer> table;
    private ObservableList<Lecturer> lecturerList;

    public LecturerView(User currentUser) {
        this.currentUser = currentUser;
        buildUI();
    }

    public Parent getView() { return root; }

    private void buildUI() {
        root = new VBox(16);
        root.setPadding(new Insets(8));

        // header with title and add button
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Lecturer Management");
        title.getStyleClass().add("page-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Add Lecturer");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> showLecturerDialog(null));

        header.getChildren().addAll(title, spacer, addBtn);

        // search input field
        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Search lecturers...");
        searchField.getStyleClass().add("search-field");
        searchField.setMaxWidth(400);

        // table for lecturers
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Lecturer, String> fnCol = new TableColumn<>("First Name");
        fnCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFirstName()));

        TableColumn<Lecturer, String> lnCol = new TableColumn<>("Last Name");
        lnCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLastName()));

        TableColumn<Lecturer, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));

        TableColumn<Lecturer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        phoneCol.setPrefWidth(130);

        TableColumn<Lecturer, String> subjCol = new TableColumn<>("Assigned Subjects");
        subjCol.setCellValueFactory(c -> {
            List<Subject> subs = subjectService.getSubjectsByLecturer(c.getValue().getLecturerId());
            String names = subs.stream().map(Subject::getSubjectName).collect(Collectors.joining(", "));
            return new SimpleStringProperty(names.isEmpty() ? "—" : names);
        });

        TableColumn<Lecturer, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(250);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏ Edit");
            private final Button assignBtn = new Button("📚 Assign");
            private final Button delBtn = new Button("🗑 Delete");
            {
                editBtn.getStyleClass().add("btn-secondary");
                assignBtn.getStyleClass().add("btn-secondary");
                delBtn.getStyleClass().add("btn-danger");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Lecturer lec = getTableView().getItems().get(getIndex());
                editBtn.setOnAction(e -> showLecturerDialog(lec));
                assignBtn.setOnAction(e -> showAssignDialog(lec));
                delBtn.setOnAction(e -> deleteLecturer(lec));
                setGraphic(new HBox(8, editBtn, assignBtn, delBtn));
            }
        });

        table.getColumns().addAll(fnCol, lnCol, emailCol, phoneCol, subjCol, actionsCol);

        loadData();

        FilteredList<Lecturer> filtered = new FilteredList<>(lecturerList, p -> true);
        searchField.textProperty().addListener((obs, o, n) -> {
            filtered.setPredicate(lec -> {
                if (n == null || n.isEmpty()) return true;
                String lc = n.toLowerCase();
                return lec.getFirstName().toLowerCase().contains(lc)
                    || lec.getLastName().toLowerCase().contains(lc)
                    || (lec.getEmail() != null && lec.getEmail().toLowerCase().contains(lc));
            });
        });
        table.setItems(filtered);

        root.getChildren().addAll(header, searchField, table);
    }

    private void loadData() {
        lecturerList = FXCollections.observableArrayList(lecturerService.getAllLecturers());
        table.setItems(lecturerList);
    }

    private void showLecturerDialog(Lecturer existing) {
        Dialog<Lecturer> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Lecturer" : "Edit Lecturer");

        DialogPane pane = dialog.getDialogPane();
        pane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        pane.setStyle("-fx-background-color: #1E293B;");
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(16));

        TextField fnField = new TextField(existing != null ? existing.getFirstName() : "");
        TextField lnField = new TextField(existing != null ? existing.getLastName() : "");
        TextField emailField = new TextField(existing != null ? existing.getEmail() : "");
        TextField phoneField = new TextField(existing != null ? existing.getPhone() : "");

        String[] labels = {"First Name", "Last Name", "Email", "Phone"};
        TextField[] fields = {fnField, lnField, emailField, phoneField};

        for (int i = 0; i < labels.length; i++) {
            Label l = new Label(labels[i]);
            l.getStyleClass().add("field-label");
            grid.add(l, 0, i);
            grid.add(fields[i], 1, i);
        }

        pane.setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                Lecturer l = existing != null ? existing : new Lecturer();
                l.setFirstName(fnField.getText().trim());
                l.setLastName(lnField.getText().trim());
                l.setEmail(emailField.getText().trim());
                l.setPhone(phoneField.getText().trim());
                return l;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(lec -> {
            boolean ok = existing != null ? lecturerService.updateLecturer(lec) : lecturerService.saveLecturer(lec);
            if (ok) {
                AlertHelper.showSuccess("Lecturer saved successfully!");
                loadData();
            } else {
                AlertHelper.showError("Error", "Failed to save lecturer.");
            }
        });
    }

    private void showAssignDialog(Lecturer lecturer) {
        Dialog<List<Integer>> dialog = new Dialog<>();
        dialog.setTitle("Assign Subjects — " + lecturer.getFullName());
        dialog.setHeaderText("Select subjects to assign to " + lecturer.getFullName());

        DialogPane pane = dialog.getDialogPane();
        pane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        pane.setStyle("-fx-background-color: #1E293B;");
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        pane.setPrefWidth(500);

        List<Subject> allSubjects = subjectService.getAllSubjects();
        List<Integer> assigned = lecturerService.getAssignedSubjectIds(lecturer.getLecturerId());

        VBox list = new VBox(8);
        list.setPadding(new Insets(12));

        List<CheckBox> checkBoxes = new ArrayList<>();
        for (Subject sub : allSubjects) {
            CheckBox cb = new CheckBox(sub.getSubjectCode() + " — " + sub.getSubjectName()
                + " (" + sub.getCourseName() + ")");
            cb.setUserData(sub.getSubjectId());
            cb.setSelected(assigned.contains(sub.getSubjectId()));
            cb.setStyle("-fx-text-fill: #ccccdd;");
            checkBoxes.add(cb);
            list.getChildren().add(cb);
        }

        ScrollPane sp = new ScrollPane(list);
        sp.setFitToWidth(true);
        sp.setPrefHeight(300);
        pane.setContent(sp);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return checkBoxes.stream()
                    .filter(CheckBox::isSelected)
                    .map(cb -> (Integer) cb.getUserData())
                    .collect(Collectors.toList());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ids -> {
            if (lecturerService.updateSubjectAssignments(lecturer.getLecturerId(), ids)) {
                AlertHelper.showSuccess("Subject assignments updated!");
                loadData();
            } else {
                AlertHelper.showError("Error", "Failed to update assignments.");
            }
        });
    }

    private void deleteLecturer(Lecturer lecturer) {
        if (AlertHelper.showConfirm("Delete Lecturer",
                "Delete " + lecturer.getFullName() + "? This will remove all their session and assignment records.")) {
            if (lecturerService.deleteLecturer(lecturer.getLecturerId())) {
                AlertHelper.showSuccess("Lecturer deleted.");
                loadData();
            } else {
                AlertHelper.showError("Error", "Failed to delete lecturer.");
            }
        }
    }
}
