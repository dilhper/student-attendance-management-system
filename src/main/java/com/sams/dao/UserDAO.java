package com.sams.dao;

import com.sams.model.User;
import com.sams.model.UserRole;

import java.sql.*;

/**
 * Data Access Object for user authentication.
 */
public class UserDAO {

    /**
     * Authenticates a user by username and password.
     * @return the User if credentials match, or null.
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapRow(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRole(UserRole.valueOf(rs.getString("role")));
        int lecId = rs.getInt("lecturer_id");
        u.setLecturerId(rs.wasNull() ? null : lecId);
        return u;
    }
}
