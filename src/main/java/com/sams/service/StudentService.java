package com.sams.service;

import com.sams.dao.StudentDAO;
import com.sams.model.Student;

import java.util.List;

/**
 * Service Layer: StudentService
 * 
 * Handles all business logic and validation rules for student profile operations
 * before delegating database transactions to the StudentDAO layer.
 */
public class StudentService {

    private final StudentDAO studentDAO = new StudentDAO();

    /**
     * Retrieves all registered students from the system.
     * 
     * @return List of all students
     */
    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    /**
     * Finds a single student by their unique database identifier.
     * 
     * @param targetStudentId Primary key of the student
     * @return Student object if found, or null
     */
    public Student getStudentById(int targetStudentId) {
        if (targetStudentId <= 0) {
            return null;
        }
        return studentDAO.findById(targetStudentId);
    }

    /**
     * Retrieves all students enrolled in a specific degree course.
     * 
     * @param targetCourseId Primary key of the course
     * @return List of students enrolled in the specified course
     */
    public List<Student> getStudentsByCourse(int targetCourseId) {
        if (targetCourseId <= 0) {
            return List.of();
        }
        return studentDAO.findByCourseId(targetCourseId);
    }

    /**
     * Retrieves all students enrolled in the course associated with a given class session.
     * 
     * @param targetSessionId Primary key of the class session
     * @return List of enrolled students for the session
     */
    public List<Student> getStudentsBySession(int targetSessionId) {
        if (targetSessionId <= 0) {
            return List.of();
        }
        return studentDAO.findBySessionId(targetSessionId);
    }

    /**
     * Validates and persists a new student registration record.
     * Enforces mandatory fields: registration number, first name, last name, and course selection.
     * 
     * @param newStudent Student entity to register
     * @return true if successfully saved, false if validation fails or persistence error occurs
     */
    public boolean saveStudent(Student newStudent) {
        if (!validateStudentInformation(newStudent)) {
            return false;
        }
        if (newStudent.getCourseId() <= 0) {
            return false;
        }
        return studentDAO.save(newStudent);
    }

    /**
     * Validates and updates an existing student record in the database.
     * 
     * @param updatedStudent Student entity containing modified information
     * @return true if successfully updated, false otherwise
     */
    public boolean updateStudent(Student updatedStudent) {
        if (updatedStudent == null || updatedStudent.getStudentId() <= 0) {
            return false;
        }
        if (!validateStudentInformation(updatedStudent)) {
            return false;
        }
        return studentDAO.update(updatedStudent);
    }

    /**
     * Removes a student record by their primary identifier.
     * 
     * @param targetStudentId ID of the student to remove
     * @return true if deleted, false otherwise
     */
    public boolean deleteStudent(int targetStudentId) {
        if (targetStudentId <= 0) {
            return false;
        }
        return studentDAO.delete(targetStudentId);
    }

    /**
     * Returns the total count of registered students for dashboard statistics.
     * 
     * @return integer total count
     */
    public int getStudentCount() {
        return studentDAO.count();
    }

    /**
     * Internal helper method to validate required personal details for a student.
     * 
     * @param candidateStudent Student instance to check
     * @return true if all mandatory fields are valid
     */
    private boolean validateStudentInformation(Student candidateStudent) {
        if (candidateStudent == null) {
            return false;
        }
        String regNumber = candidateStudent.getRegistrationNumber();
        String firstName = candidateStudent.getFirstName();
        String lastName = candidateStudent.getLastName();

        return regNumber != null && !regNumber.trim().isEmpty()
                && firstName != null && !firstName.trim().isEmpty()
                && lastName != null && !lastName.trim().isEmpty();
    }
}
