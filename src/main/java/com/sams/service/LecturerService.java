package com.sams.service;

import com.sams.dao.LecturerDAO;
import com.sams.model.Lecturer;

import java.util.List;

/**
 * Service layer for lecturer management business logic.
 */
public class LecturerService {

    private final LecturerDAO lecturerDAO = new LecturerDAO();

    public List<Lecturer> getAllLecturers() {
        return lecturerDAO.findAll();
    }

    public Lecturer getLecturerById(int id) {
        return lecturerDAO.findById(id);
    }

    public boolean saveLecturer(Lecturer lecturer) {
        if (lecturer.getFirstName() == null || lecturer.getFirstName().isBlank()) return false;
        if (lecturer.getLastName() == null || lecturer.getLastName().isBlank()) return false;
        return lecturerDAO.save(lecturer);
    }

    public boolean updateLecturer(Lecturer lecturer) {
        if (lecturer.getFirstName() == null || lecturer.getFirstName().isBlank()) return false;
        if (lecturer.getLastName() == null || lecturer.getLastName().isBlank()) return false;
        return lecturerDAO.update(lecturer);
    }

    public boolean deleteLecturer(int id) {
        return lecturerDAO.delete(id);
    }

    public int getLecturerCount() {
        return lecturerDAO.count();
    }

    public List<Integer> getAssignedSubjectIds(int lecturerId) {
        return lecturerDAO.getAssignedSubjectIds(lecturerId);
    }

    public boolean updateSubjectAssignments(int lecturerId, List<Integer> subjectIds) {
        return lecturerDAO.updateSubjectAssignments(lecturerId, subjectIds);
    }
}
