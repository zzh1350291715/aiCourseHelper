package com.onlinestudy.dto;

import lombok.Data;

@Data
public class CourseCreationDto {
    private String title;
    private String description;
    private Long instructorId;
} 