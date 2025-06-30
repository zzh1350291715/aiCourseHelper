package com.onlinestudy.repository;

import com.onlinestudy.domain.CourseOutline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseOutlineRepository extends JpaRepository<CourseOutline, Long> {
    
    List<CourseOutline> findByCourse_Id(Long courseId);
    
    Optional<CourseOutline> findByCourse_IdAndStatus(Long courseId, CourseOutline.GenerationStatus status);
    
    List<CourseOutline> findByKnowledgeBase_Id(Long knowledgeBaseId);
} 