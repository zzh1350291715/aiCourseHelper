package com.onlinestudy.service;

import com.onlinestudy.domain.Course;
import com.onlinestudy.domain.CourseMaterial;
import com.onlinestudy.domain.StudentProgress;
import com.onlinestudy.domain.User;
import com.onlinestudy.dto.CourseProgressSummary;
import com.onlinestudy.repository.CourseMaterialRepository;
import com.onlinestudy.repository.CourseRepository;
import com.onlinestudy.repository.StudentProgressRepository;
import com.onlinestudy.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StudentProgressService {

    private final StudentProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseMaterialRepository materialRepository;

    public StudentProgressService(StudentProgressRepository progressRepository,
                                UserRepository userRepository,
                                CourseRepository courseRepository,
                                CourseMaterialRepository materialRepository) {
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.materialRepository = materialRepository;
    }

    public List<StudentProgress> getStudentProgress(Long studentId, Long courseId) {
        return progressRepository.findByStudent_IdAndCourse_Id(studentId, courseId);
    }

    public StudentProgress updateProgress(Long studentId, Long courseId, Long materialId, String status) {
        // 获取学生、课程和材料对象
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        CourseMaterial material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("课程材料不存在"));

        // 查找现有的进度记录
        Optional<StudentProgress> existingProgress = progressRepository
                .findByStudent_IdAndCourse_IdAndMaterial_Id(studentId, courseId, materialId);

        StudentProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
        } else {
            progress = new StudentProgress();
            progress.setStudent(student);
            progress.setCourse(course);
            progress.setMaterial(material);
        }

        // 更新状态
        progress.setStatus(StudentProgress.Status.valueOf(status));
        progress.setLastAccessedAt(Timestamp.valueOf(LocalDateTime.now()));

        return progressRepository.save(progress);
    }

    public StudentProgress recordAccess(Long studentId, Long courseId, Long materialId) {
        // 获取学生、课程和材料对象
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        CourseMaterial material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("课程材料不存在"));

        // 查找现有的进度记录
        Optional<StudentProgress> existingProgress = progressRepository
                .findByStudent_IdAndCourse_IdAndMaterial_Id(studentId, courseId, materialId);

        StudentProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
        } else {
            progress = new StudentProgress();
            progress.setStudent(student);
            progress.setCourse(course);
            progress.setMaterial(material);
            progress.setStatus(StudentProgress.Status.ACCESSED);
        }

        // 更新访问时间
        progress.setLastAccessedAt(Timestamp.valueOf(LocalDateTime.now()));

        return progressRepository.save(progress);
    }

    public List<CourseProgressSummary> getStudentCourseProgress(Long studentId) {
        return progressRepository.findCourseProgressSummaryByStudentId(studentId);
    }
} 