# 数据库初始化和配置指南

## 概述
本项目已配置为使用独立的数据库 `online_study_dev`，避免与其他数据库产生冲突。

## 配置修改
- **应用配置**: `application.yml` 中的数据库连接已修改为 `online_study_dev`
- **SQL脚本**: 所有相关SQL脚本已更新为使用新的数据库名称

## SQL脚本运行顺序

### 1. 初始化数据库（必须首先运行）
```bash
# 创建数据库
mysql -u root -p < init_database.sql
```

### 2. 主要表结构初始化（二选一）

#### 方案A：使用完整的表结构脚本（推荐）
```bash
# 运行完整的在线学习平台表结构脚本
mysql -u root -p < online_study.sql
```

#### 方案B：使用现有的ai.sql脚本
```bash
# 运行AI功能相关的表结构脚本
mysql -u root -p < ../sql/ai.sql
```

### 3. 添加AI增强功能（可选）
```bash
# 添加AI智能生成功能表
mysql -u root -p < ai_features.sql
```

### 4. 数据库结构更新（可选，按需运行）
```bash
# 修复课程材料类型枚举
mysql -u root -p < fix_material_type.sql

# 更新材料类型和其他字段
mysql -u root -p < update_material_type.sql
```

## 一键初始化脚本

### Windows PowerShell
```powershell
# 设置MySQL用户和密码
$mysqlUser = "root"
$mysqlPassword = "238729046"

# 运行初始化脚本
Get-Content init_database.sql | mysql -u $mysqlUser -p$mysqlPassword
Get-Content online_study.sql | mysql -u $mysqlUser -p$mysqlPassword
Get-Content ai_features.sql | mysql -u $mysqlUser -p$mysqlPassword

Write-Host "数据库初始化完成！"
```

### Linux/macOS Bash
```bash
#!/bin/bash
MYSQL_USER="root"
MYSQL_PASSWORD="238729046"

# 运行初始化脚本
mysql -u $MYSQL_USER -p$MYSQL_PASSWORD < init_database.sql
mysql -u $MYSQL_USER -p$MYSQL_PASSWORD < online_study.sql
mysql -u $MYSQL_USER -p$MYSQL_PASSWORD < ai_features.sql

echo "数据库初始化完成！"
```

## 验证数据库创建

### 1. 连接到MySQL并验证数据库
```sql
-- 登录MySQL
mysql -u root -p

-- 显示所有数据库
SHOW DATABASES;

-- 使用新数据库
USE online_study_dev;

-- 显示所有表
SHOW TABLES;

-- 检查用户表结构
DESCRIBE users;
```

### 2. 验证应用连接
启动Spring Boot应用，检查日志中的数据库连接信息：
```
连接URL: jdbc:mysql://localhost:3306/online_study_dev
```

## 注意事项

1. **密码安全**: 生产环境中请修改 `application.yml` 中的数据库密码
2. **字符集**: 所有脚本已设置为使用 `utf8mb4` 字符集，支持中文和emoji
3. **外键约束**: 表结构包含完整的外键约束，确保数据完整性
4. **测试数据**: `online_study.sql` 包含基本的测试数据

## 故障排除

### 常见问题
1. **权限问题**: 确保MySQL用户有创建数据库的权限
2. **字符集问题**: 运行前设置 `SET NAMES utf8mb4;`
3. **外键约束错误**: 按照建议的顺序运行脚本

### 重置数据库
如需重新初始化：
```sql
DROP DATABASE IF EXISTS online_study_dev;
```
然后重新运行初始化脚本。 