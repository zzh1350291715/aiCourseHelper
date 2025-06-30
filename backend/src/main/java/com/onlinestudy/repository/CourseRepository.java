package com.onlinestudy.repository;

import com.onlinestudy.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByInstructor_Id(Long instructorId);

} 