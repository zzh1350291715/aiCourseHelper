package com.onlinestudy.controller;

import com.onlinestudy.domain.StudentAnswer;
import com.onlinestudy.domain.StudentQuizReport;
import com.onlinestudy.domain.Question;
import com.onlinestudy.repository.StudentAnswerRepository;
import com.onlinestudy.repository.StudentQuizReportRepository;
import com.onlinestudy.repository.QuestionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/student-answers")
public class StudentAnswerController {
    private final StudentAnswerRepository studentAnswerRepository;
    private final StudentQuizReportRepository studentQuizReportRepository;
    private final QuestionRepository questionRepository;

    public StudentAnswerController(StudentAnswerRepository studentAnswerRepository, 
                                 StudentQuizReportRepository studentQuizReportRepository,
                                 QuestionRepository questionRepository) {
        this.studentAnswerRepository = studentAnswerRepository;
        this.studentQuizReportRepository = studentQuizReportRepository;
        this.questionRepository = questionRepository;
    }

    // 学生提交答题
    @PostMapping
    public StudentAnswer submitAnswer(@RequestBody StudentAnswer answer) {
        StudentAnswer saved = studentAnswerRepository.save(answer);
        // 更新student_quiz_report为SUBMITTED
        if (answer.getQuiz() != null && answer.getStudent() != null) {
            // 再查一次，防止并发重复插入
            StudentQuizReport report = studentQuizReportRepository.findByStudentIdAndQuizId(answer.getStudent().getId(), answer.getQuiz().getId())
                .orElseGet(() -> {
                    StudentQuizReport newReport = new StudentQuizReport();
                    newReport.setStudent(answer.getStudent());
                    newReport.setQuiz(answer.getQuiz());
                    return newReport;
                });
            report.setStatus("SUBMITTED");
            report.setSubmittedAt(LocalDateTime.now());
            studentQuizReportRepository.save(report);
        }
        return saved;
    }

    // 批量提交答题并自动评分
    @PostMapping("/batch-submit")
    @Transactional
    public Map<String, Object> batchSubmitAnswers(@RequestBody Map<String, Object> request) {
        Long studentId = Long.valueOf(request.get("studentId").toString());
        Long quizId = Long.valueOf(request.get("quizId").toString());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> answers = (List<Map<String, Object>>) request.get("answers");
        
        // 先删除该学生本次quiz的所有旧答题，避免重做时重复
        List<StudentAnswer> oldAnswers = studentAnswerRepository.findByStudentIdAndQuizId(studentId, quizId);
        if (oldAnswers != null && !oldAnswers.isEmpty()) {
            studentAnswerRepository.deleteAll(oldAnswers);
        }
        
        // 保存所有答题
        for (Map<String, Object> answerData : answers) {
            StudentAnswer answer = new StudentAnswer();
            answer.setStudent(new com.onlinestudy.domain.User());
            answer.getStudent().setId(studentId);
            answer.setQuiz(new com.onlinestudy.domain.Quiz());
            answer.getQuiz().setId(quizId);
            answer.setQuestion(new Question());
            answer.getQuestion().setId(Long.valueOf(answerData.get("questionId").toString()));
            answer.setAnswerText(answerData.get("answer").toString());
            studentAnswerRepository.save(answer);
        }
        
        // 自动评分
        double totalScore = 0.0;
        int totalQuestions = 0;
        List<Map<String, Object>> scoreDetails = new java.util.ArrayList<>();
        
        for (Map<String, Object> answerData : answers) {
            Long questionId = Long.valueOf(answerData.get("questionId").toString());
            String studentAnswer = answerData.get("answer").toString();
            
            Question question = questionRepository.findById(questionId).orElse(null);
            if (question != null) {
                totalQuestions++;
                double questionScore = 0.0;
                boolean isCorrect = false;
                String studentLabel = null;
                // 根据题目类型评分
                switch (question.getQuestionType().toString()) {
                    case "MULTIPLE_CHOICE":
                    case "SINGLE_CHOICE": {
                        studentLabel = getOptionLabelByContent(question, studentAnswer);
                        isCorrect = normalize(studentLabel).equals(normalize(question.getCorrectAnswer()));
                        questionScore = isCorrect ? 10.0 : 0.0;
                        break;
                    }
                    case "TRUE_FALSE":
                        isCorrect = toBool(studentAnswer) == toBool(question.getCorrectAnswer());
                        questionScore = isCorrect ? 10.0 : 0.0;
                        break;
                    case "SHORT_ANSWER":
                        questionScore = 5.0;
                        break;
                }
                // 日志输出
                System.out.println("题目ID: " + questionId);
                System.out.println("学生答案内容: " + studentAnswer);
                System.out.println("选项: " + question.getOptions());
                System.out.println("学生答案序号: " + studentLabel);
                System.out.println("正确答案: " + question.getCorrectAnswer());
                System.out.println("判题结果: " + isCorrect);
                totalScore += questionScore;
                Map<String, Object> detail = new HashMap<>();
                detail.put("questionId", questionId);
                detail.put("questionType", question.getQuestionType().toString());
                detail.put("studentAnswer", studentAnswer);
                detail.put("correctAnswer", question.getCorrectAnswer());
                detail.put("options", question.getOptions());
                detail.put("isCorrect", isCorrect);
                detail.put("score", questionScore);
                scoreDetails.add(detail);
            }
        }
        
        double averageScore = totalQuestions > 0 ? totalScore / totalQuestions : 0.0;
        
        // 更新或创建学生测验报告
        StudentQuizReport report = studentQuizReportRepository.findByStudentIdAndQuizId(studentId, quizId)
            .orElseGet(() -> {
                StudentQuizReport newReport = new StudentQuizReport();
                newReport.setStudent(new com.onlinestudy.domain.User());
                newReport.getStudent().setId(studentId);
                newReport.setQuiz(new com.onlinestudy.domain.Quiz());
                newReport.getQuiz().setId(quizId);
                return newReport;
            });
        
        report.setStatus("COMPLETED");
        report.setScore(averageScore);
        report.setSubmittedAt(LocalDateTime.now());
        studentQuizReportRepository.save(report);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalScore", totalScore);
        result.put("averageScore", averageScore);
        result.put("totalQuestions", totalQuestions);
        result.put("scoreDetails", scoreDetails);
        result.put("status", "COMPLETED");
        
        return result;
    }

