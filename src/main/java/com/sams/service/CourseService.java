package com.sams.service;

import com.sams.dao.CourseDAO;
import com.sams.model.Course;

import java.util.List;

/**
 * Service layer for course management business logic.
 */
public class CourseService {

    private final CourseDAO courseDAO = new CourseDAO();

    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    public Course getCourseById(int id) {
        return courseDAO.findById(id);
    }

    public boolean saveCourse(Course course) {
        // Validation
        if (course.getCourseCode() == null || course.getCourseCode().isBlank()) return false;
        if (course.getCourseName() == null || course.getCourseName().isBlank()) return false;
        return courseDAO.save(course);
    }

    public boolean updateCourse(Course course) {
        if (course.getCourseCode() == null || course.getCourseCode().isBlank()) return false;
        if (course.getCourseName() == null || course.getCourseName().isBlank()) return false;
        return courseDAO.update(course);
    }

    public boolean deleteCourse(int id) {
        return courseDAO.delete(id);
    }

    public int getCourseCount() {
        return courseDAO.count();
    }
}
