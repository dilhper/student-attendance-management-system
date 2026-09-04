package com.sams.model;

/**
 * Represents a student enrolled in a course.
 */
public class Student {

    private int studentId;
    private String registrationNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private int courseId;
    private String courseName;  // denormalized for display

    public Student() {}

    public Student(int studentId, String registrationNumber, String firstName, String lastName,
                   String email, String phone, int courseId) {
        this.studentId = studentId;
        this.registrationNumber = registrationNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.courseId = courseId;
    }

    // Getters and Setters

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return registrationNumber + " — " + firstName + " " + lastName;
    }
}
