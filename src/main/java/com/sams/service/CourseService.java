package com.sams.service;

import com.sams.dao.CourseDAO;
import com.sams.model.Course;

import java.util.List;

// CourseService handles business logic and validations for courses
public class CourseService {

    private CourseDAO courseDAO = new CourseDAO();

    // get all courses
    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    // find course by id
    public Course getCourseById(int courseId) {
        if (courseId <= 0) {
            return null;
        }
        return courseDAO.findById(courseId);
    }

    // save a new course
    public boolean saveCourse(Course course) {
        if (!validateCourse(course)) {
            return false;
        }
        return courseDAO.save(course);
    }

    // update course details
    public boolean updateCourse(Course course) {
        if (course == null || course.getCourseId() <= 0) {
            return false;
        }
        if (!validateCourse(course)) {
            return false;
        }
        return courseDAO.update(course);
    }

    // delete course by id
    public boolean deleteCourse(int courseId) {
        if (courseId <= 0) {
            return false;
        }
        return courseDAO.delete(courseId);
    }

    // get total course count for dashboard
    public int getCourseCount() {
        return courseDAO.count();
    }

    // validation helper for course fields
    private boolean validateCourse(Course c) {
        if (c == null) {
            return false;
        }
        String code = c.getCourseCode();
        String name = c.getCourseName();

        return code != null && !code.trim().isEmpty()
                && name != null && !name.trim().isEmpty();
    }
}
