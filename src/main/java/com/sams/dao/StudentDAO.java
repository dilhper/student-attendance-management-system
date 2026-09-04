package com.sams.dao;

import com.sams.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Student CRUD operations.
 */
public class StudentDAO {

    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = """
            SELECT s.*, c.course_name
            FROM students s
            JOIN courses c ON s.course_id = c.course_id
            ORDER BY s.registration_number
            """;
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Student findById(int studentId) {
        String sql = """
            SELECT s.*, c.course_name
            FROM students s
            JOIN courses c ON s.course_id = c.course_id
            WHERE s.student_id = ?
            """;
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, studentId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> findByCourseId(int courseId) {
        List<Student> list = new ArrayList<>();
        String sql = """
            SELECT s.*, c.course_name
            FROM students s
            JOIN courses c ON s.course_id = c.course_id
            WHERE s.course_id = ?
            ORDER BY s.registration_number
            """;
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Returns students enrolled in the same course as the given session's subject.
     */
    public List<Student> findBySessionId(int sessionId) {
        List<Student> list = new ArrayList<>();
        String sql = """
            SELECT st.*, c.course_name
            FROM students st
            JOIN subjects sub ON st.course_id = sub.course_id
            JOIN class_sessions cs ON cs.subject_id = sub.subject_id
            JOIN courses c ON st.course_id = c.course_id
            WHERE cs.session_id = ?
            ORDER BY st.registration_number
            """;
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, sessionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean save(Student student) {
        String sql = "INSERT INTO students (registration_number, first_name, last_name, email, phone, course_id) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, student.getRegistrationNumber());
                stmt.setString(2, student.getFirstName());
                stmt.setString(3, student.getLastName());
                stmt.setString(4, student.getEmail());
                stmt.setString(5, student.getPhone());
                stmt.setInt(6, student.getCourseId());
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (keys.next()) student.setStudentId(keys.getInt(1));
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Student student) {
        String sql = "UPDATE students SET registration_number = ?, first_name = ?, last_name = ?, email = ?, phone = ?, course_id = ? WHERE student_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, student.getRegistrationNumber());
                stmt.setString(2, student.getFirstName());
                stmt.setString(3, student.getLastName());
                stmt.setString(4, student.getEmail());
                stmt.setString(5, student.getPhone());
                stmt.setInt(6, student.getCourseId());
                stmt.setInt(7, student.getStudentId());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, studentId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM students";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setRegistrationNumber(rs.getString("registration_number"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setCourseId(rs.getInt("course_id"));
        try {
            s.setCourseName(rs.getString("course_name"));
        } catch (SQLException ignored) {}
        return s;
    }
}
