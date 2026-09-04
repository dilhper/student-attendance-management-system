package com.sams.model;

/**
 * Represents an attendance record for a student in a specific class session.
 */
public class Attendance {

    private int attendanceId;
    private int sessionId;
    private int studentId;
    private AttendanceStatus status;

    // Denormalized display fields
    private String studentName;
    private String registrationNumber;
    private String subjectName;
    private String courseName;
    private String sessionDate;
    private String timeSlot;
    private String lecturerName;

    public Attendance() {}

    public Attendance(int attendanceId, int sessionId, int studentId, AttendanceStatus status) {
        this.attendanceId = attendanceId;
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.status = status;
    }

    // Getters and Setters

    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getSessionDate() { return sessionDate; }
    public void setSessionDate(String sessionDate) { this.sessionDate = sessionDate; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getLecturerName() { return lecturerName; }
    public void setLecturerName(String lecturerName) { this.lecturerName = lecturerName; }

    @Override
    public String toString() {
        return studentName + " | " + status;
    }
}
