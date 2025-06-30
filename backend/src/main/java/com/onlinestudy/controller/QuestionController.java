package com.onlinestudy.controller;

import com.onlinestudy.domain.Question;
import com.onlinestudy.repository.QuestionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionRepository questionRepository;

    public QuestionController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @GetMapping
    public List<Question> getAllQuestions(@RequestParam(required = false) String questionType,
                                          @RequestParam(required = false) String difficulty) {
        boolean validType = false, validDiff = false;
        Question.QuestionType type = null;
        Question.Difficulty diff = null;
        if (questionType != null) {
            try { type = Question.QuestionType.valueOf(questionType); validType = true; } catch (Exception ignored) {}
        }
        if (difficulty != null) {
            try { diff = Question.Difficulty.valueOf(difficulty); validDiff = true; } catch (Exception ignored) {}
        }
        if (validType && validDiff) {
            return questionRepository.findByQuestionTypeAndDifficulty(type, diff);
        } else if (validType) {
            return questionRepository.findByQuestionType(type);
        } else if (validDiff) {
            return questionRepository.findByDifficulty(diff);
        } else {
            return questionRepository.findAll();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        Optional<Question> question = questionRepository.findById(id);
        return question.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Question createQuestion(@RequestBody Question question) {
        return questionRepository.save(question);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id, @RequestBody Question updated) {
        return questionRepository.findById(id)
                .map(q -> {
                    q.setQuestionText(updated.getQuestionText());
                    q.setQuestionType(updated.getQuestionType());
                    q.setOptions(updated.getOptions());
                    q.setCorrectAnswer(updated.getCorrectAnswer());
                    q.setKnowledgeBase(updated.getKnowledgeBase());
                    q.setDifficulty(updated.getDifficulty());
                    q.setTags(updated.getTags());
                    return ResponseEntity.ok(questionRepository.save(q));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        if (questionRepository.existsById(id)) {
            questionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/knowledge-base/{knowledgeBaseId}")
    public List<Question> getQuestionsByKnowledgeBase(@PathVariable Long knowledgeBaseId) {
        return questionRepository.findByKnowledgeBaseId(knowledgeBaseId);
    }

    @GetMapping("/knowledge-base/{knowledgeBaseId}/type/{questionType}")
    public List<Question> getQuestionsByKnowledgeBaseAndType(@PathVariable Long knowledgeBaseId, @PathVariable String questionType) {
        Question.QuestionType type = Question.QuestionType.valueOf(questionType.toUpperCase());
        return questionRepository.findByKnowledgeBaseIdAndQuestionType(knowledgeBaseId, type);
    }
} 