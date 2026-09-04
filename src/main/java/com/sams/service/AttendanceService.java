package com.sams.service;

import com.sams.dao.AttendanceDAO;
import com.sams.model.Attendance;

import java.time.LocalDate;
import java.util.List;

/**
 * Service Layer: AttendanceService
 * 
 * Coordinates business operations for recording student attendance per class session
 * and querying attendance records with multifaceted filtering criteria for reporting.
 */
public class AttendanceService {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    /**
     * Retrieves all recorded attendance entries for a specific class session.
     * 
     * @param targetSessionId Unique ID of the class session
     * @return List of Attendance records for that session
     */
    public List<Attendance> getAttendanceBySession(int targetSessionId) {
        if (targetSessionId <= 0) {
            return List.of();
        }
        return attendanceDAO.findBySessionId(targetSessionId);
    }

    /**
     * Persists or updates attendance status entries for an entire classroom session.
     * 
     * @param attendanceRecords Collection of attendance entries to record
     * @return true if all records were successfully stored, false if collection is invalid
     */
    public boolean saveAttendance(List<Attendance> attendanceRecords) {
        if (attendanceRecords == null || attendanceRecords.isEmpty()) {
            return false;
        }
        return attendanceDAO.saveAll(attendanceRecords);
    }

    /**
     * Generates a filtered attendance report based on specified optional criteria.
     * Supports filtering by student, course subject, and date intervals.
     * 
     * @param filterStudentId Optional student ID filter (pass null for all students)
     * @param filterSubjectId Optional subject ID filter (pass null for all subjects)
     * @param startDateInterval Optional beginning of date range (inclusive)
     * @param endDateInterval Optional end of date range (inclusive)
     * @return List of matching Attendance records
     */
    public List<Attendance> getAttendanceReport(Integer filterStudentId, Integer filterSubjectId,
                                                LocalDate startDateInterval, LocalDate endDateInterval) {
        return attendanceDAO.getReport(filterStudentId, filterSubjectId, startDateInterval, endDateInterval);
    }
}
