package com.sams.service;

import com.sams.dao.StudentDAO;
import com.sams.model.Student;

import java.util.List;

/**
 * Service layer for student management business logic.
 */
public class StudentService {

    private final StudentDAO studentDAO = new StudentDAO();

    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    public Student getStudentById(int id) {
        return studentDAO.findById(id);
    }

    public List<Student> getStudentsByCourse(int courseId) {
        return studentDAO.findByCourseId(courseId);
    }

    public List<Student> getStudentsBySession(int sessionId) {
        return studentDAO.findBySessionId(sessionId);
    }

    public boolean saveStudent(Student student) {
        if (student.getRegistrationNumber() == null || student.getRegistrationNumber().isBlank()) return false;
        if (student.getFirstName() == null || student.getFirstName().isBlank()) return false;
        if (student.getLastName() == null || student.getLastName().isBlank()) return false;
        if (student.getCourseId() <= 0) return false;
        return studentDAO.save(student);
    }

    public boolean updateStudent(Student student) {
        if (student.getRegistrationNumber() == null || student.getRegistrationNumber().isBlank()) return false;
        if (student.getFirstName() == null || student.getFirstName().isBlank()) return false;
        if (student.getLastName() == null || student.getLastName().isBlank()) return false;
        return studentDAO.update(student);
    }

    public boolean deleteStudent(int id) {
        return studentDAO.delete(id);
    }

    public int getStudentCount() {
        return studentDAO.count();
    }
}
