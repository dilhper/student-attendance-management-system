package com.sams.dao;

import com.sams.model.Attendance;
import com.sams.model.AttendanceStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for attendance records — marking and reporting.
 */
public class AttendanceDAO {

    /**
     * Returns existing attendance records for a session (used to pre-fill the marking form).
     */
    public List<Attendance> findBySessionId(int sessionId) {
        List<Attendance> list = new ArrayList<>();
        String sql = """
            SELECT a.*, CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                   s.registration_number
            FROM attendance a
            JOIN students s ON a.student_id = s.student_id
            WHERE a.session_id = ?
            ORDER BY s.registration_number
            """;
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, sessionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Attendance att = new Attendance();
                        att.setAttendanceId(rs.getInt("attendance_id"));
                        att.setSessionId(rs.getInt("session_id"));
                        att.setStudentId(rs.getInt("student_id"));
                        att.setStatus(AttendanceStatus.valueOf(rs.getString("status")));
                        att.setStudentName(rs.getString("student_name"));
                        att.setRegistrationNumber(rs.getString("registration_number"));
                        list.add(att);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Inserts or updates (upserts) attendance for a session+student combination.
     * Uses INSERT ... ON DUPLICATE KEY UPDATE for MySQL.
     */
    public boolean saveAll(List<Attendance> records) {
        String sql = """
            INSERT INTO attendance (session_id, student_id, status)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE status = VALUES(status)
            """;
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Attendance att : records) {
                    stmt.setInt(1, att.getSessionId());
                    stmt.setInt(2, att.getStudentId());
                    stmt.setString(3, att.getStatus().name());
                    stmt.addBatch();
                }
                stmt.executeBatch();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Filtered attendance report query. All filter parameters are optional (pass null to skip).
     */
    public List<Attendance> getReport(Integer studentId, Integer subjectId,
                                      LocalDate dateFrom, LocalDate dateTo) {
        List<Attendance> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT a.attendance_id, a.session_id, a.student_id, a.status,
                   CONCAT(st.first_name, ' ', st.last_name) AS student_name,
                   st.registration_number,
                   sub.subject_name, c.course_name,
                   cs.session_date,
                   CONCAT(cs.start_time, ' - ', cs.end_time) AS time_slot,
                   CONCAT(l.first_name, ' ', l.last_name) AS lecturer_name
            FROM attendance a
            JOIN students st ON a.student_id = st.student_id
            JOIN class_sessions cs ON a.session_id = cs.session_id
            JOIN subjects sub ON cs.subject_id = sub.subject_id
            JOIN courses c ON sub.course_id = c.course_id
            JOIN lecturers l ON cs.lecturer_id = l.lecturer_id
            WHERE 1=1
            """);

        List<Object> params = new ArrayList<>();

        if (studentId != null) {
            sql.append(" AND a.student_id = ?");
            params.add(studentId);
        }
        if (subjectId != null) {
            sql.append(" AND cs.subject_id = ?");
            params.add(subjectId);
        }
        if (dateFrom != null) {
            sql.append(" AND cs.session_date >= ?");
            params.add(Date.valueOf(dateFrom));
        }
        if (dateTo != null) {
            sql.append(" AND cs.session_date <= ?");
            params.add(Date.valueOf(dateTo));
        }

        sql.append(" ORDER BY cs.session_date DESC, st.registration_number");

        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    Object p = params.get(i);
                    if (p instanceof Integer) stmt.setInt(i + 1, (Integer) p);
                    else if (p instanceof Date) stmt.setDate(i + 1, (Date) p);
                    else stmt.setString(i + 1, p.toString());
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Attendance att = new Attendance();
                        att.setAttendanceId(rs.getInt("attendance_id"));
                        att.setSessionId(rs.getInt("session_id"));
                        att.setStudentId(rs.getInt("student_id"));
                        att.setStatus(AttendanceStatus.valueOf(rs.getString("status")));
                        att.setStudentName(rs.getString("student_name"));
                        att.setRegistrationNumber(rs.getString("registration_number"));
                        att.setSubjectName(rs.getString("subject_name"));
                        att.setCourseName(rs.getString("course_name"));
                        att.setSessionDate(rs.getString("session_date"));
                        att.setTimeSlot(rs.getString("time_slot"));
                        att.setLecturerName(rs.getString("lecturer_name"));
                        list.add(att);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
