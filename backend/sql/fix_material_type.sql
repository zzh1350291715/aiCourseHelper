-- 修复 course_materials 表的 material_type 字段
-- 删除现有的外键约束
ALTER TABLE student_progress DROP FOREIGN KEY fk_progress_material;
ALTER TABLE quizzes DROP FOREIGN KEY fk_quizzes_material;
ALTER TABLE course_materials DROP FOREIGN KEY fk_materials_course;

-- 删除现有表
DROP TABLE IF EXISTS course_materials;

-- 重新创建表
CREATE TABLE course_materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    material_type ENUM('VIDEO', 'DOCUMENT', 'PDF', 'IMAGE', 'QUIZ') NOT NULL,
    content_url VARCHAR(255),
    `order` INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 重新添加外键约束
ALTER TABLE course_materials ADD CONSTRAINT fk_materials_course FOREIGN KEY (course_id) REFERENCES courses(id);
ALTER TABLE student_progress ADD CONSTRAINT fk_progress_material FOREIGN KEY (material_id) REFERENCES course_materials(id);
ALTER TABLE quizzes ADD CONSTRAINT fk_quizzes_material FOREIGN KEY (material_id) REFERENCES course_materials(id); 