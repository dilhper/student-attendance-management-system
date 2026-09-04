package com.sams.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a scheduled class session linking a subject, lecturer, date/time and room.
 */
public class ClassSession {

    private int sessionId;
    private int subjectId;
    private int lecturerId;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;

    // Denormalized display fields
    private String subjectName;
    private String subjectCode;
    private String lecturerName;
    private String courseName;
    private int courseId;

    public ClassSession() {}

    public ClassSession(int sessionId, int subjectId, int lecturerId,
                        LocalDate sessionDate, LocalTime startTime, LocalTime endTime, String room) {
        this.sessionId = sessionId;
        this.subjectId = subjectId;
        this.lecturerId = lecturerId;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
    }

    // Getters and Setters

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public int getLecturerId() { return lecturerId; }
    public void setLecturerId(int lecturerId) { this.lecturerId = lecturerId; }

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getLecturerName() { return lecturerName; }
    public void setLecturerName(String lecturerName) { this.lecturerName = lecturerName; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getTimeSlot() {
        return startTime + " – " + endTime;
    }

    @Override
    public String toString() {
        return sessionDate + " | " + subjectName + " (" + startTime + "–" + endTime + ")";
    }
}
