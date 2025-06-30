-- 创建新的MySQL用户脚本
-- 注意：请使用root用户或具有用户管理权限的用户执行此脚本

-- 1. 创建新用户（请根据需要修改用户名和密码）
CREATE USER IF NOT EXISTS 'onlinestudy_user'@'localhost' IDENTIFIED BY 'your_new_password';
CREATE USER IF NOT EXISTS 'onlinestudy_user'@'%' IDENTIFIED BY 'your_new_password';

-- 2. 授予对特定数据库的权限
-- 授予对ai数据库的所有权限
GRANT ALL PRIVILEGES ON ai.* TO 'onlinestudy_user'@'localhost';
GRANT ALL PRIVILEGES ON ai.* TO 'onlinestudy_user'@'%';

-- 如果使用其他数据库名，请相应修改：
-- GRANT ALL PRIVILEGES ON online_study_dev.* TO 'onlinestudy_user'@'localhost';
-- GRANT ALL PRIVILEGES ON online_study_dev.* TO 'onlinestudy_user'@'%';

-- 3. 刷新权限
FLUSH PRIVILEGES;

-- 4. 验证用户创建
SELECT User, Host FROM mysql.user WHERE User = 'onlinestudy_user';

-- 5. 显示用户权限
SHOW GRANTS FOR 'onlinestudy_user'@'localhost'; 