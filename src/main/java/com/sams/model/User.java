package com.sams.model;

/**
 * Represents a system user with login credentials and role.
 */
public class User {

    private int userId;
    private String username;
    private String password;
    private UserRole role;
    private Integer lecturerId;   // nullable — only set for LECTURER role

    public User() {}

    public User(int userId, String username, String password, UserRole role, Integer lecturerId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.lecturerId = lecturerId;
    }

    // Getters and Setters

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public Integer getLecturerId() { return lecturerId; }
    public void setLecturerId(Integer lecturerId) { this.lecturerId = lecturerId; }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}
