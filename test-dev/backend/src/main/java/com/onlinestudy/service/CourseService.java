package com.onlinestudy.service;

import com.onlinestudy.domain.Course;
import com.onlinestudy.domain.User;
import com.onlinestudy.dto.CourseCreationDto;
import com.onlinestudy.repository.CourseRepository;
import com.onlinestudy.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    Course createCourse(CourseCreationDto courseDto);
    Optional<Course> getCourseById(Long courseId);
    List<Course> getCoursesByInstructor(Long instructorId);
    List<Course> getAllCourses();
}

@Service
class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseServiceImpl(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public Course createCourse(CourseCreationDto courseDto) {
        User instructor = userRepository.findById(courseDto.getInstructorId())
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        if (instructor.getRole() != User.Role.TEACHER) {
            throw new IllegalArgumentException("用户不是教师,请切换身份");
        }

        Course newCourse = new Course();
        newCourse.setTitle(courseDto.getTitle());
        newCourse.setDescription(courseDto.getDescription());
        newCourse.setInstructor(instructor);

        return courseRepository.save(newCourse);
    }

    @Override
    public Optional<Course> getCourseById(Long courseId) {
        return courseRepository.findById(courseId);
    }

    @Override
    public List<Course> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructor_Id(instructorId);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}
