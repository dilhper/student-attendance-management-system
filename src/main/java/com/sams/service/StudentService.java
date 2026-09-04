package com.sams.service;

import com.sams.dao.StudentDAO;
import com.sams.model.Student;

import java.util.List;

// StudentService handles business logic and validation for students
public class StudentService {

    private StudentDAO studentDAO = new StudentDAO();

    // get all students
    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    // find student by id
    public Student getStudentById(int studentId) {
        if (studentId <= 0) {
            return null;
        }
        return studentDAO.findById(studentId);
    }

    // get students by course id
    public List<Student> getStudentsByCourse(int courseId) {
        if (courseId <= 0) {
            return List.of();
        }
        return studentDAO.findByCourseId(courseId);
    }

    // get students for a session
    public List<Student> getStudentsBySession(int sessionId) {
        if (sessionId <= 0) {
            return List.of();
        }
        return studentDAO.findBySessionId(sessionId);
    }

    // save a new student after basic validation
    public boolean saveStudent(Student student) {
        if (student == null) {
            return false;
        }
        // check required fields
        if (!validateStudent(student)) {
            return false;
        }
        if (student.getCourseId() <= 0) {
            return false;
        }
        return studentDAO.save(student);
    }

    // update existing student
    public boolean updateStudent(Student student) {
        if (student == null || student.getStudentId() <= 0) {
            return false;
        }
        if (!validateStudent(student)) {
            return false;
        }
        return studentDAO.update(student);
    }

    // delete student by id
    public boolean deleteStudent(int studentId) {
        if (studentId <= 0) {
            return false;
        }
        return studentDAO.delete(studentId);
    }

    // get total student count for dashboard
    public int getStudentCount() {
        return studentDAO.count();
    }

    // helper method to validate required fields
    private boolean validateStudent(Student s) {
        if (s == null) {
            return false;
        }
        String reg = s.getRegistrationNumber();
        String first = s.getFirstName();
        String last = s.getLastName();

        return reg != null && !reg.trim().isEmpty()
                && first != null && !first.trim().isEmpty()
                && last != null && !last.trim().isEmpty();
    }
}
