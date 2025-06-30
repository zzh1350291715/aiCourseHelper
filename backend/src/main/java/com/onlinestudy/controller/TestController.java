package com.onlinestudy.controller;

import com.onlinestudy.domain.CourseMaterial;
import com.onlinestudy.repository.CourseMaterialRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final CourseMaterialRepository materialRepository;

    public TestController(CourseMaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @GetMapping("/materials")
    public ResponseEntity<List<CourseMaterial>> getAllMaterials() {
        List<CourseMaterial> materials = materialRepository.findAll();
        return ResponseEntity.ok(materials);
    }

    @PostMapping("/material")
    public ResponseEntity<CourseMaterial> createTestMaterial() {
        CourseMaterial material = new CourseMaterial();
        material.setTitle("测试材料");
        material.setMaterialType(CourseMaterial.MaterialType.PDF);
        material.setOrder(1);
        
        CourseMaterial saved = materialRepository.save(material);
        return ResponseEntity.ok(saved);
    }
} 