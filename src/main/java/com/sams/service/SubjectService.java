package com.sams.service;

import com.sams.dao.SubjectDAO;
import com.sams.model.Subject;

import java.util.List;

/**
 * Service layer for subject management business logic.
 */
public class SubjectService {

    private final SubjectDAO subjectDAO = new SubjectDAO();

    public List<Subject> getAllSubjects() {
        return subjectDAO.findAll();
    }

    public List<Subject> getSubjectsByCourse(int courseId) {
        return subjectDAO.findByCourseId(courseId);
    }

    public List<Subject> getSubjectsByLecturer(int lecturerId) {
        return subjectDAO.findByLecturerId(lecturerId);
    }

    public boolean saveSubject(Subject subject) {
        if (subject.getSubjectCode() == null || subject.getSubjectCode().isBlank()) return false;
        if (subject.getSubjectName() == null || subject.getSubjectName().isBlank()) return false;
        if (subject.getCourseId() <= 0) return false;
        return subjectDAO.save(subject);
    }

    public boolean updateSubject(Subject subject) {
        if (subject.getSubjectCode() == null || subject.getSubjectCode().isBlank()) return false;
        if (subject.getSubjectName() == null || subject.getSubjectName().isBlank()) return false;
        return subjectDAO.update(subject);
    }

    public boolean deleteSubject(int id) {
        return subjectDAO.delete(id);
    }
}
