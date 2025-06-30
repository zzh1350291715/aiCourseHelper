package com.onlinestudy.repository;

import com.onlinestudy.domain.StudentQuizReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentQuizReportRepository extends JpaRepository<StudentQuizReport, Long> {
    Optional<StudentQuizReport> findByStudentIdAndQuizId(Long studentId, Long quizId);
    List<StudentQuizReport> findByStudentId(Long studentId);
    long countByQuizId(Long quizId);
    List<StudentQuizReport> findByQuizId(Long quizId);
} 