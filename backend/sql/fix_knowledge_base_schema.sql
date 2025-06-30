-- 修复知识库表结构
-- 允许course_id为null，支持创建独立知识库

USE online_study;

-- 修改knowledge_bases表，允许course_id为null
ALTER TABLE knowledge_bases 
MODIFY COLUMN course_id BIGINT NULL COMMENT '关联课程ID，可为空表示独立知识库';

-- 更新外键约束（如果需要的话）
-- ALTER TABLE knowledge_bases 
-- DROP FOREIGN KEY fk_knowledge_course;

-- ALTER TABLE knowledge_bases 
-- ADD CONSTRAINT fk_knowledge_course 
-- FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL;

-- 验证表结构
DESCRIBE knowledge_bases; 