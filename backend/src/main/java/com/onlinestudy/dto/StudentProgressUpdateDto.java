package com.onlinestudy.dto;

import lombok.Data;

@Data
public class StudentProgressUpdateDto {
    private Long studentId;
    private Long courseId;
    private Long materialId;
    private String status; // ACCESSED, IN_PROGRESS, COMPLETED
} 