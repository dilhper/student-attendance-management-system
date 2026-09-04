package com.sams.service;

import com.sams.dao.CourseDAO;
import com.sams.model.Course;

import java.util.List;

/**
 * Service Layer: CourseService
 * 
 * Manages institutional course degree programmes, validating academic course codes
 * and titles before delegating persistent operations to the CourseDAO.
 */
public class CourseService {

    private final CourseDAO courseDAO = new CourseDAO();

    /**
     * Fetches all registered academic courses.
     * 
     * @return List of all Course entities
     */
    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    /**
     * Retrieves a single academic course by its unique database key.
     * 
     * @param targetCourseId Course identifier
     * @return Course entity or null if not located
     */
    public Course getCourseById(int targetCourseId) {
        if (targetCourseId <= 0) {
            return null;
        }
        return courseDAO.findById(targetCourseId);
    }

    /**
     * Validates and persists a new academic course.
     * 
     * @param newCourse Course instance to register
     * @return true on successful insertion, false if validation fails
     */
    public boolean saveCourse(Course newCourse) {
        if (!validateCourseDetails(newCourse)) {
            return false;
        }
        return courseDAO.save(newCourse);
    }

    /**
     * Validates and modifies an existing course entry.
     * 
     * @param updatedCourse Course instance with updated values
     * @return true on successful modification, false otherwise
     */
    public boolean updateCourse(Course updatedCourse) {
        if (updatedCourse == null || updatedCourse.getCourseId() <= 0) {
            return false;
        }
        if (!validateCourseDetails(updatedCourse)) {
            return false;
        }
        return courseDAO.update(updatedCourse);
    }

    /**
     * Deletes a course and cascades removal of associated entities.
     * 
     * @param targetCourseId Primary key of the course to remove
     * @return true if deletion succeeded, false otherwise
     */
    public boolean deleteCourse(int targetCourseId) {
        if (targetCourseId <= 0) {
            return false;
        }
        return courseDAO.delete(targetCourseId);
    }

    /**
     * Returns total number of active courses for administrative statistics.
     * 
     * @return count integer
     */
    public int getCourseCount() {
        return courseDAO.count();
    }

    /**
     * Validates required academic identifiers for courses.
     * 
     * @param candidateCourse Course instance to inspect
     * @return true if course code and title are non-empty
     */
    private boolean validateCourseDetails(Course candidateCourse) {
        if (candidateCourse == null) {
            return false;
        }
        String courseCode = candidateCourse.getCourseCode();
        String courseName = candidateCourse.getCourseName();

        return courseCode != null && !courseCode.trim().isEmpty()
                && courseName != null && !courseName.trim().isEmpty();
    }
}