    // 查询学生某次测验的所有答题
    @GetMapping("/student/{studentId}/quiz/{quizId}")
    public List<StudentAnswer> getAnswersByStudentAndQuiz(@PathVariable Long studentId, @PathVariable Long quizId) {
        return studentAnswerRepository.findByStudentIdAndQuizId(studentId, quizId);
    }

    // 获取学生测验结果详情
    @GetMapping("/student/{studentId}/quiz/{quizId}/result")
    public Map<String, Object> getStudentQuizResult(@PathVariable Long studentId, @PathVariable Long quizId) {
        List<StudentAnswer> answers = studentAnswerRepository.findByStudentIdAndQuizId(studentId, quizId);
        Optional<StudentQuizReport> report = studentQuizReportRepository.findByStudentIdAndQuizId(studentId, quizId);
        
        List<Map<String, Object>> resultDetails = new java.util.ArrayList<>();
        for (StudentAnswer answer : answers) {
            Question question = questionRepository.findById(answer.getQuestion().getId()).orElse(null);
            if (question != null) {
                Map<String, Object> detail = new HashMap<>();
                detail.put("questionId", question.getId());
                detail.put("questionText", question.getQuestionText());
                detail.put("questionType", question.getQuestionType().toString());
                detail.put("studentAnswer", answer.getAnswerText());
                detail.put("correctAnswer", question.getCorrectAnswer());
                detail.put("options", question.getOptions());
                boolean isCorrect = false;
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
                detail.put("isCorrect", isCorrect);
                resultDetails.add(detail);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("answers", resultDetails);
        result.put("report", report.orElse(null));
        
        return result;
    }

    // 工具方法：忽略大小写和空格
    private String normalize(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("　", "").replaceAll("\\s", "").toLowerCase();
    }
    // 新增：根据内容获取选项序号（A/B/C...）
    private String getOptionLabelByContent(Question question, String content) {
        if (question.getOptions() == null) return "";
        Object optsObj = question.getOptions().get("options");
        if (optsObj instanceof java.util.List) {
            java.util.List<?> opts = (java.util.List<?>) optsObj;
            for (int i = 0; i < opts.size(); i++) {
                Object opt = opts.get(i);
                if (opt != null && normalize(opt.toString()).equals(normalize(content))) {
                    return String.valueOf((char)('A' + i));
                }
            }
        } else {
            // 兼容对象格式 {A:..., B:...}
            for (Map.Entry<String, Object> entry : question.getOptions().entrySet()) {
                if (entry.getValue() != null && normalize(entry.getValue().toString()).equals(normalize(content))) {
                    return entry.getKey();
                }
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
} 