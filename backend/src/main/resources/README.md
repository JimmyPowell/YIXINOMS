# 配置文件说明

本项目使用了Spring Boot的配置文件机制，为保护敏感信息，我们不将真实配置文件提交到版本控制系统中。

## 配置文件设置

1. 复制模板配置文件创建实际的配置文件：
   ```bash
   cp application-example.yml application.yml
   ```

2. 编辑 `application.yml` 文件，根据你的实际环境修改相关配置：
   - JWT密钥
   - 邮件服务器配置
   - 数据库连接信息
   - 等其他配置项

## 敏感信息处理

配置文件中的敏感信息可以通过两种方式设置：

1. 直接在配置文件中设置（仅建议在开发环境使用）
2. 通过环境变量覆盖（推荐在生产环境使用）

### 环境变量列表

以下是本项目使用的主要环境变量：

| 环境变量名 | 描述 | 默认值 |
|------------|------|--------|
| MAIL_HOST | 邮件服务器地址 | smtp.example.com |
| MAIL_PORT | 邮件服务器端口 | 465 |
| MAIL_USERNAME | 邮箱账号 | your-email@example.com |
| MAIL_PASSWORD | 邮箱密码或授权码 | your-email-password-or-app-code |
| DB_HOST | 数据库主机 | localhost |
| DB_PORT | 数据库端口 | 3306 |
| DB_NAME | 数据库名称 | yixinoms |
| DB_USERNAME | 数据库用户名 | root |
| DB_PASSWORD | 数据库密码 | your-db-password |
| LOG_LEVEL | 日志级别 | info |

## 注意事项

- 确保 `application.yml` 文件已经被添加到 `.gitignore` 中，防止意外提交
- 不要在公共环境或共享代码库中暴露敏感配置
- 在生产环境中，推荐使用环境变量或外部配置服务管理敏感信息 