-- AI智能生成功能数据库表结构

-- 使用online_study数据库
USE online_study;

-- 课程大纲表
CREATE TABLE IF NOT EXISTS course_outlines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    knowledge_base_id BIGINT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    chapters JSON COMMENT '章节信息JSON格式',
    learning_objectives JSON COMMENT '学习目标JSON格式',
    time_allocation JSON COMMENT '时间分配JSON格式',
    status ENUM('DRAFT', 'GENERATED', 'REVIEWED', 'APPROVED') DEFAULT 'DRAFT',
    estimated_hours INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_course (course_id),
    INDEX idx_knowledge_base (knowledge_base_id),
    INDEX idx_status (status),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (knowledge_base_id) REFERENCES knowledge_bases(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程大纲表';

-- PPT大纲表
CREATE TABLE IF NOT EXISTS ppt_outlines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    knowledge_base_id BIGINT NULL,
    title VARCHAR(255) NOT NULL,
    chapter_name TEXT,
    slides JSON COMMENT '幻灯片结构JSON格式',
    key_points JSON COMMENT '关键点JSON格式',
    examples JSON COMMENT '示例JSON格式',
    status ENUM('DRAFT', 'GENERATED', 'REVIEWED', 'APPROVED') DEFAULT 'DRAFT',
    slide_count INT DEFAULT 0,
    estimated_duration INT DEFAULT 0 COMMENT '预计讲授时长（分钟）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_course (course_id),
    INDEX idx_knowledge_base (knowledge_base_id),
    INDEX idx_status (status),
    INDEX idx_chapter (course_id, chapter_name(100)),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (knowledge_base_id) REFERENCES knowledge_bases(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PPT大纲表';

-- 自动评分结果表
CREATE TABLE IF NOT EXISTS auto_grading_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    student_answer_file_path VARCHAR(500),
    total_score DECIMAL(5,2) DEFAULT 0,
    max_score DECIMAL(5,2) DEFAULT 100,
    score_percentage DECIMAL(5,2) DEFAULT 0,
    detailed_scores JSON COMMENT '详细评分JSON格式',
    feedback TEXT COMMENT '评分反馈',
    ai_analysis JSON COMMENT 'AI分析结果JSON格式',
    status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'MANUAL_REVIEW_REQUIRED') DEFAULT 'PENDING',
    method ENUM('AUTO', 'MANUAL', 'HYBRID') DEFAULT 'AUTO',
    processing_time_seconds INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_quiz_student (quiz_id, student_id),
    INDEX idx_quiz (quiz_id),
    INDEX idx_student (student_id),
    INDEX idx_status (status),
    INDEX idx_method (method),
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='自动评分结果表';

-- 修改知识库表，添加描述字段
ALTER TABLE knowledge_bases ADD COLUMN IF NOT EXISTS description TEXT COMMENT '知识库描述';

-- 修复知识库表，允许course_id为空（支持创建独立的知识库）
ALTER TABLE knowledge_bases MODIFY course_id BIGINT NULL COMMENT '关联课程ID，可为空表示独立知识库';

-- 创建AI生成任务记录表（用于跟踪生成任务状态）
CREATE TABLE IF NOT EXISTS ai_generation_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    knowledge_base_id BIGINT NOT NULL,
    task_type ENUM('OUTLINE', 'PPT', 'QUIZ', 'GRADING') NOT NULL,
    task_status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    input_parameters JSON COMMENT '输入参数JSON格式',
    result_data JSON COMMENT '生成结果JSON格式',
    error_message TEXT,
    processing_start_time TIMESTAMP NULL,
    processing_end_time TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_knowledge_base (knowledge_base_id),
    INDEX idx_task_type (task_type),
    INDEX idx_task_status (task_status),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (knowledge_base_id) REFERENCES knowledge_bases(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI生成任务记录表';

-- 创建文档向量索引表（为RAG功能准备）
CREATE TABLE IF NOT EXISTS document_embeddings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    chunk_index INT NOT NULL DEFAULT 0,
    chunk_text TEXT NOT NULL,
    embedding_vector JSON COMMENT '向量嵌入JSON格式',
    metadata JSON COMMENT '元数据JSON格式',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_document (document_id),
    INDEX idx_chunk (document_id, chunk_index),
    FOREIGN KEY (document_id) REFERENCES knowledge_base_documents(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档向量嵌入表';

-- 插入一些示例数据
INSERT IGNORE INTO courses (title, description, instructor_id, created_at) 
VALUES ('人工智能基础', '介绍人工智能的基本概念和应用', 1, NOW());

INSERT IGNORE INTO knowledge_bases (course_id, name, description, created_at)
VALUES (1, '人工智能知识库', '包含人工智能基础理论和实践案例的知识库', NOW()); 