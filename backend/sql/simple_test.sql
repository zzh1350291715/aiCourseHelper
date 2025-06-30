-- 简单的字符集测试脚本
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE DATABASE IF NOT EXISTS test_charset 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE test_charset;

CREATE TABLE test_table (
    id INT PRIMARY KEY,
    chinese_text VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO test_table (id, chinese_text) VALUES 
(1, '测试中文字符'),
(2, '这是中文测试');

SELECT * FROM test_table;

DROP DATABASE test_charset; 