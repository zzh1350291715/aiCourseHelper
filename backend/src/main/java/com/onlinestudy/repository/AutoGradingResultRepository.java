package com.onlinestudy.repository;

import com.onlinestudy.domain.AutoGradingResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutoGradingResultRepository extends JpaRepository<AutoGradingResult, Long> {
    
    List<AutoGradingResult> findByQuiz_Id(Long quizId);
    
    List<AutoGradingResult> findByStudent_Id(Long studentId);
    
    Optional<AutoGradingResult> findByQuiz_IdAndStudent_Id(Long quizId, Long studentId);
    
    List<AutoGradingResult> findByStatus(AutoGradingResult.GradingStatus status);
    
    List<AutoGradingResult> findByQuiz_IdAndStatus(Long quizId, AutoGradingResult.GradingStatus status);
} 