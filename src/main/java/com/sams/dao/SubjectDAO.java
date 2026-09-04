package com.sams.dao;

import com.sams.model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Subject CRUD operations.
 */
public class SubjectDAO {

    public List<Subject> findAll() {
        List<Subject> list = new ArrayList<>();
        String sql = """
            SELECT s.*, c.course_name
            FROM subjects s
            JOIN courses c ON s.course_id = c.course_id
            ORDER BY s.subject_code
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

    public List<Subject> findByCourseId(int courseId) {
        List<Subject> list = new ArrayList<>();
        String sql = """
            SELECT s.*, c.course_name
            FROM subjects s
            JOIN courses c ON s.course_id = c.course_id
            WHERE s.course_id = ?
            ORDER BY s.subject_code
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

    public List<Subject> findByLecturerId(int lecturerId) {
        List<Subject> list = new ArrayList<>();
        String sql = """
            SELECT s.*, c.course_name
            FROM subjects s
            JOIN courses c ON s.course_id = c.course_id
            JOIN lecturer_subjects ls ON s.subject_id = ls.subject_id
            WHERE ls.lecturer_id = ?
            ORDER BY s.subject_code
            """;
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, lecturerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean save(Subject subject) {
        String sql = "INSERT INTO subjects (subject_code, subject_name, course_id) VALUES (?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, subject.getSubjectCode());
                stmt.setString(2, subject.getSubjectName());
                stmt.setInt(3, subject.getCourseId());
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (keys.next()) subject.setSubjectId(keys.getInt(1));
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Subject subject) {
        String sql = "UPDATE subjects SET subject_code = ?, subject_name = ?, course_id = ? WHERE subject_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, subject.getSubjectCode());
                stmt.setString(2, subject.getSubjectName());
                stmt.setInt(3, subject.getCourseId());
                stmt.setInt(4, subject.getSubjectId());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int subjectId) {
        String sql = "DELETE FROM subjects WHERE subject_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, subjectId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Subject mapRow(ResultSet rs) throws SQLException {
        Subject s = new Subject();
        s.setSubjectId(rs.getInt("subject_id"));
        s.setSubjectCode(rs.getString("subject_code"));
        s.setSubjectName(rs.getString("subject_name"));
        s.setCourseId(rs.getInt("course_id"));
        try {
            s.setCourseName(rs.getString("course_name"));
        } catch (SQLException ignored) {
            // course_name not in query
        }
        return s;
    }
}
