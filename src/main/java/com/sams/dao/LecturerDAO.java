package com.sams.dao;

import com.sams.model.Lecturer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Lecturer CRUD and subject-assignment operations.
 */
public class LecturerDAO {

    public List<Lecturer> findAll() {
        List<Lecturer> list = new ArrayList<>();
        String sql = "SELECT * FROM lecturers ORDER BY last_name, first_name";
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

    public Lecturer findById(int lecturerId) {
        String sql = "SELECT * FROM lecturers WHERE lecturer_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, lecturerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean save(Lecturer lecturer) {
        String sql = "INSERT INTO lecturers (first_name, last_name, email, phone) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, lecturer.getFirstName());
                stmt.setString(2, lecturer.getLastName());
                stmt.setString(3, lecturer.getEmail());
                stmt.setString(4, lecturer.getPhone());
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (keys.next()) lecturer.setLecturerId(keys.getInt(1));
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Lecturer lecturer) {
        String sql = "UPDATE lecturers SET first_name = ?, last_name = ?, email = ?, phone = ? WHERE lecturer_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, lecturer.getFirstName());
                stmt.setString(2, lecturer.getLastName());
                stmt.setString(3, lecturer.getEmail());
                stmt.setString(4, lecturer.getPhone());
                stmt.setInt(5, lecturer.getLecturerId());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int lecturerId) {
        String sql = "DELETE FROM lecturers WHERE lecturer_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, lecturerId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM lecturers";
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

    /**
     * Replaces all subject assignments for a lecturer.
     */
    public boolean updateSubjectAssignments(int lecturerId, List<Integer> subjectIds) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            // Remove existing assignments
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM lecturer_subjects WHERE lecturer_id = ?")) {
                del.setInt(1, lecturerId);
                del.executeUpdate();
            }
            // Insert new assignments
            if (subjectIds != null && !subjectIds.isEmpty()) {
                String sql = "INSERT INTO lecturer_subjects (lecturer_id, subject_id) VALUES (?, ?)";
                try (PreparedStatement ins = conn.prepareStatement(sql)) {
                    for (int subjectId : subjectIds) {
                        ins.setInt(1, lecturerId);
                        ins.setInt(2, subjectId);
                        ins.addBatch();
                    }
                    ins.executeBatch();
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the list of subject IDs assigned to a lecturer.
     */
    public List<Integer> getAssignedSubjectIds(int lecturerId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT subject_id FROM lecturer_subjects WHERE lecturer_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, lecturerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) ids.add(rs.getInt("subject_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    private Lecturer mapRow(ResultSet rs) throws SQLException {
        Lecturer l = new Lecturer();
        l.setLecturerId(rs.getInt("lecturer_id"));
        l.setFirstName(rs.getString("first_name"));
        l.setLastName(rs.getString("last_name"));
        l.setEmail(rs.getString("email"));
        l.setPhone(rs.getString("phone"));
        return l;
    }
}
