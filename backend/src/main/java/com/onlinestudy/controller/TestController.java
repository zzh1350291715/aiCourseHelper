package com.onlinestudy.controller;

import com.onlinestudy.domain.Course;
import com.onlinestudy.domain.CourseMaterial;
import com.onlinestudy.repository.CourseMaterialRepository;
import com.onlinestudy.repository.CourseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    private final CourseMaterialRepository materialRepository;
    private final CourseRepository courseRepository;

    public TestController(CourseMaterialRepository materialRepository, CourseRepository courseRepository) {
        this.materialRepository = materialRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/materials")
    public ResponseEntity<List<CourseMaterial>> getAllMaterials() {
        List<CourseMaterial> materials = materialRepository.findAll();
        return ResponseEntity.ok(materials);
    }

    @PostMapping("/material")
    public ResponseEntity<CourseMaterial> createTestMaterial() {
        // 首先查找一个存在的课程
        Course course = courseRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No course found"));
        
        CourseMaterial material = new CourseMaterial();
        material.setTitle("测试PDF材料");
        material.setMaterialType(CourseMaterial.MaterialType.PDF);
        material.setCourse(course);
        material.setOrder(1);
        material.setFileSize(1024000L); // 1MB
        material.setContentUrl("/uploads/test.pdf");
        
        CourseMaterial saved = materialRepository.save(material);
        return ResponseEntity.ok(saved);
    }
}