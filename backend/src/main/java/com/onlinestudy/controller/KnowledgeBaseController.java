package com.onlinestudy.controller;

import com.onlinestudy.domain.KnowledgeBase;
import com.onlinestudy.domain.KnowledgeBaseDocument;
import com.onlinestudy.service.KnowledgeBaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge-base")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    public KnowledgeBaseController(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    // 获取所有知识库
    @GetMapping
    public ResponseEntity<List<KnowledgeBase>> getAllKnowledgeBases() {
        List<KnowledgeBase> knowledgeBases = knowledgeBaseService.getAllKnowledgeBases();
        return ResponseEntity.ok(knowledgeBases);
    }

    // 删除知识库
    @DeleteMapping("/{knowledgeBaseId}")
    public ResponseEntity<Void> deleteKnowledgeBase(@PathVariable Long knowledgeBaseId) {
        try {
            knowledgeBaseService.deleteKnowledgeBase(knowledgeBaseId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<KnowledgeBase> createKnowledgeBase(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "sessionId", required = false) String sessionId) {
        System.out.println("=== 开始创建知识库 ===");
        System.out.println("请求参数: " + request);
        System.out.println("sessionId: " + sessionId);

        try {
            // 验证用户认证
            if (sessionId == null || sessionId.trim().isEmpty()) {
                System.out.println("错误：缺少sessionId");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String name = request.get("name") != null ? request.get("name").toString() : null;
            String description = request.get("description") != null ? request.get("description").toString() : "";
            
            System.out.println("解析参数 - name: " + name + ", description: " + description);

            if (name == null || name.trim().isEmpty()) {
                System.out.println("错误：名称为空");
                return ResponseEntity.badRequest().build();
            }

            // courseId现在是必需的
            Long courseId = null;
            if (request.get("courseId") != null) {
                try {
                    courseId = Long.valueOf(request.get("courseId").toString());
                    System.out.println("课程ID: " + courseId);
                } catch (NumberFormatException e) {
                    System.out.println("错误：课程ID格式无效");
                    return ResponseEntity.badRequest().build();
                }
            } else {
                System.out.println("错误：缺少课程ID");
                return ResponseEntity.badRequest().build();
            }

            System.out.println("调用服务层创建知识库...");
            KnowledgeBase kb = knowledgeBaseService.createKnowledgeBase(name, description, courseId);
            System.out.println("知识库创建成功，ID: " + kb.getId());

            return ResponseEntity.ok(kb);
        } catch (Exception e) {
            System.out.println("创建知识库异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/upload/{knowledgeBaseId}")
    public ResponseEntity<KnowledgeBaseDocument> uploadDocument(
            @PathVariable Long knowledgeBaseId,
            @RequestParam("file") MultipartFile file) {
        try {
            KnowledgeBaseDocument doc = knowledgeBaseService.uploadDocument(knowledgeBaseId, file);
            return ResponseEntity.ok(doc);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<KnowledgeBase> getKnowledgeBaseByCourse(@PathVariable Long courseId) {
        return knowledgeBaseService.getKnowledgeBaseByCourse(courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/documents/{knowledgeBaseId}")
    public ResponseEntity<List<KnowledgeBaseDocument>> getDocuments(@PathVariable Long knowledgeBaseId) {
        try {
            List<KnowledgeBaseDocument> documents = knowledgeBaseService.getDocuments(knowledgeBaseId);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/generate-outline/{knowledgeBaseId}")
    public ResponseEntity<Map<String, Object>> generateOutline(@PathVariable Long knowledgeBaseId) {
        try {
            Map<String, Object> outline = knowledgeBaseService.generateOutline(knowledgeBaseId);
            return ResponseEntity.ok(outline);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/generate-ppt/{knowledgeBaseId}")
    public ResponseEntity<Map<String, Object>> generatePPT(@PathVariable Long knowledgeBaseId) {
        try {
            Map<String, Object> ppt = knowledgeBaseService.generatePPT(knowledgeBaseId);
            return ResponseEntity.ok(ppt);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/generate-quiz/{knowledgeBaseId}")
    public ResponseEntity<Map<String, Object>> generateQuiz(@PathVariable Long knowledgeBaseId) {
        try {
            Map<String, Object> quiz = knowledgeBaseService.generateQuiz(knowledgeBaseId);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 问答测试
    @PostMapping("/qa/{knowledgeBaseId}")
    public ResponseEntity<Map<String, Object>> testQA(
            @PathVariable Long knowledgeBaseId,
            @RequestBody Map<String, Object> request) {
        try {
            String question = request.get("question").toString();
            Map<String, Object> answer = knowledgeBaseService.answerQuestion(knowledgeBaseId, question);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 删除文档
    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long documentId) {
        try {
            knowledgeBaseService.deleteDocument(documentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 重新处理文档
    @PostMapping("/reprocess/{documentId}")
    public ResponseEntity<Void> reprocessDocument(@PathVariable Long documentId) {
        try {
            knowledgeBaseService.reprocessDocument(documentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/auto-grade")
    public ResponseEntity<Map<String, Object>> autoGrade(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = knowledgeBaseService.autoGrade(request);
        return ResponseEntity.ok(result);
    }

    // 测试接口
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> test(@RequestBody Map<String, Object> request) {
        System.out.println("=== 测试接口被调用 ===");
        System.out.println("接收到的数据: " + request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "测试成功");
        response.put("receivedData", request);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }
}
