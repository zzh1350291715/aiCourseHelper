package com.onlinestudy.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_quiz_report", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "quiz_id"}))
public class StudentQuizReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    private String status; // NOT_STARTED, SUBMITTED, COMPLETED
    private LocalDateTime submittedAt;
    private Double score; // 测验得分
} 