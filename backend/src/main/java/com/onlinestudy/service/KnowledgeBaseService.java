package com.onlinestudy.service;

import com.onlinestudy.domain.KnowledgeBase;
import com.onlinestudy.domain.KnowledgeBaseDocument;

import com.onlinestudy.repository.KnowledgeBaseRepository;
import com.onlinestudy.repository.KnowledgeBaseDocumentRepository;
import com.onlinestudy.repository.CourseRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public interface KnowledgeBaseService {
    List<KnowledgeBase> getAllKnowledgeBases();
    KnowledgeBase createKnowledgeBase(String name, String description, Long courseId);
    void deleteKnowledgeBase(Long knowledgeBaseId);
    KnowledgeBaseDocument uploadDocument(Long knowledgeBaseId, MultipartFile file);
    void deleteDocument(Long documentId);
    void reprocessDocument(Long documentId);
    Optional<KnowledgeBase> getKnowledgeBaseByCourse(Long courseId);
    List<KnowledgeBaseDocument> getDocuments(Long knowledgeBaseId);
    
    // 基础功能接口（先实现简单版本）
    Map<String, Object> generateOutline(Long knowledgeBaseId);
    Map<String, Object> generatePPT(Long knowledgeBaseId);
    Map<String, Object> generateQuiz(Long knowledgeBaseId);
    Map<String, Object> answerQuestion(Long knowledgeBaseId, String question);
    Map<String, Object> autoGrade(Map<String, Object> request);
    List<KnowledgeBase> getKnowledgeBasesByCourseId(Long courseId);
}

@Service
class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final KnowledgeBaseDocumentRepository documentRepository;
    private final CourseRepository courseRepository;

    public KnowledgeBaseServiceImpl(KnowledgeBaseRepository knowledgeBaseRepository,
                                   KnowledgeBaseDocumentRepository documentRepository,
                                   CourseRepository courseRepository) {
        this.knowledgeBaseRepository = knowledgeBaseRepository;
        this.documentRepository = documentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public List<KnowledgeBase> getAllKnowledgeBases() {
        return knowledgeBaseRepository.findAll();
    }

    @Transactional
    @Override
    public KnowledgeBase createKnowledgeBase(String name, String description, Long courseId) {
        System.out.println("=== 服务层：开始创建知识库 ===");
        System.out.println("参数 - name: " + name + ", description: " + description + ", courseId: " + courseId);
        
        KnowledgeBase kb = new KnowledgeBase();
        kb.setName(name);
        kb.setDescription(description);
        
        System.out.println("创建知识库对象完成");
        
        if (courseId != null) {
            System.out.println("查找课程ID: " + courseId);
            courseRepository.findById(courseId).ifPresent(course -> {
                System.out.println("找到课程: " + course.getTitle());
                kb.setCourse(course);
            });
        }
        
        System.out.println("准备保存知识库到数据库...");
        KnowledgeBase savedKb = knowledgeBaseRepository.save(kb);
        System.out.println("知识库保存成功，生成ID: " + savedKb.getId());
        
        return savedKb;
    }

    @Transactional
    @Override
    public void deleteKnowledgeBase(Long knowledgeBaseId) {
        knowledgeBaseRepository.deleteById(knowledgeBaseId);
    }

    @Override
    public KnowledgeBaseDocument uploadDocument(Long knowledgeBaseId, MultipartFile file) {
        return knowledgeBaseRepository.findById(knowledgeBaseId)
                .map(kb -> {
                    try {
                        // 创建上传目录
                        String uploadDir = "uploads/knowledge-base/" + knowledgeBaseId;
                        Path uploadPath = Paths.get(uploadDir);
                        if (!Files.exists(uploadPath)) {
                            Files.createDirectories(uploadPath);
                        }

                        // 保存文件
                        String fileName = file.getOriginalFilename();
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(file.getInputStream(), filePath);

                        // 保存文档记录
                        KnowledgeBaseDocument doc = new KnowledgeBaseDocument();
                        doc.setKnowledgeBase(kb);
                        doc.setFileName(fileName);
                        doc.setFilePath(filePath.toString());
                        doc.setStatus(KnowledgeBaseDocument.Status.READY);

                        return documentRepository.save(doc);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to upload file", e);
                    }
                })
                .orElseThrow(() -> new IllegalArgumentException("Knowledge base not found"));
    }

    @Override
    public Optional<KnowledgeBase> getKnowledgeBaseByCourse(Long courseId) {
        return knowledgeBaseRepository.findByCourse_Id(courseId);
    }

    @Override
    public void deleteDocument(Long documentId) {
        documentRepository.deleteById(documentId);
    }

    @Override
    public void reprocessDocument(Long documentId) {
        documentRepository.findById(documentId).ifPresent(doc -> {
            doc.setStatus(KnowledgeBaseDocument.Status.PROCESSING);
            documentRepository.save(doc);
            // TODO: 实际的重新处理逻辑
        });
    }

    @Override
    public List<KnowledgeBaseDocument> getDocuments(Long knowledgeBaseId) {
        return documentRepository.findByKnowledgeBase_Id(knowledgeBaseId);
    }

    @Override
    public Map<String, Object> generateOutline(Long knowledgeBaseId) {
        // TODO: 调用AI服务生成大纲
        Map<String, Object> outline = new HashMap<>();
        outline.put("title", "课程大纲");
        outline.put("chapters", Arrays.asList(
            "第一章：基础知识",
            "第二章：核心概念", 
            "第三章：实践应用"
        ));
        outline.put("generatedAt", new Date());
        return outline;
    }

    @Override
    public Map<String, Object> generatePPT(Long knowledgeBaseId) {
        // TODO: 调用AI服务生成PPT
        Map<String, Object> ppt = new HashMap<>();
        ppt.put("title", "授课PPT");
        ppt.put("slides", Arrays.asList(
            "第1页：课程介绍",
            "第2页：学习目标",
            "第3页：主要内容",
            "第4页：总结"
        ));
        ppt.put("generatedAt", new Date());
        return ppt;
    }

    @Override
    public Map<String, Object> generateQuiz(Long knowledgeBaseId) {
        // TODO: 调用AI服务生成测验
        Map<String, Object> quiz = new HashMap<>();
        quiz.put("title", "自动生成测验");
        quiz.put("questions", Arrays.asList(
            Map.of("question", "什么是基础概念？", "type", "multiple_choice", "options", Arrays.asList("A", "B", "C", "D")),
            Map.of("question", "请描述核心理论", "type", "short_answer"),
            Map.of("question", "判断题：理论正确", "type", "true_false")
        ));
        quiz.put("generatedAt", new Date());
        return quiz;
    }

    @Override
    public Map<String, Object> answerQuestion(Long knowledgeBaseId, String question) {
        // TODO: 调用AI服务回答问题
        Map<String, Object> answer = new HashMap<>();
        answer.put("question", question);
        answer.put("answer", "这是一个基于知识库内容的回答示例。");
        answer.put("confidence", 0.85);
        answer.put("sources", Arrays.asList("文档1", "文档2"));
        return answer;
    }

    @Override
    public Map<String, Object> autoGrade(Map<String, Object> request) {
        // TODO: 实现自动评分逻辑
        Map<String, Object> result = new HashMap<>();
        result.put("score", 85);
        result.put("maxScore", 100);
        result.put("feedback", "整体表现良好，需要在细节方面加强。");
        result.put("gradedAt", new Date());
        return result;
    }

    @Override
    public List<KnowledgeBase> getKnowledgeBasesByCourseId(Long courseId) {
        return knowledgeBaseRepository.findAllByCourse_Id(courseId);
    }
} 