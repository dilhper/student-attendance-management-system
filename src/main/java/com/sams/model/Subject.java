package com.sams.model;

/**
 * Represents a subject (module) that belongs to a course.
 */
public class Subject {

    private int subjectId;
    private String subjectCode;
    private String subjectName;
    private int courseId;
    private String courseName;  // denormalized for display

    public Subject() {}

    public Subject(int subjectId, String subjectCode, String subjectName, int courseId) {
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.courseId = courseId;
    }

    // Getters and Setters

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    @Override
    public String toString() {
        return subjectCode + " — " + subjectName;
    }
}
