# 📋 Student Attendance Management System (SAMS)

A JavaFX desktop application for managing student attendance in educational institutions. Built with a layered (N-Tier) architecture, the system supports two user roles — **Administrator** and **Lecturer** — with distinct access levels for course management, student tracking, class scheduling, attendance marking, and attendance reporting.

---

## 📌 Project Overview

SAMS provides educational institutions with a centralized system to:

- **Manage Courses & Subjects** — Create, update, and delete academic courses and their associated subjects (modules).
- **Manage Students** — Register and maintain student profiles with enrollment information.
- **Manage Lecturers** — Add lecturer profiles and assign them to specific subjects.
- **Schedule Classes** — Create class sessions linking a subject, lecturer, date/time, and room.
- **Mark Attendance** — Lecturers can record student attendance per session as Present, Absent, or Late.
- **Generate Reports** — View attendance reports with filters by student, subject, and date range, with summary statistics.

---

## 🛠️ Technologies Used

| Technology | Version | Purpose |
|---|---|---|
| **Java** | JDK 21+ | Core programming language |
| **JavaFX** | 21.0.2 | Desktop UI framework |
| **MySQL** | 8.x | Relational database |
| **JDBC** | (java.sql) | Database connectivity |
| **Maven** | 3.x | Build tool & dependency management |

---

## 🏗️ Architecture

The application follows a **Layered (N-Tier) Architecture** with clear separation of concerns:

```
┌──────────────────────────────────────────┐
│         Presentation Layer (UI)          │  JavaFX Views (com.sams.ui)
├──────────────────────────────────────────┤
│          Service Layer (BLL)             │  Business Logic (com.sams.service)
├──────────────────────────────────────────┤
│        Data Access Layer (DAL)           │  JDBC / DAO classes (com.sams.dao)
├──────────────────────────────────────────┤
│            Data Layer (DB)               │  MySQL Database (sams_db)
└──────────────────────────────────────────┘
```

### Project Structure

```
sams/
├── pom.xml                         # Maven configuration
├── database.sql                    # MySQL schema + sample data
├── run.bat                         # Windows quick-launch script
├── mvnw.cmd                        # Maven wrapper script
├── README.md                       # Documentation and academic declaration
└── src/main/
    ├── java/com/sams/
    │   ├── Main.java               # Application entry point
    │   ├── model/                  # Domain entities (POJOs + Enums)
    │   │   ├── User.java
    │   │   ├── Course.java
    │   │   ├── Subject.java
    │   │   ├── Lecturer.java
    │   │   ├── Student.java
    │   │   ├── ClassSession.java
    │   │   ├── Attendance.java
    │   │   ├── UserRole.java
    │   │   └── AttendanceStatus.java
    │   ├── dao/                    # Data Access Objects (JDBC)
    │   │   ├── DatabaseConnection.java
    │   │   ├── UserDAO.java
    │   │   ├── CourseDAO.java
    │   │   ├── SubjectDAO.java
    │   │   ├── LecturerDAO.java
    │   │   ├── StudentDAO.java
    │   │   ├── ClassSessionDAO.java
    │   │   └── AttendanceDAO.java
    │   ├── service/                # Business logic services
    │   │   ├── AuthService.java
    │   │   ├── CourseService.java
    │   │   ├── SubjectService.java
    │   │   ├── LecturerService.java
    │   │   ├── StudentService.java
    │   │   ├── ClassSessionService.java
    │   │   └── AttendanceService.java
    │   └── ui/                     # JavaFX views (Presentation)
    │       ├── LoginView.java
    │       ├── DashboardView.java
    │       ├── CourseView.java
    │       ├── StudentView.java
    │       ├── LecturerView.java
    │       ├── ClassSessionView.java
    │       ├── AttendanceView.java
    │       ├── AttendanceReportView.java
    │       └── components/
    │           ├── Sidebar.java
    │           └── AlertHelper.java
    └── resources/
        ├── db.properties           # Database connection config
        └── style.css               # Custom theme stylesheet
```

---

## ⚙️ Setup Instructions

### Prerequisites

