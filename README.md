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
| **Java** | JDK 21+ (tested on JDK 23) | Core programming language |
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
├── README.md                       # This file
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
        └── style.css               # Dark theme stylesheet
```

---

## ⚙️ Setup Instructions

### Prerequisites

1. **JDK 21 or later** installed ([download](https://www.oracle.com/java/technologies/downloads/))
2. **Maven 3.x** installed ([download](https://maven.apache.org/download.cgi))
3. **MySQL 8.x** installed and running ([download](https://dev.mysql.com/downloads/))

### Step 1: Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/sams.git
cd sams
```

### Step 2: Set Up the Database

1. Open MySQL Workbench or the MySQL command line client.
2. Execute the `database.sql` file to create the database schema and load sample data:

```bash
mysql -u root -p < database.sql
```

This will:
- Create the `sams_db` database
- Create all required tables (courses, subjects, students, lecturers, class_sessions, attendance, users)
- Insert sample data including users, courses, students, and attendance records

### Step 3: Configure Database Connection

Edit `src/main/resources/db.properties` to match your MySQL credentials:

```properties
db.url=jdbc:mysql://localhost:3306/sams_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=root
db.password=YOUR_PASSWORD_HERE
```

### Step 4: Build and Run

**If Maven is in your PATH:**
```bash
mvn clean javafx:run
```

**If using the locally downloaded Maven (see Setup):**
```bash
C:\Users\Dell\apache-maven\apache-maven-3.9.6\bin\mvn.cmd clean javafx:run
```

The application window will open with the login screen.

---

## 🔐 Login Credentials

The following default user accounts are pre-loaded in the database:

| Role | Username | Password | Notes |
|---|---|---|---|
| **Admin** | `admin` | `admin123` | Full access to all modules |
| **Lecturer** | `john.smith` | `lecturer123` | John Smith — teaches OOP, DSA |
| **Lecturer** | `sarah.jones` | `lecturer123` | Sarah Jones — teaches DBS, Web Dev |
| **Lecturer** | `michael.wilson` | `lecturer123` | Michael Wilson — teaches SW Design, Networks |
| **Lecturer** | `emily.brown` | `lecturer123` | Emily Brown — teaches PM, Network Security |

---

## 📸 Screenshots

### Login Screen
The application starts with a styled login form. Enter your credentials to access the system.

### Admin Dashboard
Administrators see an overview with statistics (total courses, students, lecturers, sessions) and can navigate to all management modules.

### Course Management
Full CRUD for courses with inline subject management. Admins can add, edit, and delete courses and their subjects.

### Student Management
Student roster with search and course filtering. Admins can register, update, and remove student records.

### Lecturer Management
Lecturer profiles with subject assignment. Admins can manage lecturer details and assign/unassign teaching subjects.

### Class Scheduling
Schedule class sessions by selecting a course, subject, lecturer, date, time slot, and room.

### Attendance Marking
Lecturers select a class session, then mark each student as Present, Absent, or Late with a single save action.

### Attendance Reports
Filtered reports with student, subject, and date range controls. Includes color-coded status badges and percentage summary statistics.

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
| Logout | ✅ | ✅ |

---

## 📚 References & Acknowledgements

The following open-source libraries and documentation resources were consulted and utilized in the development of this coursework project:

- **JavaFX 21**: OpenJFX documentation and controls library ([openjfx.io](https://openjfx.io/))
- **MySQL Connector/J 8.3.0**: Official MySQL JDBC Driver ([dev.mysql.com](https://dev.mysql.com/doc/connector-j/en/))
- **Apache Maven**: Dependency and build management ([maven.apache.org](https://maven.apache.org/))
- **Oracle Java SE 21 Documentation**: Java Language and API Specification ([docs.oracle.com](https://docs.oracle.com/en/java/javase/21/))

---

## ⚖️ Academic Integrity Statement

This project is submitted as individual coursework for the **Object-Oriented Programming** module. All design decisions, architectural layering, database schemas, and source code implementation have been developed in accordance with institutional academic integrity policies.

---

## 📄 License

This project was developed as coursework for the Object-Oriented Programming module.

