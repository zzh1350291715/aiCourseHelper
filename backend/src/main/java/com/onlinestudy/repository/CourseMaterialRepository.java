package com.onlinestudy.repository;

import com.onlinestudy.domain.CourseMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long> {

    List<CourseMaterial> findByCourse_Id(Long courseId);
    
    List<CourseMaterial> findByCourse_IdOrderByOrderAsc(Long courseId);
    
    long countByCourse_Id(Long courseId);
} 