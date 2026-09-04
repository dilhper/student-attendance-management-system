package com.sams.ui.components;

import com.sams.model.User;
import com.sams.model.UserRole;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Navigation sidebar with role-based menu items.
 */
public class Sidebar extends VBox {

    private final User currentUser;
    private final Consumer<String> onNavigate;
    private Button activeButton;

    public Sidebar(User currentUser, Consumer<String> onNavigate) {
        this.currentUser = currentUser;
        this.onNavigate = onNavigate;
        getStyleClass().add("sidebar");
        setPrefWidth(250);
        setMinWidth(250);
        buildUI();
    }

    private void buildUI() {
        // ── Header / Brand ──
        VBox header = new VBox(4);
        header.getStyleClass().add("sidebar-header");

        Label brand = new Label("📋 SAMS");
        brand.getStyleClass().add("sidebar-brand");

        Label tagline = new Label("Attendance Management");
        tagline.getStyleClass().add("sidebar-tagline");

        header.getChildren().addAll(brand, tagline);

        // ── Navigation Items ──
        VBox nav = new VBox(2);
        nav.setPadding(new Insets(12, 0, 12, 0));

        // Section label
        Label mainLabel = new Label("  MAIN MENU");
        mainLabel.getStyleClass().add("sidebar-section-label");
        nav.getChildren().add(mainLabel);

        Button btnDashboard = createNavButton("📊  Dashboard", "dashboard");
        nav.getChildren().add(btnDashboard);

        if (currentUser.getRole() == UserRole.ADMIN) {
            Label mgmtLabel = new Label("  MANAGEMENT");
            mgmtLabel.getStyleClass().add("sidebar-section-label");
            nav.getChildren().add(mgmtLabel);

            nav.getChildren().addAll(
                createNavButton("📚  Courses", "courses"),
                createNavButton("🎓  Students", "students"),
                createNavButton("👨‍🏫  Lecturers", "lecturers")
            );
        }

        Label schedLabel = new Label("  SCHEDULING");
        schedLabel.getStyleClass().add("sidebar-section-label");
        nav.getChildren().add(schedLabel);

        nav.getChildren().add(createNavButton("📅  Class Schedule", "schedule"));

        if (currentUser.getRole() == UserRole.LECTURER) {
            nav.getChildren().add(createNavButton("✅  Mark Attendance", "attendance"));
        }
        if (currentUser.getRole() == UserRole.ADMIN) {
            nav.getChildren().add(createNavButton("✅  Mark Attendance", "attendance"));
        }

        Label reportLabel = new Label("  REPORTS");
        reportLabel.getStyleClass().add("sidebar-section-label");
        nav.getChildren().add(reportLabel);

        nav.getChildren().add(createNavButton("📋  Attendance Reports", "reports"));

        // ── Spacer ──
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // ── User Info + Logout ──
        VBox userBox = new VBox(4);
        userBox.getStyleClass().add("sidebar-user-box");

        Label username = new Label(currentUser.getUsername());
        username.getStyleClass().add("sidebar-username");

        Label role = new Label(currentUser.getRole().name());
        role.getStyleClass().add("sidebar-role");

        Button logoutBtn = new Button("🚪  Logout");
        logoutBtn.getStyleClass().addAll("sidebar-btn");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> onNavigate.accept("logout"));

        userBox.getChildren().addAll(username, role, new Separator(), logoutBtn);

        getChildren().addAll(header, nav, spacer, userBox);

        // Activate dashboard by default
        setActive(btnDashboard);
    }

    private Button createNavButton(String text, String viewId) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            setActive(btn);
            onNavigate.accept(viewId);
        });
        return btn;
    }

    private void setActive(Button btn) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("sidebar-btn-active");
        }
        btn.getStyleClass().add("sidebar-btn-active");
        activeButton = btn;
    }
}
