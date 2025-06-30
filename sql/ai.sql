/*
 Navicat Premium Dump SQL

 Source Server         : localhost3306
 Source Server Type    : MySQL
 Source Server Version : 80030 (8.0.30)
 Source Host           : localhost:3306
 Source Schema         : ai

 Target Server Type    : MySQL
 Target Server Version : 80030 (8.0.30)
 File Encoding         : 65001

 Date: 29/06/2025 16:32:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_chat_history
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_history`;
CREATE TABLE `ai_chat_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  `question` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `answer` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_chat_student`(`student_id` ASC) USING BTREE,
  INDEX `fk_chat_course`(`course_id` ASC) USING BTREE,
  CONSTRAINT `fk_chat_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_chat_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_chat_history
-- ----------------------------

-- ----------------------------
-- Table structure for auto_grading_results
-- ----------------------------
DROP TABLE IF EXISTS `auto_grading_results`;
CREATE TABLE `auto_grading_results`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ai_analysis` json NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `detailed_scores` json NULL,
  `feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `max_score` decimal(5, 2) NULL DEFAULT NULL,
  `method` enum('AUTO','MANUAL','HYBRID') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `processing_time_seconds` int NULL DEFAULT NULL,
  `score_percentage` decimal(5, 2) NULL DEFAULT NULL,
  `status` enum('PENDING','PROCESSING','COMPLETED','FAILED','MANUAL_REVIEW_REQUIRED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `student_answer_file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `total_score` decimal(5, 2) NULL DEFAULT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `quiz_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK7rss90cxdggw16f5hekhsedsx`(`quiz_id` ASC) USING BTREE,
  INDEX `FKib897igc7pnrgehuq8765jeb5`(`student_id` ASC) USING BTREE,
  CONSTRAINT `FK7rss90cxdggw16f5hekhsedsx` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKib897igc7pnrgehuq8765jeb5` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of auto_grading_results
-- ----------------------------

-- ----------------------------
-- Table structure for course_materials
-- ----------------------------
DROP TABLE IF EXISTS `course_materials`;
CREATE TABLE `course_materials`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `material_type` enum('VIDEO','DOCUMENT','PDF','IMAGE','QUIZ') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `order` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_materials_course`(`course_id` ASC) USING BTREE,
  CONSTRAINT `fk_materials_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_materials
-- ----------------------------

-- ----------------------------
-- Table structure for course_outlines
-- ----------------------------
DROP TABLE IF EXISTS `course_outlines`;
CREATE TABLE `course_outlines`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `chapters` json NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `estimated_hours` int NULL DEFAULT NULL,
  `learning_objectives` json NULL,
  `status` enum('DRAFT','GENERATED','REVIEWED','APPROVED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `time_allocation` json NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `course_id` bigint NOT NULL,
  `knowledge_base_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKpexi3f50oo0sfijhqynxe5url`(`course_id` ASC) USING BTREE,
  INDEX `FK55i1uhw3bvrhkcwq9qn910uov`(`knowledge_base_id` ASC) USING BTREE,
  CONSTRAINT `FK55i1uhw3bvrhkcwq9qn910uov` FOREIGN KEY (`knowledge_base_id`) REFERENCES `knowledge_bases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKpexi3f50oo0sfijhqynxe5url` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_outlines
-- ----------------------------

-- ----------------------------
-- Table structure for courses
-- ----------------------------
DROP TABLE IF EXISTS `courses`;
CREATE TABLE `courses`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `instructor_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_courses_instructor`(`instructor_id` ASC) USING BTREE,
  CONSTRAINT `fk_courses_instructor` FOREIGN KEY (`instructor_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of courses
-- ----------------------------
INSERT INTO `courses` VALUES (1, '软件工程', '这是描述', 1, '2025-06-29 14:43:56', '2025-06-29 14:43:56');
INSERT INTO `courses` VALUES (2, '云计算技术', '这是描述', 1, '2025-06-29 14:44:56', '2025-06-29 14:44:56');
INSERT INTO `courses` VALUES (3, '高数', '我是描述\n', 6, '2025-06-29 16:02:03', '2025-06-29 16:02:03');

-- ----------------------------
-- Table structure for knowledge_base_documents
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_base_documents`;
CREATE TABLE `knowledge_base_documents`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `knowledge_base_id` bigint NOT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` enum('UPLOADING','PROCESSING','READY','ERROR') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'UPLOADING',
  `uploaded_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_documents_knowledge`(`knowledge_base_id` ASC) USING BTREE,
  CONSTRAINT `fk_documents_knowledge` FOREIGN KEY (`knowledge_base_id`) REFERENCES `knowledge_bases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_base_documents
-- ----------------------------

-- ----------------------------
-- Table structure for knowledge_bases
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_bases`;
CREATE TABLE `knowledge_bases`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_knowledge_course`(`course_id` ASC) USING BTREE,
  CONSTRAINT `fk_knowledge_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_bases
-- ----------------------------
INSERT INTO `knowledge_bases` VALUES (1, 1, 'Java基础知识库', NOW(), NOW(), 'Java基础相关知识点题库');

-- ----------------------------
-- Table structure for ppt_outlines
-- ----------------------------
DROP TABLE IF EXISTS `ppt_outlines`;
CREATE TABLE `ppt_outlines`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `chapter_name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `estimated_duration` int NULL DEFAULT NULL,
  `examples` json NULL,
  `key_points` json NULL,
  `slide_count` int NULL DEFAULT NULL,
  `slides` json NULL,
  `status` enum('DRAFT','GENERATED','REVIEWED','APPROVED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `course_id` bigint NOT NULL,
  `knowledge_base_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKcem8c9nt4xwg6b55emrmwops3`(`course_id` ASC) USING BTREE,
  INDEX `FKd4hb2ubue6uass60krc9kbc5x`(`knowledge_base_id` ASC) USING BTREE,
  CONSTRAINT `FKcem8c9nt4xwg6b55emrmwops3` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKd4hb2ubue6uass60krc9kbc5x` FOREIGN KEY (`knowledge_base_id`) REFERENCES `knowledge_bases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ppt_outlines
-- ----------------------------

-- ----------------------------
-- Table structure for questions
-- ----------------------------
DROP TABLE IF EXISTS `questions`;
CREATE TABLE `questions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quiz_id` bigint NULL DEFAULT NULL,
  `question_text` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `question_type` enum('MULTIPLE_CHOICE','TRUE_FALSE','SHORT_ANSWER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `options` json NULL,
  `correct_answer` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `knowledge_base_id` bigint NULL DEFAULT NULL,
  `difficulty` enum('EASY','MEDIUM','HARD') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'MEDIUM',
  `tags` json NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_questions_quiz`(`quiz_id` ASC) USING BTREE,
  INDEX `fk_questions_knowledge_base`(`knowledge_base_id` ASC) USING BTREE,
  CONSTRAINT `fk_questions_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_questions_knowledge_base` FOREIGN KEY (`knowledge_base_id`) REFERENCES `knowledge_bases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of questions
-- ----------------------------
INSERT INTO `questions` (`id`, `quiz_id`, `question_text`, `question_type`, `options`, `correct_answer`, `knowledge_base_id`, `difficulty`, `tags`) VALUES
(1, NULL, 'Java中声明整型变量的关键字是？', 'MULTIPLE_CHOICE', '{"A":"int","B":"float","C":"String","D":"char"}', 'A', 1, 'EASY', '["Java","基础"]'),
(2, NULL, '下列哪个是Java的基本数据类型？', 'MULTIPLE_CHOICE', '{"A":"int","B":"Integer","C":"String","D":"List"}', 'A', 1, 'EASY', '["Java","基础"]'),
(3, NULL, 'Java中用于输出信息到控制台的方法是？', 'MULTIPLE_CHOICE', '{"A":"System.out.println()","B":"console.log()","C":"print()","D":"echo()"}', 'A', 1, 'EASY', '["Java","基础"]'),
(4, NULL, 'Java中用于继承的关键字是？', 'MULTIPLE_CHOICE', '{"A":"extends","B":"implements","C":"inherit","D":"super"}', 'A', 1, 'EASY', '["Java","基础"]'),
(5, NULL, 'Java中数组的下标起始值是？', 'MULTIPLE_CHOICE', '{"A":"0","B":"1","C":"-1","D":"随意"}', 'A', 1, 'EASY', '["Java","基础"]'),
(6, NULL, 'Java中String属于基本数据类型。', 'TRUE_FALSE', NULL, 'false', 1, 'EASY', '["Java","基础"]'),
(7, NULL, 'Java中类名建议以大写字母开头。', 'TRUE_FALSE', NULL, 'true', 1, 'EASY', '["Java","基础"]'),
(8, NULL, 'Java中main方法的正确写法是？', 'MULTIPLE_CHOICE', '{"A":"public static void main(String[] args)","B":"void main(String[] args)","C":"static void main(String args[])","D":"public void main()"}', 'A', 1, 'MEDIUM', '["Java","基础"]'),
(9, NULL, 'Java中用于实现多态的机制是？', 'MULTIPLE_CHOICE', '{"A":"继承","B":"重载和重写","C":"接口","D":"以上都是"}', 'D', 1, 'MEDIUM', '["Java","基础"]'),
(10, NULL, 'Java中final修饰的变量可以被修改。', 'TRUE_FALSE', NULL, 'false', 1, 'MEDIUM', '["Java","基础"]'),
(11, NULL, 'Java中==和equals的区别是什么？', 'SHORT_ANSWER', NULL, '==比较引用，equals比较内容', 1, 'MEDIUM', '["Java","基础"]'),
(12, NULL, 'Java中ArrayList和LinkedList的区别？', 'SHORT_ANSWER', NULL, 'ArrayList底层是数组，LinkedList底层是链表', 1, 'MEDIUM', '["Java","基础"]'),
(13, NULL, 'Java中如何定义一个常量？', 'MULTIPLE_CHOICE', '{"A":"final int a = 10;","B":"const int a = 10;","C":"static int a = 10;","D":"int a = 10;"}', 'A', 1, 'EASY', '["Java","基础"]'),
(14, NULL, 'Java中包的关键字是？', 'MULTIPLE_CHOICE', '{"A":"package","B":"import","C":"namespace","D":"module"}', 'A', 1, 'EASY', '["Java","基础"]'),
(15, NULL, 'Java中switch语句可以作用于哪些类型？', 'MULTIPLE_CHOICE', '{"A":"int,char,String,enum","B":"float,double","C":"boolean","D":"List"}', 'A', 1, 'MEDIUM', '["Java","基础"]'),
(16, NULL, 'Java中构造方法的名称必须与类名相同。', 'TRUE_FALSE', NULL, 'true', 1, 'EASY', '["Java","基础"]'),
(17, NULL, 'Java中接口使用哪个关键字声明？', 'MULTIPLE_CHOICE', '{"A":"interface","B":"implements","C":"abstract","D":"extends"}', 'A', 1, 'EASY', '["Java","基础"]'),
(18, NULL, 'Java中可以多继承类。', 'TRUE_FALSE', NULL, 'false', 1, 'EASY', '["Java","基础"]'),
(19, NULL, 'Java中方法重载是指方法名相同但参数不同。', 'TRUE_FALSE', NULL, 'true', 1, 'EASY', '["Java","基础"]'),
(20, NULL, '简述Java中异常处理的关键字。', 'SHORT_ANSWER', NULL, 'try, catch, finally, throw, throws', 1, 'MEDIUM', '["Java","基础"]');

-- ----------------------------
-- Table structure for quizzes
-- ----------------------------
DROP TABLE IF EXISTS `quizzes`;
CREATE TABLE `quizzes`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `material_id` bigint NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKqop3m0r35qj6uhfongsfyghxm`(`material_id` ASC) USING BTREE,
  CONSTRAINT `fk_quizzes_material` FOREIGN KEY (`material_id`) REFERENCES `course_materials` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of quizzes
-- ----------------------------

-- ----------------------------
-- Table structure for student_answers
-- ----------------------------
DROP TABLE IF EXISTS `student_answers`;
CREATE TABLE `student_answers`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quiz_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `answer_text` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `is_correct` tinyint(1) NULL DEFAULT NULL,
  `score` decimal(38, 2) NULL DEFAULT NULL,
  `submitted_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_answers_quiz`(`quiz_id` ASC) USING BTREE,
  INDEX `fk_answers_student`(`student_id` ASC) USING BTREE,
  INDEX `fk_answers_question`(`question_id` ASC) USING BTREE,
  CONSTRAINT `fk_answers_question` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_answers_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_answers_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_answers
-- ----------------------------

-- ----------------------------
-- Table structure for student_progress
-- ----------------------------
DROP TABLE IF EXISTS `student_progress`;
CREATE TABLE `student_progress`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  `material_id` bigint NOT NULL,
  `status` enum('NOT_STARTED','ACCESSED','IN_PROGRESS','COMPLETED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'NOT_STARTED',
  `last_accessed_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_progress_course`(`course_id` ASC) USING BTREE,
  INDEX `fk_progress_material`(`material_id` ASC) USING BTREE,
  UNIQUE INDEX `UK9gw3y2k6hyvj9vhow5fqa6tx`(`student_id` ASC, `material_id` ASC) USING BTREE,
  CONSTRAINT `fk_progress_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_progress_material` FOREIGN KEY (`material_id`) REFERENCES `course_materials` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_progress_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_progress
-- ----------------------------

-- ----------------------------
-- Table structure for student_quiz_report
-- ----------------------------
DROP TABLE IF EXISTS `student_quiz_report`;
CREATE TABLE `student_quiz_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `quiz_id` bigint NOT NULL,
  `status` enum('NOT_STARTED','SUBMITTED','COMPLETED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'NOT_STARTED',
  `submitted_at` datetime(6) NULL DEFAULT NULL,
  `score` decimal(5, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_student_quiz`(`student_id` ASC, `quiz_id` ASC) USING BTREE,
  INDEX `fk_report_quiz`(`quiz_id` ASC) USING BTREE,
  CONSTRAINT `fk_report_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_report_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_quiz_report
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` enum('STUDENT','TEACHER','ADMIN') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'student1', 'password123', 'STUDENT', '2025-06-29 13:59:31', '2025-06-29 13:59:31');
INSERT INTO `users` VALUES (4, '1', '1', 'STUDENT', '2025-06-29 14:06:17', '2025-06-29 14:06:17');
INSERT INTO `users` VALUES (5, '2', '2', 'STUDENT', '2025-06-29 15:28:39', '2025-06-29 15:28:39');
INSERT INTO `users` VALUES (6, '3', '3', 'TEACHER', '2025-06-29 15:36:29', '2025-06-29 15:36:29');

SET FOREIGN_KEY_CHECKS = 1;
