-- ============================================================
-- Student Attendance Management System (SAMS) — Database Script
-- Platform: MySQL 8.x
-- ============================================================

-- Create and select the database
DROP DATABASE IF EXISTS sams_db;
CREATE DATABASE sams_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sams_db;

-- ============================================================
-- DDL — Table Definitions
-- ============================================================

-- Courses offered by the institution
CREATE TABLE courses (
    course_id   INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20)  NOT NULL UNIQUE,
    course_name VARCHAR(100) NOT NULL,
    description TEXT
) ENGINE=InnoDB;

-- Subjects (modules) belonging to a course
CREATE TABLE subjects (
    subject_id   INT AUTO_INCREMENT PRIMARY KEY,
    subject_code VARCHAR(20)  NOT NULL UNIQUE,
    subject_name VARCHAR(100) NOT NULL,
    course_id    INT          NOT NULL,
    CONSTRAINT fk_subject_course FOREIGN KEY (course_id)
        REFERENCES courses(course_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Lecturer profiles
CREATE TABLE lecturers (
    lecturer_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name  VARCHAR(50)  NOT NULL,
    last_name   VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) UNIQUE,
    phone       VARCHAR(20)
) ENGINE=InnoDB;

-- Many-to-many: Lecturer ↔ Subject assignments
CREATE TABLE lecturer_subjects (
    lecturer_id INT NOT NULL,
    subject_id  INT NOT NULL,
    PRIMARY KEY (lecturer_id, subject_id),
    CONSTRAINT fk_ls_lecturer FOREIGN KEY (lecturer_id)
        REFERENCES lecturers(lecturer_id) ON DELETE CASCADE,
    CONSTRAINT fk_ls_subject FOREIGN KEY (subject_id)
        REFERENCES subjects(subject_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Student profiles
CREATE TABLE students (
    student_id          INT AUTO_INCREMENT PRIMARY KEY,
    registration_number VARCHAR(20)  NOT NULL UNIQUE,
    first_name          VARCHAR(50)  NOT NULL,
    last_name           VARCHAR(50)  NOT NULL,
    email               VARCHAR(100),
    phone               VARCHAR(20),
    course_id           INT          NOT NULL,
    CONSTRAINT fk_student_course FOREIGN KEY (course_id)
        REFERENCES courses(course_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Scheduled class sessions
CREATE TABLE class_sessions (
    session_id   INT AUTO_INCREMENT PRIMARY KEY,
    subject_id   INT   NOT NULL,
    lecturer_id  INT   NOT NULL,
    session_date DATE  NOT NULL,
    start_time   TIME  NOT NULL,
    end_time     TIME  NOT NULL,
    room         VARCHAR(50),
    CONSTRAINT fk_session_subject  FOREIGN KEY (subject_id)
        REFERENCES subjects(subject_id)  ON DELETE CASCADE,
    CONSTRAINT fk_session_lecturer FOREIGN KEY (lecturer_id)
        REFERENCES lecturers(lecturer_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Attendance records (per-student, per-session)
CREATE TABLE attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    session_id    INT NOT NULL,
    student_id    INT NOT NULL,
    status        ENUM('PRESENT','ABSENT','LATE') NOT NULL DEFAULT 'ABSENT',
    CONSTRAINT fk_att_session FOREIGN KEY (session_id)
        REFERENCES class_sessions(session_id) ON DELETE CASCADE,
    CONSTRAINT fk_att_student FOREIGN KEY (student_id)
        REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT uq_session_student UNIQUE (session_id, student_id)
) ENGINE=InnoDB;

-- User accounts for authentication
CREATE TABLE users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('ADMIN','LECTURER') NOT NULL,
    lecturer_id INT NULL,
    CONSTRAINT fk_user_lecturer FOREIGN KEY (lecturer_id)
        REFERENCES lecturers(lecturer_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Performance indexes
CREATE INDEX idx_att_session  ON attendance(session_id);
CREATE INDEX idx_att_student  ON attendance(student_id);
CREATE INDEX idx_session_date ON class_sessions(session_date);
CREATE INDEX idx_student_course ON students(course_id);

-- ============================================================
-- DML — Sample Data
-- ============================================================

-- Courses
INSERT INTO courses (course_code, course_name, description) VALUES
('CS101', 'BSc Computer Science',       'Bachelor of Science in Computer Science covering core computing principles, algorithms, and software development.'),
('SE201', 'BSc Software Engineering',   'Bachelor of Science in Software Engineering focusing on software development methodologies, design patterns, and project management.'),
('IT301', 'BSc Information Technology', 'Bachelor of Science in Information Technology covering IT infrastructure, networking, and systems administration.');

-- Subjects
INSERT INTO subjects (subject_code, subject_name, course_id) VALUES
('CS-OOP',  'Object-Oriented Programming',   1),
('CS-DBS',  'Database Systems',               1),
('CS-DSA',  'Data Structures & Algorithms',   1),
('CS-NET',  'Computer Networks',              1),
('SE-SWD',  'Software Design Patterns',       2),
('SE-PM',   'Project Management',             2),
('SE-WEB',  'Web Application Development',    2),
('IT-SEC',  'Network Security',               3),
('IT-SYS',  'Systems Administration',         3),
('IT-CLD',  'Cloud Computing',                3);

-- Lecturers
INSERT INTO lecturers (first_name, last_name, email, phone) VALUES
('John',    'Smith',   'john.smith@university.edu',    '+44 7911 123456'),
('Sarah',   'Jones',   'sarah.jones@university.edu',   '+44 7922 234567'),
('Michael', 'Wilson',  'michael.wilson@university.edu', '+44 7933 345678'),
('Emily',   'Brown',   'emily.brown@university.edu',   '+44 7944 456789'),
('David',   'Taylor',  'david.taylor@university.edu',  '+44 7955 567890');

-- Lecturer ↔ Subject assignments
INSERT INTO lecturer_subjects (lecturer_id, subject_id) VALUES
(1, 1), (1, 3),        -- John Smith    → OOP, DSA
(2, 2), (2, 7),        -- Sarah Jones   → Database Systems, Web Dev
(3, 5), (3, 4),        -- Michael Wilson → Software Design, Computer Networks
(4, 6), (4, 8),        -- Emily Brown   → Project Management, Network Security
(5, 9), (5, 10);       -- David Taylor  → Systems Admin, Cloud Computing

-- Users (default credentials for login)
INSERT INTO users (username, password, role, lecturer_id) VALUES
('admin',           'admin123',     'ADMIN',    NULL),
('john.smith',      'lecturer123',  'LECTURER', 1),
('sarah.jones',     'lecturer123',  'LECTURER', 2),
('michael.wilson',  'lecturer123',  'LECTURER', 3),
('emily.brown',     'lecturer123',  'LECTURER', 4);

-- Students (16 students across 3 courses)
INSERT INTO students (registration_number, first_name, last_name, email, phone, course_id) VALUES
('STU-2024-001', 'Alice',    'Anderson',  'alice.anderson@student.edu',   '+44 7700 100001', 1),
('STU-2024-002', 'Bob',      'Baker',     'bob.baker@student.edu',        '+44 7700 100002', 1),
('STU-2024-003', 'Charlie',  'Clark',     'charlie.clark@student.edu',    '+44 7700 100003', 1),
('STU-2024-004', 'Diana',    'Davis',     'diana.davis@student.edu',      '+44 7700 100004', 1),
('STU-2024-005', 'Edward',   'Evans',     'edward.evans@student.edu',     '+44 7700 100005', 1),
('STU-2024-006', 'Fiona',    'Foster',    'fiona.foster@student.edu',     '+44 7700 100006', 2),
('STU-2024-007', 'George',   'Green',     'george.green@student.edu',     '+44 7700 100007', 2),
('STU-2024-008', 'Hannah',   'Harris',    'hannah.harris@student.edu',    '+44 7700 100008', 2),
('STU-2024-009', 'Ian',      'Irving',    'ian.irving@student.edu',       '+44 7700 100009', 2),
('STU-2024-010', 'Julia',    'Jackson',   'julia.jackson@student.edu',    '+44 7700 100010', 2),
('STU-2024-011', 'Kevin',    'King',      'kevin.king@student.edu',       '+44 7700 100011', 3),
('STU-2024-012', 'Laura',    'Lewis',     'laura.lewis@student.edu',      '+44 7700 100012', 3),
('STU-2024-013', 'Mark',     'Mitchell',  'mark.mitchell@student.edu',    '+44 7700 100013', 3),
('STU-2024-014', 'Nina',     'Nelson',    'nina.nelson@student.edu',      '+44 7700 100014', 3),
('STU-2024-015', 'Oscar',    'Owen',      'oscar.owen@student.edu',       '+44 7700 100015', 3),
('STU-2024-016', 'Patricia', 'Parker',    'patricia.parker@student.edu',  '+44 7700 100016', 1);

-- Class Sessions (10 sample sessions across Sep 2026)
INSERT INTO class_sessions (subject_id, lecturer_id, session_date, start_time, end_time, room) VALUES
(1, 1, '2026-09-01', '09:00:00', '11:00:00', 'Room A101'),
(1, 1, '2026-09-03', '09:00:00', '11:00:00', 'Room A101'),
(2, 2, '2026-09-01', '11:00:00', '13:00:00', 'Room B202'),
(2, 2, '2026-09-04', '11:00:00', '13:00:00', 'Room B202'),
(3, 1, '2026-09-02', '14:00:00', '16:00:00', 'Room A101'),
(5, 3, '2026-09-02', '09:00:00', '11:00:00', 'Room C303'),
(6, 4, '2026-09-03', '14:00:00', '16:00:00', 'Room D404'),
(7, 2, '2026-09-05', '09:00:00', '11:00:00', 'Lab L201'),
(8, 4, '2026-09-04', '14:00:00', '16:00:00', 'Room E505'),
(9, 5, '2026-09-05', '11:00:00', '13:00:00', 'Lab L301');

-- Attendance records (sample for CS and SE sessions)
INSERT INTO attendance (session_id, student_id, status) VALUES
-- Session 1: OOP (CS students)
(1, 1, 'PRESENT'), (1, 2, 'PRESENT'), (1, 3, 'LATE'),
(1, 4, 'PRESENT'), (1, 5, 'ABSENT'),  (1, 16, 'PRESENT'),
-- Session 2: OOP (CS students)
(2, 1, 'PRESENT'), (2, 2, 'LATE'),    (2, 3, 'PRESENT'),
(2, 4, 'PRESENT'), (2, 5, 'PRESENT'), (2, 16, 'ABSENT'),
-- Session 3: DBS (CS students)
(3, 1, 'PRESENT'), (3, 2, 'PRESENT'), (3, 3, 'PRESENT'),
(3, 4, 'ABSENT'),  (3, 5, 'PRESENT'), (3, 16, 'LATE'),
-- Session 5: DSA (CS students)
(5, 1, 'PRESENT'), (5, 2, 'ABSENT'),  (5, 3, 'PRESENT'),
(5, 4, 'PRESENT'), (5, 5, 'LATE'),    (5, 16, 'PRESENT'),
-- Session 6: SW Design (SE students)
(6, 6, 'PRESENT'), (6, 7, 'PRESENT'), (6, 8, 'LATE'),
(6, 9, 'PRESENT'), (6, 10, 'ABSENT'),
-- Session 8: Web Dev (SE students)
(8, 6, 'PRESENT'), (8, 7, 'ABSENT'),  (8, 8, 'PRESENT'),
(8, 9, 'PRESENT'), (8, 10, 'PRESENT');
