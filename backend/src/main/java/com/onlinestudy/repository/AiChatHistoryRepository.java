package com.onlinestudy.repository;

import com.onlinestudy.domain.AiChatHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiChatHistoryRepository extends JpaRepository<AiChatHistory, Long> {

    List<AiChatHistory> findByStudent_IdAndCourse_IdOrderByTimestampDesc(Long studentId, Long courseId, Pageable pageable);

    List<AiChatHistory> findByStudent_IdAndCourse_IdOrderByTimestampDesc(Long studentId, Long courseId);

    List<AiChatHistory> findByStudent_IdOrderByTimestampDesc(Long studentId);
} 