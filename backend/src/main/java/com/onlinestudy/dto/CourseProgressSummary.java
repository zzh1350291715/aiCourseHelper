package com.onlinestudy.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CourseProgressSummary {
    private Long courseId;
    private String courseName;
    private Long totalMaterials;
    private Long completedMaterials;
    private Long inProgressMaterials;
    private Double completionRate;
    private String lastAccessed;

    public CourseProgressSummary() {
    }

    public CourseProgressSummary(Long courseId, String courseName, Long totalMaterials, 
                                Long completedMaterials, Long inProgressMaterials, 
                                Double completionRate, Timestamp lastAccessed) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.totalMaterials = totalMaterials;
        this.completedMaterials = completedMaterials;
        this.inProgressMaterials = inProgressMaterials;
        this.completionRate = completionRate;
        this.lastAccessed = lastAccessed != null ? lastAccessed.toString() : null;
    }
} 