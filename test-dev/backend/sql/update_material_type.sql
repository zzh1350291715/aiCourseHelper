-- 更新 course_materials 表的 material_type 字段
-- 将 ENUM 从 ('VIDEO', 'DOCUMENT', 'QUIZ') 更新为 ('VIDEO', 'DOCUMENT', 'PDF', 'IMAGE', 'QUIZ')

-- 方法1: 修改表结构（推荐）
ALTER TABLE course_materials MODIFY COLUMN material_type ENUM('VIDEO', 'DOCUMENT', 'PDF', 'IMAGE', 'QUIZ') NOT NULL;

-- 如果上面的方法失败，可以使用以下方法：
-- 1. 创建临时表
-- CREATE TABLE course_materials_temp LIKE course_materials;
-- ALTER TABLE course_materials_temp MODIFY COLUMN material_type ENUM('VIDEO', 'DOCUMENT', 'PDF', 'IMAGE', 'QUIZ') NOT NULL;

-- 2. 复制数据
-- INSERT INTO course_materials_temp SELECT * FROM course_materials;

-- 3. 删除原表并重命名
-- DROP TABLE course_materials;
-- ALTER TABLE course_materials_temp RENAME TO course_materials;

-- 4. 重新添加外键约束
-- ALTER TABLE course_materials ADD CONSTRAINT fk_materials_course FOREIGN KEY (course_id) REFERENCES courses(id);

-- 更新student_progress表的状态枚举，添加ACCESSED状态
ALTER TABLE student_progress MODIFY COLUMN status ENUM('NOT_STARTED', 'ACCESSED', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'NOT_STARTED';

-- 更新课程材料类型枚举
-- 添加 AUDIO 和 OTHER 类型支持

-- 如果是MySQL，更新枚举列定义
ALTER TABLE course_materials 
MODIFY COLUMN material_type ENUM('VIDEO', 'DOCUMENT', 'PDF', 'IMAGE', 'AUDIO', 'OTHER', 'QUIZ') NOT NULL;

-- 如果需要，更新现有的空值或无效值
UPDATE course_materials 
SET material_type = 'OTHER' 
WHERE material_type NOT IN ('VIDEO', 'DOCUMENT', 'PDF', 'IMAGE', 'AUDIO', 'OTHER', 'QUIZ');

-- 添加知识库表的描述列（如果不存在）
ALTER TABLE knowledge_bases 
ADD COLUMN IF NOT EXISTS description TEXT;

-- 设置知识库的course_id为可选
ALTER TABLE knowledge_bases 
MODIFY COLUMN course_id BIGINT NULL;

-- 提交更改
COMMIT; 