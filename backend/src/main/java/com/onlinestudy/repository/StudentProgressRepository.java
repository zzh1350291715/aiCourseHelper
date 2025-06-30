package com.onlinestudy.repository;

import com.onlinestudy.domain.StudentProgress;
import com.onlinestudy.dto.CourseProgressSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProgressRepository extends JpaRepository<StudentProgress, Long> {

    Optional<StudentProgress> findByStudent_IdAndMaterial_Id(Long studentId, Long materialId);

    List<StudentProgress> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    Optional<StudentProgress> findByStudent_IdAndCourse_IdAndMaterial_Id(Long studentId, Long courseId, Long materialId);

    @Query("SELECT new com.onlinestudy.dto.CourseProgressSummary(" +
           "c.id, c.title, " +
           "COUNT(cm.id), " +
           "COUNT(CASE WHEN sp.status = 'COMPLETED' THEN 1 END), " +
           "COUNT(CASE WHEN sp.status = 'IN_PROGRESS' THEN 1 END), " +
           "CAST(COUNT(CASE WHEN sp.status = 'COMPLETED' THEN 1 END) AS double) / COUNT(cm.id), " +
           "MAX(sp.lastAccessedAt)) " +
           "FROM Course c " +
           "LEFT JOIN CourseMaterial cm ON c.id = cm.course.id " +
           "LEFT JOIN StudentProgress sp ON c.id = sp.course.id AND cm.id = sp.material.id AND sp.student.id = :studentId " +
           "GROUP BY c.id, c.title")
    List<CourseProgressSummary> findCourseProgressSummaryByStudentId(@Param("studentId") Long studentId);
} 