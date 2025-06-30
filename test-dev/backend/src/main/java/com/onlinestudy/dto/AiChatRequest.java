package com.onlinestudy.dto;

import lombok.Data;

@Data
public class AiChatRequest {
    private Long studentId;
    private Long courseId;
    private String question;
} 