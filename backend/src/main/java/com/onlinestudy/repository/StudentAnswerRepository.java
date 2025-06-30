package com.onlinestudy.repository;

import com.onlinestudy.domain.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {

    Optional<StudentAnswer> findByStudentIdAndQuestionId(Long studentId, Long questionId);

    List<StudentAnswer> findByStudentIdAndQuizId(Long studentId, Long quizId);
    
    void deleteByQuizId(Long quizId);
} 