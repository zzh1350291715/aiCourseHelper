package com.onlinestudy.controller;

import com.onlinestudy.domain.StudentProgress;
import com.onlinestudy.dto.CourseProgressSummary;
import com.onlinestudy.dto.StudentAccessDto;
import com.onlinestudy.dto.StudentProgressUpdateDto;
import com.onlinestudy.service.StudentProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-progress")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class StudentProgressController {

    private final StudentProgressService progressService;

    public StudentProgressController(StudentProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<List<StudentProgress>> getStudentProgress(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        List<StudentProgress> progress = progressService.getStudentProgress(studentId, courseId);
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/update")
    public ResponseEntity<StudentProgress> updateProgress(@RequestBody StudentProgressUpdateDto updateDto) {
        StudentProgress progress = progressService.updateProgress(
            updateDto.getStudentId(),
            updateDto.getCourseId(),
            updateDto.getMaterialId(),
            updateDto.getStatus()
        );
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/record-access")
    public ResponseEntity<StudentProgress> recordAccess(@RequestBody StudentAccessDto accessDto) {
        StudentProgress progress = progressService.recordAccess(
            accessDto.getStudentId(),
            accessDto.getCourseId(),
            accessDto.getMaterialId()
        );
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/student/{studentId}/courses")
    public ResponseEntity<List<CourseProgressSummary>> getStudentCourseProgress(@PathVariable Long studentId) {
        List<CourseProgressSummary> summaries = progressService.getStudentCourseProgress(studentId);
        return ResponseEntity.ok(summaries);
    }
}
