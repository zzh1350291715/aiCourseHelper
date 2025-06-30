-- online-study/backend/sql/online_study.sql

-- =============================================
-- 设置字符集 - 解决中文乱码问题
-- =============================================
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection = utf8mb4;

-- =============================================
-- 数据库初始化脚本 - 在线学习平台
-- =============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS online_study 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE online_study;

-- 删除现有表（如果存在）- 仅用于重新部署
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS ai_chat_history;
DROP TABLE IF EXISTS student_answers;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS quizzes;
DROP TABLE IF EXISTS knowledge_base_documents;
DROP TABLE IF EXISTS knowledge_bases;
DROP TABLE IF EXISTS student_progress;
DROP TABLE IF EXISTS course_materials;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 创建表结构
-- =============================================

-- Users Table: Stores both students and instructors
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Hashed password
    email VARCHAR(255) NOT NULL UNIQUE,
    role ENUM('STUDENT', 'INSTRUCTOR') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- Courses Table
CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    instructor_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_instructor (instructor_id),
    INDEX idx_title (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- Course Materials Table
CREATE TABLE course_materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    material_type ENUM('VIDEO', 'DOCUMENT', 'QUIZ', 'PDF', 'PPT') NOT NULL,
    content_url VARCHAR(500),
    `order` INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_course (course_id),
    INDEX idx_type (material_type),
    INDEX idx_order (course_id, `order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程材料表';

-- Student Progress Table
CREATE TABLE student_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    status ENUM('NOT_STARTED', 'ACCESSED', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'NOT_STARTED',
    last_accessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_student_material (student_id, material_id),
    INDEX idx_student_course (student_id, course_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生学习进度表';

-- Knowledge Bases Table (for RAG)
CREATE TABLE knowledge_bases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库表';

-- Knowledge Base Documents Table
CREATE TABLE knowledge_base_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    knowledge_base_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    status ENUM('UPLOADING', 'PROCESSING', 'READY', 'ERROR') DEFAULT 'UPLOADING',
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_knowledge_base (knowledge_base_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库文档表';

-- Quizzes Table
CREATE TABLE quizzes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_material (material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测验表';

-- Questions Table
CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    question_type ENUM('MULTIPLE_CHOICE', 'TRUE_FALSE', 'SHORT_ANSWER') NOT NULL,
    options JSON, -- For multiple choice options
    correct_answer TEXT,
    
    INDEX idx_quiz (quiz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问题表';

-- Student Answers Table
CREATE TABLE student_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    answer_text TEXT,
    is_correct BOOLEAN,
    score DECIMAL(5, 2),
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_quiz_student (quiz_id, student_id),
    INDEX idx_question (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生答案表';

-- AI Chat History Table
CREATE TABLE ai_chat_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_student_course (student_id, course_id),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI聊天记录表';

-- =============================================
-- 添加外键约束
-- =============================================

-- Add Foreign Key Constraints
ALTER TABLE courses ADD CONSTRAINT fk_courses_instructor FOREIGN KEY (instructor_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE course_materials ADD CONSTRAINT fk_materials_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;
ALTER TABLE student_progress ADD CONSTRAINT fk_progress_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE student_progress ADD CONSTRAINT fk_progress_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;
ALTER TABLE student_progress ADD CONSTRAINT fk_progress_material FOREIGN KEY (material_id) REFERENCES course_materials(id) ON DELETE CASCADE;
ALTER TABLE knowledge_bases ADD CONSTRAINT fk_knowledge_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;
ALTER TABLE knowledge_base_documents ADD CONSTRAINT fk_documents_knowledge FOREIGN KEY (knowledge_base_id) REFERENCES knowledge_bases(id) ON DELETE CASCADE;
ALTER TABLE quizzes ADD CONSTRAINT fk_quizzes_material FOREIGN KEY (material_id) REFERENCES course_materials(id) ON DELETE CASCADE;
ALTER TABLE questions ADD CONSTRAINT fk_questions_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE;
ALTER TABLE student_answers ADD CONSTRAINT fk_answers_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE;
ALTER TABLE student_answers ADD CONSTRAINT fk_answers_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE student_answers ADD CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE;
ALTER TABLE ai_chat_history ADD CONSTRAINT fk_chat_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE ai_chat_history ADD CONSTRAINT fk_chat_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;

-- =============================================
-- 插入测试数据
-- =============================================

-- Insert test data
INSERT INTO users (username, password, email, role) VALUES 
('student1', 'password123', 'student1@example.com', 'STUDENT'),
('student2', 'password123', 'student2@example.com', 'STUDENT'),
('instructor1', 'password123', 'instructor1@example.com', 'INSTRUCTOR'),
('instructor2', 'password123', 'instructor2@example.com', 'INSTRUCTOR'),
('admin', 'admin123', 'admin@example.com', 'INSTRUCTOR');

-- 插入测试课程
INSERT INTO courses (title, description, instructor_id) VALUES 
('JavaScript 入门教程', '从零开始学习JavaScript编程语言，掌握前端开发基础', 3),
('Vue.js 实战开发', '使用Vue.js框架构建现代化的Web应用程序', 3),
('Python 数据分析', '使用Python进行数据分析和可视化', 4),
('Spring Boot 后端开发', '学习Spring Boot框架，构建企业级后端应用', 4);

-- 插入测试课程材料
INSERT INTO course_materials (course_id, title, material_type, content_url, `order`) VALUES 
(1, 'JavaScript 基础语法', 'DOCUMENT', '/materials/js-basics.pdf', 1),
(1, '变量和数据类型', 'VIDEO', '/materials/js-variables.mp4', 2),
(1, '函数和作用域', 'DOCUMENT', '/materials/js-functions.pdf', 3),
(1, '基础知识测试', 'QUIZ', '', 4),
(2, 'Vue.js 介绍', 'DOCUMENT', '/materials/vue-intro.pdf', 1),
(2, '组件化开发', 'VIDEO', '/materials/vue-components.mp4', 2),
(2, '路由管理', 'DOCUMENT', '/materials/vue-router.pdf', 3);

-- 显示创建结果
SELECT 'Database setup completed successfully!' as message;
SHOW TABLES;