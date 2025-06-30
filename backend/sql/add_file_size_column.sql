-- 为course_materials表添加file_size字段
USE ai;

ALTER TABLE course_materials 
ADD COLUMN file_size BIGINT NULL COMMENT '文件大小（字节）';

-- 更新现有记录的file_size为NULL（表示未知大小）
-- 新上传的文件会自动设置正确的文件大小