package com.onlinestudy.service;

import com.onlinestudy.domain.AiChatHistory;
import com.onlinestudy.domain.Course;
import com.onlinestudy.domain.User;
import com.onlinestudy.dto.AiChatResponse;
import com.onlinestudy.repository.AiChatHistoryRepository;
import com.onlinestudy.repository.CourseRepository;
import com.onlinestudy.repository.KnowledgeBaseRepository;
import com.onlinestudy.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AiChatService {

    private final AiChatHistoryRepository chatHistoryRepository;
    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public AiChatService(AiChatHistoryRepository chatHistoryRepository,
                        KnowledgeBaseRepository knowledgeBaseRepository,
                        UserRepository userRepository,
                        CourseRepository courseRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.knowledgeBaseRepository = knowledgeBaseRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public AiChatResponse askQuestion(Long studentId, Long courseId, String question) {
        // 这里应该调用实际的AI服务
        // 暂时返回模拟回答
        String answer = generateAnswer(question, courseId);
        
        // 获取学生和课程对象
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        
        // 保存聊天记录
        AiChatHistory chatHistory = new AiChatHistory();
        chatHistory.setStudent(student);
        chatHistory.setCourse(course);
        chatHistory.setQuestion(question);
        chatHistory.setAnswer(answer);
        chatHistory.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        
        chatHistoryRepository.save(chatHistory);
        
        return new AiChatResponse(answer, chatHistory.getId());
    }

    public List<AiChatHistory> getChatHistory(Long studentId, Long courseId) {
        return chatHistoryRepository.findByStudent_IdAndCourse_IdOrderByTimestampDesc(studentId, courseId);
    }

    public List<AiChatHistory> getAllChatHistory(Long studentId) {
        return chatHistoryRepository.findByStudent_IdOrderByTimestampDesc(studentId);
    }

    private String generateAnswer(String question, Long courseId) {
        // 这里应该集成实际的AI模型
        // 可以使用课程的知识库来增强回答
        return "这是对您问题的回答。在实际实现中，这里会调用AI模型来生成基于课程内容的回答。";
    }
} 