package com.onlinestudy.repository;

import com.onlinestudy.domain.KnowledgeBaseDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeBaseDocumentRepository extends JpaRepository<KnowledgeBaseDocument, Long> {

    List<KnowledgeBaseDocument> findByKnowledgeBase_Id(Long knowledgeBaseId);
} 