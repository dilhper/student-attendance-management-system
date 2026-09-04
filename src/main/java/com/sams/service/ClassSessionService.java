package com.sams.service;

import com.sams.dao.ClassSessionDAO;
import com.sams.model.ClassSession;

import java.util.List;

/**
 * Service layer for class scheduling business logic.
 */
public class ClassSessionService {

    private final ClassSessionDAO sessionDAO = new ClassSessionDAO();

    public List<ClassSession> getAllSessions() {
        return sessionDAO.findAll();
    }

    public ClassSession getSessionById(int id) {
        return sessionDAO.findById(id);
    }

    public List<ClassSession> getSessionsByLecturer(int lecturerId) {
        return sessionDAO.findByLecturerId(lecturerId);
    }

    public boolean saveSession(ClassSession session) {
        if (session.getSubjectId() <= 0) return false;
        if (session.getLecturerId() <= 0) return false;
        if (session.getSessionDate() == null) return false;
        if (session.getStartTime() == null || session.getEndTime() == null) return false;
        if (session.getStartTime().isAfter(session.getEndTime())) return false;
        return sessionDAO.save(session);
    }

    public boolean updateSession(ClassSession session) {
        if (session.getSessionDate() == null) return false;
        if (session.getStartTime() == null || session.getEndTime() == null) return false;
        if (session.getStartTime().isAfter(session.getEndTime())) return false;
        return sessionDAO.update(session);
    }

    public boolean deleteSession(int id) {
        return sessionDAO.delete(id);
    }

    public int getSessionCount() {
        return sessionDAO.count();
    }
}
