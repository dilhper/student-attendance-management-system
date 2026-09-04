package com.sams.service;

import com.sams.dao.AttendanceDAO;
import com.sams.model.Attendance;

import java.time.LocalDate;
import java.util.List;

/**
 * Service layer for attendance marking and reporting.
 */
public class AttendanceService {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    /**
     * Returns existing attendance records for a session.
     */
    public List<Attendance> getAttendanceBySession(int sessionId) {
        return attendanceDAO.findBySessionId(sessionId);
    }

    /**
     * Saves (or updates) attendance records for an entire session.
     */
    public boolean saveAttendance(List<Attendance> records) {
        if (records == null || records.isEmpty()) return false;
        return attendanceDAO.saveAll(records);
    }

    /**
     * Returns a filtered attendance report.
     * All parameters are optional — pass null to skip a filter.
     */
    public List<Attendance> getAttendanceReport(Integer studentId, Integer subjectId,
                                                 LocalDate dateFrom, LocalDate dateTo) {
        return attendanceDAO.getReport(studentId, subjectId, dateFrom, dateTo);
    }
}
