package com.sams.service;

import com.sams.dao.AttendanceDAO;
import com.sams.model.Attendance;

import java.time.LocalDate;
import java.util.List;

// AttendanceService manages attendance recording and reports
public class AttendanceService {

    private AttendanceDAO attendanceDAO = new AttendanceDAO();

    // get attendance records for a specific session
    public List<Attendance> getAttendanceBySession(int sessionId) {
        if (sessionId <= 0) {
            return List.of();
        }
        return attendanceDAO.findBySessionId(sessionId);
    }

    // save or update attendance records for a session
    public boolean saveAttendance(List<Attendance> records) {
        if (records == null || records.isEmpty()) {
            return false;
        }
        return attendanceDAO.saveAll(records);
    }

    // get attendance report with optional filters
    public List<Attendance> getAttendanceReport(Integer studentId, Integer subjectId,
                                                LocalDate startDate, LocalDate endDate) {
        return attendanceDAO.getReport(studentId, subjectId, startDate, endDate);
    }
}
