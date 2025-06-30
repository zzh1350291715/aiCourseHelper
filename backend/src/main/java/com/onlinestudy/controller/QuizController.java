package com.onlinestudy.controller;

import com.onlinestudy.domain.Quiz;
import com.onlinestudy.domain.Question;
import com.onlinestudy.repository.QuizRepository;
import com.onlinestudy.repository.QuestionRepository;
import com.onlinestudy.repository.StudentAnswerRepository;
import com.onlinestudy.domain.StudentQuizReport;
import com.onlinestudy.repository.StudentQuizReportRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import com.onlinestudy.domain.StudentAnswer;
import com.onlinestudy.domain.CourseMaterial;
import com.onlinestudy.repository.CourseMaterialRepository;
import com.onlinestudy.domain.Course;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final StudentQuizReportRepository studentQuizReportRepository;
    private final CourseMaterialRepository courseMaterialRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public QuizController(QuizRepository quizRepository, QuestionRepository questionRepository, StudentAnswerRepository studentAnswerRepository, StudentQuizReportRepository studentQuizReportRepository, CourseMaterialRepository courseMaterialRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.studentQuizReportRepository = studentQuizReportRepository;
        this.courseMaterialRepository = courseMaterialRepository;
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<Map<String, Object>> getQuiz(@PathVariable Long quizId) {
        Optional<Quiz> quizOpt = quizRepository.findById(quizId);
        if (quizOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Quiz quiz = quizOpt.get();
        Map<String, Object> result = new HashMap<>();
        result.put("id", quiz.getId());
        result.put("title", quiz.getTitle());
        if (quiz.getCourseMaterial() != null) {
            Map<String, Object> cm = new HashMap<>();
            cm.put("id", quiz.getCourseMaterial().getId());
            result.put("courseMaterial", cm);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{quizId}/questions")
    public List<Question> getQuizQuestions(@PathVariable Long quizId) {
        List<Question> questions = questionRepository.findByQuizId(quizId);
        for (Question q : questions) {
            q.setCorrectAnswer(null);
            // 兜底：如果是选择题且options为null或不含options键，补空数组
            if (q.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE) {
                Map<String, Object> opts = q.getOptions();
                if (opts == null) {
                    opts = new HashMap<>();
                }
                Object optionsObj = opts.get("options");
                if (optionsObj instanceof List) {
                    // 正常格式，无需处理
                } else if (opts.size() > 0) {
                    // 兼容旧格式：{A:..., B:..., ...}，转为数组
                    List<String> arr = new ArrayList<>();
                    for (Object v : opts.values()) {
                        arr.add(v == null ? "" : v.toString());
                    }
                    opts = new HashMap<>();
                    opts.put("options", arr);
                    q.setOptions(opts);
                } else {
                    // 空对象或无options，补空数组
                    opts.put("options", new ArrayList<>());
                    q.setOptions(opts);
                }
            }
        }
        return questions;
    }

    @GetMapping
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    @Transactional
    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        if (quizRepository.existsById(quizId)) {
            // 先删student_quiz_report
            entityManager.createNativeQuery("DELETE FROM student_quiz_report WHERE quiz_id = :quizId")
                .setParameter("quizId", quizId)
                .executeUpdate();
            // 再删student_answers
            studentAnswerRepository.deleteByQuizId(quizId);
            // 最后删quiz
            quizRepository.deleteById(quizId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/material/{materialId}")
    public ResponseEntity<Quiz> getQuizByMaterialId(@PathVariable Long materialId) {
        Optional<Quiz> quiz = quizRepository.findByCourseMaterialId(materialId);
        return quiz.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/student-quiz-report/status")
    public ResponseEntity<?> getQuizStatus(@RequestParam Long studentId, @RequestParam Long quizId) {
        Optional<StudentQuizReport> report = studentQuizReportRepository.findByStudentIdAndQuizId(studentId, quizId);
        String status = report.map(StudentQuizReport::getStatus).orElse("NOT_STARTED");
        return ResponseEntity.ok(Map.of("status", status));
    }

    @GetMapping("/available")
    public List<Map<String, Object>> getAvailableQuizzes(@RequestParam Long studentId) {
        List<Long> completedQuizIds = studentQuizReportRepository.findByStudentId(studentId)
            .stream().map(r -> r.getQuiz().getId()).toList();
        List<Quiz> quizzes;
        if (completedQuizIds.isEmpty()) {
            quizzes = quizRepository.findAll();
        } else {
            quizzes = quizRepository.findAll().stream()
                .filter(q -> !completedQuizIds.contains(q.getId()))
                .toList();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Quiz quiz : quizzes) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", quiz.getId());
            map.put("title", quiz.getTitle());
            map.put("createdAt", quiz.getCreatedAt());
            if (quiz.getCourseMaterial() != null && quiz.getCourseMaterial().getCourse() != null) {
                Map<String, Object> courseMaterial = new HashMap<>();
                Map<String, Object> course = new HashMap<>();
                course.put("id", quiz.getCourseMaterial().getCourse().getId());
                course.put("title", quiz.getCourseMaterial().getCourse().getTitle());
                courseMaterial.put("course", course);
                map.put("courseMaterial", courseMaterial);
            }
            result.add(map);
        }
        return result;
    }

    @GetMapping("/completed")
    public List<Map<String, Object>> getCompletedQuizzes(@RequestParam Long studentId) {
        List<Long> completedQuizIds = studentQuizReportRepository.findByStudentId(studentId)
            .stream().map(r -> r.getQuiz().getId()).toList();
        if (completedQuizIds.isEmpty()) return List.of();
        List<Quiz> quizzes = quizRepository.findAll().stream()
            .filter(q -> completedQuizIds.contains(q.getId()))
            .toList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Quiz quiz : quizzes) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", quiz.getId());
            map.put("title", quiz.getTitle());
            map.put("createdAt", quiz.getCreatedAt());
            if (quiz.getCourseMaterial() != null && quiz.getCourseMaterial().getCourse() != null) {
                Map<String, Object> courseMaterial = new HashMap<>();
                Map<String, Object> course = new HashMap<>();
                course.put("id", quiz.getCourseMaterial().getCourse().getId());
                course.put("title", quiz.getCourseMaterial().getCourse().getTitle());
                courseMaterial.put("course", course);
                map.put("courseMaterial", courseMaterial);
            }
            result.add(map);
        }
        return result;
    }

    @GetMapping("/instructor/list")
    public List<Map<String, Object>> getQuizListForInstructor() {
        List<Quiz> quizzes = quizRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Quiz quiz : quizzes) {
            long count = studentQuizReportRepository.countByQuizId(quiz.getId());
            boolean hasStudent = count > 0;
            Map<String, Object> map = new HashMap<>();
            map.put("id", quiz.getId());
            map.put("title", quiz.getTitle());
            map.put("courseTitle", quiz.getCourseMaterial().getCourse().getTitle());
            map.put("hasStudent", hasStudent);
            map.put("studentCount", count);
            map.put("createdAt", quiz.getCreatedAt());
            result.add(map);
        }
        return result;
    }

    @GetMapping("/instructor/{quizId}/detail")
    public Map<String, Object> getQuizDetailForInstructor(@PathVariable Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();
        List<StudentQuizReport> reports = studentQuizReportRepository.findByQuizId(quizId);
        List<Map<String, Object>> students = new ArrayList<>();
        for (StudentQuizReport report : reports) {
            Map<String, Object> stu = new HashMap<>();
            stu.put("studentId", report.getStudent().getId());
            stu.put("username", report.getStudent().getUsername());
            students.add(stu);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("id", quiz.getId());
        result.put("title", quiz.getTitle());
        result.put("courseTitle", quiz.getCourseMaterial().getCourse().getTitle());
        result.put("createdAt", quiz.getCreatedAt());
        result.put("students", students);
        return result;
    }

    @GetMapping("/instructor/{quizId}/student/{studentId}/detail")
    public List<Map<String, Object>> getStudentQuizDetail(@PathVariable Long quizId, @PathVariable Long studentId) {
        List<StudentAnswer> answers = studentAnswerRepository.findByStudentIdAndQuizId(studentId, quizId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (StudentAnswer answer : answers) {
            Map<String, Object> map = new HashMap<>();
            Question question = answer.getQuestion();
            map.put("questionText", question != null ? question.getQuestionText() : "");
            map.put("studentAnswer", answer.getAnswerText());
            map.put("correctAnswer", question != null ? question.getCorrectAnswer() : "");
            boolean isCorrect = false;
            if (question != null) {
                switch (question.getQuestionType().toString()) {
                    case "MULTIPLE_CHOICE":
                    case "SINGLE_CHOICE": {
                        String studentLabel = getOptionLabelByContent(question, answer.getAnswerText());
                        isCorrect = normalize(studentLabel).equals(normalize(question.getCorrectAnswer()));
                        break;
                    }
                    case "TRUE_FALSE":
                        isCorrect = toBool(answer.getAnswerText()) == toBool(question.getCorrectAnswer());
                        break;
                    default:
                        isCorrect = answer.getAnswerText().equals(question.getCorrectAnswer());
                }
            }
            map.put("isCorrect", isCorrect);
            map.put("score", answer.getScore());
            map.put("studentId", answer.getStudent() != null ? answer.getStudent().getId() : null);
            map.put("username", answer.getStudent() != null ? answer.getStudent().getUsername() : "");
            result.add(map);
        }
        return result;
    }

    // 工具方法：忽略大小写和空格
    private String normalize(String s) {
        return s == null ? "" : s.trim().replaceAll("　", "").replaceAll("\\s", "").toLowerCase();
    }
    // 新增：根据内容获取选项序号（A/B/C...）
    private String getOptionLabelByContent(Question question, String content) {
        if (question.getOptions() == null) return "";
        Object optsObj = question.getOptions().get("options");
        if (!(optsObj instanceof java.util.List)) return "";
        java.util.List<?> opts = (java.util.List<?>) optsObj;
        for (int i = 0; i < opts.size(); i++) {
            Object opt = opts.get(i);
            if (opt != null && normalize(opt.toString()).equals(normalize(content))) {
                return String.valueOf((char)('A' + i));
            }
        }
        return "";
    }
    // 工具方法：判断题字符串转布尔
    private boolean toBool(String s) {
        if (s == null) return false;
        s = s.trim().toLowerCase();
        return s.equals("true") || s.equals("1") || s.equals("正确") || s.equals("yes");
    }

    // 自动生成测验
    @PostMapping("/generate")
    @Transactional
    public ResponseEntity<Quiz> generateQuiz(@RequestBody Map<String, Object> request) {
        try {
            String title = (String) request.get("title");
            Long courseId = Long.valueOf(request.get("courseId").toString());
            Long knowledgeBaseId = Long.valueOf(request.get("knowledgeBaseId").toString());
            Integer questionCount = Integer.valueOf(request.get("questionCount").toString());
            @SuppressWarnings("unchecked")
            List<String> questionTypes = (List<String>) request.get("questionTypes");

            // 从知识库中获取题目
            List<Question> availableQuestions = new ArrayList<>();
            for (String type : questionTypes) {
                List<Question> questions = questionRepository.findByKnowledgeBaseIdAndQuestionType(
                    knowledgeBaseId, Question.QuestionType.valueOf(type));
                availableQuestions.addAll(questions);
            }

            if (availableQuestions.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // 随机选择指定数量的题目
            List<Question> selectedQuestions = new ArrayList<>();
            if (availableQuestions.size() <= questionCount) {
                selectedQuestions = availableQuestions;
            } else {
                // 随机选择
                java.util.Collections.shuffle(availableQuestions);
                selectedQuestions = availableQuestions.subList(0, questionCount);
            }

            // 创建课程材料（测验类型）
            CourseMaterial material = new CourseMaterial();
            material.setCourse(new Course());
            material.getCourse().setId(courseId);
            material.setTitle(title);
            material.setMaterialType(CourseMaterial.MaterialType.QUIZ);
            material.setOrder(1);
            material = courseMaterialRepository.save(material);

            // 创建测验
            Quiz quiz = new Quiz();
            quiz.setCourseMaterial(material);
            quiz.setTitle(title);
            quiz = quizRepository.save(quiz);

            // 将选中的题目关联到测验
            for (Question question : selectedQuestions) {
                question.setQuiz(quiz);
                questionRepository.save(question);
            }

            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
} 