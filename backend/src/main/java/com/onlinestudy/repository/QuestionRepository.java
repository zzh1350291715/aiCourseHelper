package com.onlinestudy.repository;

import com.onlinestudy.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByQuizId(Long quizId);
    
    List<Question> findByKnowledgeBaseId(Long knowledgeBaseId);
    
    List<Question> findByKnowledgeBaseIdAndQuestionType(Long knowledgeBaseId, Question.QuestionType questionType);

    List<Question> findByQuestionType(Question.QuestionType questionType);
    List<Question> findByDifficulty(Question.Difficulty difficulty);
    List<Question> findByQuestionTypeAndDifficulty(Question.QuestionType questionType, Question.Difficulty difficulty);
} 