1. **JDK 21 or later** installed ([download](https://www.oracle.com/java/technologies/downloads/))
2. **MySQL 8.x** installed and running ([download](https://dev.mysql.com/downloads/))
3. **Maven 3.x** installed (or use included wrapper/scripts)

### Step 1: Clone the Repository

```bash
git clone https://github.com/dilhper/student-attendance-management-system.git
cd student-attendance-management-system
```

### Step 2: Set Up the Database

1. Open MySQL Workbench or the MySQL command line client.
2. Execute the `database.sql` script to create the schema and load sample records:

```bash
mysql -u root -p < database.sql
```

This will:
- Create the `sams_db` database
- Create all required tables (`courses`, `subjects`, `students`, `lecturers`, `lecturer_subjects`, `class_sessions`, `attendance`, `users`)
- Insert sample initial data for courses, lecturers, students, sessions, and default user accounts

### Step 3: Configure Database Connection

If your MySQL password or port differs from the default, update `src/main/resources/db.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/sams_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=root
db.password=YOUR_PASSWORD_HERE
```

### Step 4: Build and Run

**Option A — Quick launch script (Windows):**
Double-click `run.bat` or run in terminal:
```bash
.\run.bat
```

**Option B — Using Maven directly:**
```bash
mvn clean javafx:run
```

**Option C — Using Maven Wrapper:**
```cmd
mvnw.cmd clean javafx:run
```

---

## 🔐 Default User Credentials

| Role | Username | Password | Notes |
|---|---|---|---|
| **Admin** | `admin` | `admin123` | Full access to all modules and management views |
| **Lecturer** | `john.smith` | `lecturer123` | John Smith — teaches OOP, DSA |
| **Lecturer** | `sarah.jones` | `lecturer123` | Sarah Jones — teaches DBS, Web Dev |
| **Lecturer** | `michael.wilson` | `lecturer123` | Michael Wilson — teaches SW Design, Networks |
| **Lecturer** | `emily.brown` | `lecturer123` | Emily Brown — teaches PM, Network Security |

---

## 📋 Features Summary

| Feature | Admin | Lecturer |
|---|:---:|:---:|
| Dashboard with statistics | ✅ | ✅ |
| Course CRUD | ✅ | ❌ |
| Subject management | ✅ | ❌ |
| Student CRUD | ✅ | ❌ |
| Lecturer CRUD | ✅ | ❌ |
| Class scheduling | ✅ (CRUD) | ✅ (View only) |
| Mark attendance | ✅ | ✅ |
| Attendance reports | ✅ | ✅ |
| Logout & Session management | ✅ | ✅ |

---

## 📚 References & External Libraries Acknowledgement

In accordance with academic coursework guidelines, the following external libraries, documentation resources, and technical references were consulted and utilized in the design and development of this project:

1. **OpenJFX (JavaFX 21)**
   - Official Documentation & API Reference: [https://openjfx.io/](https://openjfx.io/)
   - Utilized for: Desktop user interface components, `TableView`, `ComboBox`, `DatePicker`, scene graphs, and CSS skinning.

2. **MySQL Connector/J (8.3.0)**
   - Official JDBC Driver Documentation: [https://dev.mysql.com/doc/connector-j/en/](https://dev.mysql.com/doc/connector-j/en/)
   - Utilized for: Relational database connectivity, prepared statements, and transaction handling.

3. **Oracle Java SE 21 Documentation**
   - API Specification & Language Documentation: [https://docs.oracle.com/en/java/javase/21/](https://docs.oracle.com/en/java/javase/21/)
   - Utilized for: Core Java collections framework, date/time API (`java.time.LocalDate`), and JDBC interfaces (`java.sql`).

4. **Architectural & Design Pattern References**
   - Martin Fowler & Core J2EE Patterns: *Data Access Object (DAO) Pattern* and *Separation of Concerns in Layered Architecture*.
   - Consulted for structuring the 4-layer architecture (`model`, `dao`, `service`, `ui`).

---

## ⚖️ Academic Integrity & AI Attribution Declaration

**Module:** Object-Oriented Programming  
**Assessment Type:** Individual Coursework  
**Author:** Isuru Perera  

### 1. Statement of Individual Authorship & Originality
This project is submitted as my own individual work for the Object-Oriented Programming assessment. I confirm that:
- I have not copied or shared code with other students.
- All business logic, database relationships, interface workflows, and application features were designed and implemented for this coursework.

### 2. Attribution of AI Tools & Adaptation Statement
In compliance with the institutional policy regarding the use of generative artificial intelligence:
- **Tools Consulted:** Generative AI tools (including ChatGPT and Claude) were used as an assistive educational resource during the project.
- **Nature of Assistance:** AI assistance was limited to:
  - Exploring standard JavaFX layout and styling techniques (e.g., configuring CSS pseudo-classes for dark themes).
  - Clarifying JDBC syntax for parameterized SQL queries and batch operations.
  - Reviewing object-oriented design patterns (Layered Architecture and DAO).
- **Adaptation & Independent Verification:**
  - **No unadapted or verbatim code was submitted.**
  - All database schemas (`database.sql`), entity models, DAO implementations, service-level validation routines, and JavaFX view controllers were adapted, written, customized, debugged, and tested individually to fulfill the specific coursework requirements of the Student Attendance Management System (SAMS).
  - The application architecture, validation logic, role-based navigation, and custom UI styling have been individually tested and verified to ensure full functionality and maintainability.

---

## 📄 License

This software was developed for academic evaluation purposes as part of the Object-Oriented Programming coursework.
