# 大事件（big-event）

一个基于 Spring Boot 的个人文章发布后端，提供用户账号体系（注册/登录/资料/头像/改密）、文章分类管理、文章的增删改查（分页+条件筛选）以及图片上传能力。数据归属到创建它的用户，接口通过 JWT + Redis 校验登录状态。

## 技术栈

- **框架**：Spring Boot 4.1.1（Java 17）
- **持久层**：MyBatis（注解 SQL 为主，`ArticleMapper` 的动态条件查询用 XML）+ MySQL，分页用 PageHelper
- **登录态**：JWT（`com.auth0:java-jwt`）签发令牌，Redis（`spring-boot-starter-data-redis`）维护"每个用户当前有效 token"，实现单点登录——重新登录会顶掉旧会话，改密码会立即使旧会话失效
- **参数校验**：Jakarta Bean Validation（`spring-boot-starter-validation`），统一由 `GlobalExceptionHandler` 转成结构化错误响应
- **文件存储**：阿里云 OSS（`alibabacloud-oss-v2`），用于用户头像等图片上传
- **其它**：Lombok 简化实体/DTO/VO；全局 Jackson 时间格式统一为 `yyyy-MM-dd HH:mm:ss`（`application.yml`）

## 功能概览

| 模块 | 能力 |
| --- | --- |
| 用户 | 注册、登录、获取/更新当前用户信息、更新头像、修改密码 |
| 文章分类 | 新增、列表、详情、更新、删除（均只作用于当前登录用户自己创建的分类；删除有文章的分类会被拒绝） |
| 文章 | 新增、按分类/发布状态筛选+分页查询、详情、更新、删除（均只作用于当前登录用户自己创建的文章） |
| 文件 | 上传图片到阿里云 OSS，返回可访问 URL |

## 项目结构

```
src/main/java/com/cshlands/
├── controller/     # REST 接口层
├── service/        # 业务接口 + serviceImpl 实现
├── mapper/         # MyBatis Mapper（注解 SQL / handler）
├── pojo/           # 数据库实体
├── dto/            # 请求体
├── vo/             # 响应体
├── interceptors/   # 登录态校验拦截器
├── exception/      # 业务异常 + 全局异常处理
├── utils/          # JWT、MD5、Redis key、阿里云 OSS 等工具类
└── config/         # 拦截器注册等 Web 配置
```

## 认证机制

1. 登录成功后，服务端签发 JWT，并把 `login:token:<userId>` → `token` 写入 Redis（TTL 与 JWT 有效期一致）。
2. 之后的请求需在请求头携带 `Authorization: Bearer <token>`；`LoginInterceptor` 会校验 JWT 本身合法性，并要求它与 Redis 中该用户当前记录的 token **完全一致**——不一致（比如已经在别处重新登录、或已改密码）会返回 401。
3. 修改密码会主动清除 Redis 中对应的 key，使该用户所有旧会话立即失效。

## 快速开始

### 前置依赖
- JDK 17+
- MySQL（需自行建库建表，数据库名 `big_event`）
- Redis（默认 `localhost:6379`）
- 阿里云 OSS 的 AccessKey（供 `EnvironmentVariableCredentialsProvider` 读取），仅在需要文件上传功能时必需

### 环境变量
可通过环境变量覆盖 `src/main/resources/application.yml` 中的默认值：

| 变量 | 说明 | 默认值 |
| --- | --- | --- |
| `MYSQL_HOST` / `MYSQL_PORT` | MySQL 地址 | `localhost` / `3306` |
| `MYSQL_USER` / `MYSQL_PASSWORD` | MySQL 账号密码 | `root` / `password` |
| `JWT_SECRET` | JWT 签名密钥 | 内置开发用默认值，**生产环境务必覆盖** |
| `JWT_EXPIRATION_MS` | JWT 有效期（毫秒） | `86400000`（24 小时） |

Redis 地址目前在 `application.yml` 中写死为 `localhost:6379`，如需自定义请直接修改该文件。

### 运行

```bash
mvn spring-boot:run
```

服务默认监听 `http://localhost:3000`，接口统一挂在 `/api` 前缀下。

### 测试

```bash
mvn test
```

## 相关文档

- [`docs/大事件接口文档-RESTful版.md`](docs/大事件接口文档-RESTful版.md)：接口列表、请求/响应格式、鉴权约定（`AuthController` 尚未完全迁移到文档描述的 RESTful 路径，仍是历史版路径 `/auth/login`、`/auth/register`）
- [`docs/代码优化建议.md`](docs/代码优化建议.md)：一次代码走查记录的可改进点（重复代码、死代码、风格统一等）
