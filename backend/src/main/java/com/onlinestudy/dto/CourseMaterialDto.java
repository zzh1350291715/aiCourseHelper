package com.onlinestudy.dto;

import com.onlinestudy.domain.CourseMaterial;
import lombok.Data;

@Data
public class CourseMaterialDto {
    private Long courseId;
    private String title;
    private CourseMaterial.MaterialType materialType;
    private String contentUrl;
    private int order;
} 