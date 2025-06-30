package com.onlinestudy.controller;

import com.onlinestudy.domain.AiChatHistory;
import com.onlinestudy.dto.AiChatRequest;
import com.onlinestudy.dto.AiChatResponse;
import com.onlinestudy.service.AiChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai-chat")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/ask")
    public ResponseEntity<AiChatResponse> askQuestion(@RequestBody AiChatRequest request) {
        AiChatResponse response = aiChatService.askQuestion(
            request.getStudentId(),
            request.getCourseId(),
            request.getQuestion()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/student/{studentId}/course/{courseId}")
    public ResponseEntity<List<AiChatHistory>> getChatHistory(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        List<AiChatHistory> history = aiChatService.getChatHistory(studentId, courseId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/student/{studentId}")
    public ResponseEntity<List<AiChatHistory>> getAllChatHistory(@PathVariable Long studentId) {
        List<AiChatHistory> history = aiChatService.getAllChatHistory(studentId);
        return ResponseEntity.ok(history);
    }
} 