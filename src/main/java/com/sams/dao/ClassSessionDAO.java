package com.sams.dao;

import com.sams.model.ClassSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ClassSession CRUD operations.
 */
public class ClassSessionDAO {

    private static final String SELECT_ALL = """
        SELECT cs.*, sub.subject_name, sub.subject_code, sub.course_id,
               CONCAT(l.first_name, ' ', l.last_name) AS lecturer_name,
               c.course_name
        FROM class_sessions cs
        JOIN subjects sub ON cs.subject_id = sub.subject_id
        JOIN lecturers l ON cs.lecturer_id = l.lecturer_id
        JOIN courses c ON sub.course_id = c.course_id
        """;

    public List<ClassSession> findAll() {
        List<ClassSession> list = new ArrayList<>();
        String sql = SELECT_ALL + " ORDER BY cs.session_date DESC, cs.start_time";
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

    public ClassSession findById(int sessionId) {
        String sql = SELECT_ALL + " WHERE cs.session_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, sessionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ClassSession> findByLecturerId(int lecturerId) {
        List<ClassSession> list = new ArrayList<>();
        String sql = SELECT_ALL + " WHERE cs.lecturer_id = ? ORDER BY cs.session_date DESC, cs.start_time";
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

    public boolean save(ClassSession session) {
        String sql = "INSERT INTO class_sessions (subject_id, lecturer_id, session_date, start_time, end_time, room) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, session.getSubjectId());
                stmt.setInt(2, session.getLecturerId());
                stmt.setDate(3, Date.valueOf(session.getSessionDate()));
                stmt.setTime(4, Time.valueOf(session.getStartTime()));
                stmt.setTime(5, Time.valueOf(session.getEndTime()));
                stmt.setString(6, session.getRoom());
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (keys.next()) session.setSessionId(keys.getInt(1));
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(ClassSession session) {
        String sql = "UPDATE class_sessions SET subject_id = ?, lecturer_id = ?, session_date = ?, start_time = ?, end_time = ?, room = ? WHERE session_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, session.getSubjectId());
                stmt.setInt(2, session.getLecturerId());
                stmt.setDate(3, Date.valueOf(session.getSessionDate()));
                stmt.setTime(4, Time.valueOf(session.getStartTime()));
                stmt.setTime(5, Time.valueOf(session.getEndTime()));
                stmt.setString(6, session.getRoom());
                stmt.setInt(7, session.getSessionId());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int sessionId) {
        String sql = "DELETE FROM class_sessions WHERE session_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, sessionId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM class_sessions";
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

    private ClassSession mapRow(ResultSet rs) throws SQLException {
        ClassSession cs = new ClassSession();
        cs.setSessionId(rs.getInt("session_id"));
        cs.setSubjectId(rs.getInt("subject_id"));
        cs.setLecturerId(rs.getInt("lecturer_id"));
        cs.setSessionDate(rs.getDate("session_date").toLocalDate());
        cs.setStartTime(rs.getTime("start_time").toLocalTime());
        cs.setEndTime(rs.getTime("end_time").toLocalTime());
        cs.setRoom(rs.getString("room"));
        try {
            cs.setSubjectName(rs.getString("subject_name"));
            cs.setSubjectCode(rs.getString("subject_code"));
            cs.setLecturerName(rs.getString("lecturer_name"));
            cs.setCourseName(rs.getString("course_name"));
            cs.setCourseId(rs.getInt("course_id"));
        } catch (SQLException ignored) {}
        return cs;
    }
}
