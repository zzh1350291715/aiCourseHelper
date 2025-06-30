package com.onlinestudy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auto_grading_results")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AutoGradingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "student_answer_file_path")
    private String studentAnswerFilePath; // 学生答卷文件路径

    @Column(name = "total_score", precision = 5, scale = 2)
    private BigDecimal totalScore;

    @Column(name = "max_score", precision = 5, scale = 2)
    private BigDecimal maxScore;

    @Column(name = "score_percentage", precision = 5, scale = 2)
    private BigDecimal scorePercentage;

    @Column(columnDefinition = "JSON")
    private String detailedScores; // JSON格式存储详细评分

    @Column(columnDefinition = "TEXT")
    private String feedback; // 评分反馈

    @Column(columnDefinition = "JSON")
    private String aiAnalysis; // AI分析结果

    @Enumerated(EnumType.STRING)
    private GradingStatus status = GradingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private GradingMethod method = GradingMethod.AUTO;

    @Column(name = "processing_time_seconds")
    private Integer processingTimeSeconds;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public enum GradingStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, MANUAL_REVIEW_REQUIRED
    }

    public enum GradingMethod {
        AUTO, MANUAL, HYBRID
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public String getStudentAnswerFilePath() {
        return studentAnswerFilePath;
    }

    public void setStudentAnswerFilePath(String studentAnswerFilePath) {
        this.studentAnswerFilePath = studentAnswerFilePath;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }

    public BigDecimal getScorePercentage() {
        return scorePercentage;
    }

    public void setScorePercentage(BigDecimal scorePercentage) {
        this.scorePercentage = scorePercentage;
    }

    public String getDetailedScores() {
        return detailedScores;
    }

    public void setDetailedScores(String detailedScores) {
        this.detailedScores = detailedScores;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getAiAnalysis() {
        return aiAnalysis;
    }

    public void setAiAnalysis(String aiAnalysis) {
        this.aiAnalysis = aiAnalysis;
    }

    public GradingStatus getStatus() {
        return status;
    }

    public void setStatus(GradingStatus status) {
        this.status = status;
    }

    public GradingMethod getMethod() {
        return method;
    }

    public void setMethod(GradingMethod method) {
        this.method = method;
    }

    public Integer getProcessingTimeSeconds() {
        return processingTimeSeconds;
    }

    public void setProcessingTimeSeconds(Integer processingTimeSeconds) {
        this.processingTimeSeconds = processingTimeSeconds;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
} 