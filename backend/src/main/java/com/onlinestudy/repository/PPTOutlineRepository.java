package com.onlinestudy.repository;

import com.onlinestudy.domain.PPTOutline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PPTOutlineRepository extends JpaRepository<PPTOutline, Long> {
    
    List<PPTOutline> findByCourse_Id(Long courseId);
    
    List<PPTOutline> findByKnowledgeBase_Id(Long knowledgeBaseId);
    
    Optional<PPTOutline> findByCourse_IdAndChapterName(Long courseId, String chapterName);
    
    List<PPTOutline> findByCourse_IdAndStatus(Long courseId, PPTOutline.GenerationStatus status);
} 