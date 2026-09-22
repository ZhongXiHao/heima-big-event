# 大事件接口文档 - V2.0（RESTful 版）

> 本文档由 V1.0 改写而来：路径只用名词，资源 ID 放进路径，动作交给 HTTP 方法，成功/失败交给 HTTP 状态码。
> 新旧路径对照见 [附录 A](#附录-a新旧接口对照)，前后端改动清单见 [附录 B](#附录-b前后端改动清单)。

---

## 0. 通用约定

### 0.1 接口总览

| 模块 | 操作 | 方法 | 路径 | 成功状态码 | 需登录 |
| ---- | ---- | ---- | ---- | ---------- | ------ |
| 用户 | 注册 | POST | `/users` | 201 | 否 |
| 用户 | 登录 | POST | `/sessions` | 201 | 否 |
| 用户 | 获取当前用户信息 | GET | `/users/me` | 200 | 是 |
| 用户 | 更新当前用户基本信息 | PUT | `/users/me` | 200 | 是 |
| 用户 | 更新当前用户头像 | PUT | `/users/me/avatar` | 204 | 是 |
| 用户 | 更新当前用户密码 | PUT | `/users/me/password` | 204 | 是 |
| 分类 | 新增分类 | POST | `/categories` | 201 | 是 |
| 分类 | 分类列表 | GET | `/categories` | 200 | 是 |
| 分类 | 分类详情 | GET | `/categories/{id}` | 200 | 是 |
| 分类 | 更新分类 | PUT | `/categories/{id}` | 200 | 是 |
| 分类 | 删除分类 | DELETE | `/categories/{id}` | 204 | 是 |
| 文章 | 新增文章 | POST | `/articles` | 201 | 是 |
| 文章 | 文章列表（条件分页） | GET | `/articles` | 200 | 是 |
| 文章 | 文章详情 | GET | `/articles/{id}` | 200 | 是 |
| 文章 | 更新文章 | PUT | `/articles/{id}` | 200 | 是 |
| 文章 | 删除文章 | DELETE | `/articles/{id}` | 204 | 是 |
| 文件 | 上传文件 | POST | `/files` | 201 | 是 |

### 0.2 认证

登录成功后服务端下发 JWT。之后除注册、登录外的所有请求，都要在请求头携带：

```
Authorization: Bearer <登录时下发的JWT令牌>
```

- 令牌缺失、无效或过期时，返回 `401`。
- **当前用户由后端从令牌中解析**（例如放入 `ThreadLocal`）。所有接口都不需要、也不应信任前端传来的用户 ID，这也是 `/users/me` 这种路径的由来。
- 分类和文章属于创建它们的用户；访问不属于自己的资源，一律按 `404` 处理（不暴露资源是否存在）。

### 0.3 请求约定

- 除文件上传（`multipart/form-data`）外，请求体统一使用 `application/json`。
- 资源 ID 放在路径里（`/articles/4`）。
- Query 参数只用于过滤、分页、排序（`/articles?pageNum=1&pageSize=3`）。
- 路径使用小写复数名词；JSON 字段统一使用驼峰命名；时间格式为 `yyyy-MM-dd HH:mm:ss`。

### 0.4 响应约定

**成功**：使用 2xx 状态码，响应体直接就是资源本身，没有外层包装。

| 状态码 | 含义 | 响应体 |
| ------ | ---- | ------ |
| 200 OK | 查询、更新成功 | 资源 / 资源列表 |
| 201 Created | 创建成功 | 新创建的资源 |
| 204 No Content | 成功，但无内容可返回 | 无 |

**失败**：使用 4xx / 5xx 状态码，响应体统一为：

```json
{
    "status": 400,
    "message": "用户名必须是5~16位非空字符"
}
```

| 状态码 | 含义 | 典型场景 |
| ------ | ---- | -------- |
| 400 Bad Request | 请求参数不合法 | 参数校验失败、两次新密码不一致 |
| 401 Unauthorized | 未认证 | 未登录、令牌过期、用户名或密码错误 |
| 404 Not Found | 资源不存在 | ID 不存在，或资源不属于当前用户 |
| 409 Conflict | 与现有资源冲突 | 用户名已被占用 |
| 500 Internal Server Error | 服务端异常 | 未预料到的错误 |

后文每个接口只列出该接口特有的失败情形；参数校验失败（400）和未登录（401）是所有接口的共性，不再重复。

---

## 1. 用户相关接口

### 1.1 注册

> **`POST /users`**
>
> 该接口用于注册新用户（创建一个 user 资源）。无需登录。

**请求体**（`application/json`）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| username | 用户名 | string | 是 | 5~16位非空字符 |
| password | 密码 | string | 是 | 5~16位非空字符 |

```json
{
    "username": "zhangsan",
    "password": "123456"
}
```

**成功响应**：`201 Created`

响应体不返回密码；返回的是新用户的公开信息。

```json
{
    "id": 5,
    "username": "zhangsan",
    "nickname": "",
    "email": "",
    "userPic": "",
    "createTime": "2023-09-02 22:21:31",
    "updateTime": "2023-09-02 22:21:31"
}
```

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 400 | 用户名或密码不符合长度要求 |
| 409 | 用户名已被占用 |

---

### 1.2 登录

> **`POST /sessions`**
>
> 该接口用于登录。登录的本质是"创建一个会话/令牌"，所以是对 `sessions` 集合做 POST。无需登录。
>
> 备注：业界也常见 `POST /auth/login`，两种写法都可以接受，项目内保持一致即可。

**请求体**（`application/json`）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| username | 用户名 | string | 是 | 5~16位非空字符 |
| password | 密码 | string | 是 | 5~16位非空字符 |

```json
{
    "username": "zhangsan",
    "password": "123456"
}
```

**成功响应**：`201 Created`

| 名称 | 类型 | 说明 |
| ---- | ---- | ---- |
| token | string | JWT 令牌 |

```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGFpbXMiOnsiaWQiOjUsInVzZXJuYW1lIjoid2FuZ2JhIn0sImV4cCI6MTY5MzcxNTk3OH0.pE_RATcoF7Nm9KEp9eC3CzcBbKWAFOL0IsuMNjnZ95M"
}
```

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 401 | 用户名不存在或密码错误（两种情况返回同样的提示，避免暴露用户名是否存在） |

---

### 1.3 获取当前用户信息

> **`GET /users/me`**
>
> 该接口用于获取当前已登录用户的详细信息。`me` 表示"令牌所属的用户"。

**请求参数**：无

**成功响应**：`200 OK`

| 名称 | 类型 | 说明 |
| ---- | ---- | ---- |
| id | number | 主键ID |
| username | string | 用户名 |
| nickname | string | 昵称 |
| email | string | 邮箱 |
| userPic | string | 头像地址 |
| createTime | string | 创建时间 |
| updateTime | string | 更新时间 |

```json
{
    "id": 5,
    "username": "wangba",
    "nickname": "",
    "email": "",
    "userPic": "",
    "createTime": "2023-09-02 22:21:31",
    "updateTime": "2023-09-02 22:21:31"
}
```

---

### 1.4 更新当前用户基本信息

> **`PUT /users/me`**
>
> 该接口用于更新已登录用户的基本信息（不含头像和密码）。
>
> 与 V1.0 的区别：请求体**不再包含 `id`**，用户身份完全来自令牌，避免"传别人的 id 改别人资料"的越权问题。

**请求体**（`application/json`）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| username | 用户名 | string | 否 | 5~16位非空字符 |
| nickname | 昵称 | string | 是 | 1~10位非空字符 |
| email | 邮箱 | string | 是 | 满足邮箱的格式 |

```json
{
    "username": "wangba",
    "nickname": "wb",
    "email": "wb@itcast.cn"
}
```

**成功响应**：`200 OK`，返回更新后的用户信息（结构同 1.3）。

```json
{
    "id": 5,
    "username": "wangba",
    "nickname": "wb",
    "email": "wb@itcast.cn",
    "userPic": "",
    "createTime": "2023-09-02 22:21:31",
    "updateTime": "2023-09-03 09:10:02"
}
```

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 409 | 修改后的用户名已被其他用户占用 |

---

### 1.5 更新当前用户头像

> **`PUT /users/me/avatar`**
>
> 该接口用于更新已登录用户的头像。头像是用户的子资源，PUT 表示整体替换它。
>
> 与 V1.0 的区别：头像地址由 query 参数改为放在请求体里。

**请求体**（`application/json`）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| avatarUrl | 头像地址 | string | 是 | url地址 |

```json
{
    "avatarUrl": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/9bf1cf5b-1420-4c1b-91ad-e0f4631cbed4.png"
}
```

**成功响应**：`204 No Content`（无响应体）

---

### 1.6 更新当前用户密码

> **`PUT /users/me/password`**
>
> 该接口用于更新已登录用户的密码。
>
> 与 V1.0 的区别：路径改为名词子资源；请求字段由下划线命名改为驼峰命名。

**请求体**（`application/json`）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| oldPwd | 原密码 | string | 是 | |
| newPwd | 新密码 | string | 是 | 5~16位非空字符 |
| rePwd | 确认新密码 | string | 是 | 必须与 newPwd 一致 |

```json
{
    "oldPwd": "123456",
    "newPwd": "234567",
    "rePwd": "234567"
}
```

**成功响应**：`204 No Content`（无响应体）

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 400 | newPwd 与 rePwd 不一致；原密码错误；新密码不符合长度要求 |

---

## 2. 文章分类相关接口

### 2.1 新增文章分类

> **`POST /categories`**
>
> 该接口用于新增文章分类。

**请求体**（`application/json`）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| categoryName | 分类名称 | string | 是 | |
| categoryAlias | 分类别名 | string | 是 | |

```json
{
    "categoryName": "人文",
    "categoryAlias": "rw"
}
```

**成功响应**：`201 Created`，返回新创建的分类。

```json
{
    "id": 7,
    "categoryName": "人文",
    "categoryAlias": "rw",
    "createTime": "2023-09-03 12:00:00",
    "updateTime": "2023-09-03 12:00:00"
}
```

---

### 2.2 文章分类列表

> **`GET /categories`**
>
> 该接口用于获取当前已登录用户创建的所有文章分类。

**请求参数**：无

**成功响应**：`200 OK`，响应体是数组。

| 名称 | 类型 | 说明 |
| ---- | ---- | ---- |
| id | number | 主键ID |
| categoryName | string | 分类名称 |
| categoryAlias | string | 分类别名 |
| createTime | string | 创建时间 |
| updateTime | string | 修改时间 |

```json
[
    {
        "id": 3,
        "categoryName": "美食",
        "categoryAlias": "my",
        "createTime": "2023-09-02 12:06:59",
        "updateTime": "2023-09-02 12:06:59"
    },
    {
        "id": 4,
        "categoryName": "娱乐",
        "categoryAlias": "yl",
        "createTime": "2023-09-02 12:08:16",
        "updateTime": "2023-09-02 12:08:16"
    }
]
```

---

### 2.3 获取文章分类详情

> **`GET /categories/{id}`**
>
> 该接口用于根据 ID 获取文章分类详情。

**路径参数**

| 参数名称 | 说明 | 类型 | 是否必须 |
| -------- | ---- | ---- | -------- |
| id | 分类主键ID | number | 是 |

请求示例：`GET /categories/6`

**成功响应**：`200 OK`

```json
{
    "id": 6,
    "categoryName": "风土人情",
    "categoryAlias": "ftrq",
    "createTime": "2023-09-03 11:07:13",
    "updateTime": "2023-09-03 11:13:39"
}
```

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 404 | 分类不存在，或不属于当前用户 |

---

### 2.4 更新文章分类

> **`PUT /categories/{id}`**
>
> 该接口用于更新文章分类。ID 在路径里，不再出现在请求体中。

**路径参数**：`id`（分类主键ID）

**请求体**（`application/json`）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| categoryName | 分类名称 | string | 是 | |
| categoryAlias | 分类别名 | string | 是 | |

```json
{
    "categoryName": "风土人情",
    "categoryAlias": "ftrq"
}
```

**成功响应**：`200 OK`，返回更新后的分类（结构同 2.3）。

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 404 | 分类不存在，或不属于当前用户 |

---

### 2.5 删除文章分类

> **`DELETE /categories/{id}`**
>
> 该接口用于根据 ID 删除文章分类。

**路径参数**：`id`（分类主键ID）

请求示例：`DELETE /categories/6`

**成功响应**：`204 No Content`（无响应体）

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 404 | 分类不存在，或不属于当前用户 |

---

## 3. 文章管理相关接口

### 3.1 新增文章

> **`POST /articles`**
>
> 该接口用于新增文章（发布文章）。

**请求体**（`application/json`）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| title | 文章标题 | string | 是 | 1~10个非空字符 |
| content | 文章正文 | string | 是 | |
| coverImg | 封面图像地址 | string | 是 | 必须是url地址 |
| state | 发布状态 | string | 是 | 已发布 \| 草稿 |
| categoryId | 文章分类ID | number | 是 | |

```json
{
    "title": "陕西旅游攻略",
    "content": "兵马俑,华清池,法门寺,华山...爱去哪去哪...",
    "coverImg": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/9bf1cf5b-1420-4c1b-91ad-e0f4631cbed4.png",
    "state": "草稿",
    "categoryId": 2
}
```

**成功响应**：`201 Created`，返回新创建的文章。

```json
{
    "id": 5,
    "title": "陕西旅游攻略",
    "content": "兵马俑,华清池,法门寺,华山...爱去哪去哪...",
    "coverImg": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/9bf1cf5b-1420-4c1b-91ad-e0f4631cbed4.png",
    "state": "草稿",
    "categoryId": 2,
    "createTime": "2023-09-03 11:55:30",
    "updateTime": "2023-09-03 11:55:30"
}
```

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 400 | 参数不合法，或 `categoryId` 对应的分类不存在 |

---

### 3.2 文章列表（条件分页）

> **`GET /articles`**
>
> 该接口用于根据条件查询文章，带分页。

**Query 参数**

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| pageNum | 当前页码 | number | 是 | |
| pageSize | 每页条数 | number | 是 | |
| categoryId | 文章分类ID | number | 否 | 不传则不按分类过滤 |
| state | 发布状态 | string | 否 | 已发布 \| 草稿；不传则不按状态过滤 |

请求示例：`GET /articles?pageNum=1&pageSize=3&categoryId=2&state=草稿`

**成功响应**：`200 OK`

| 名称 | 类型 | 说明 |
| ---- | ---- | ---- |
| total | number | 总记录数 |
| items | array | 当前页的文章列表 |
| items[].id | number | 主键ID |
| items[].title | string | 文章标题 |
| items[].content | string | 文章正文 |
| items[].coverImg | string | 封面图像地址 |
| items[].state | string | 发布状态：已发布 \| 草稿 |
| items[].categoryId | number | 文章分类ID |
| items[].createTime | string | 创建时间 |
| items[].updateTime | string | 更新时间 |

```json
{
    "total": 1,
    "items": [
        {
            "id": 5,
            "title": "陕西旅游攻略",
            "content": "兵马俑,华清池,法门寺,华山...爱去哪去哪...",
            "coverImg": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/9bf1cf5b-1420-4c1b-91ad-e0f4631cbed4.png",
            "state": "草稿",
            "categoryId": 2,
            "createTime": "2023-09-03 11:55:30",
            "updateTime": "2023-09-03 11:55:30"
        }
    ]
}
```

---

### 3.3 获取文章详情

> **`GET /articles/{id}`**
>
> 该接口用于根据 ID 获取文章详细信息。

**路径参数**：`id`（文章主键ID）

请求示例：`GET /articles/4`

**成功响应**：`200 OK`

```json
{
    "id": 4,
    "title": "北京旅游攻略",
    "content": "天安门,颐和园,鸟巢,长城...爱去哪去哪...",
    "coverImg": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/9bf1cf5b-1420-4c1b-91ad-e0f4631cbed4.png",
    "state": "已发布",
    "categoryId": 2,
    "createTime": "2023-09-03 11:35:04",
    "updateTime": "2023-09-03 11:40:31"
}
```

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 404 | 文章不存在，或不属于当前用户 |

---

### 3.4 更新文章

> **`PUT /articles/{id}`**
>
> 该接口用于更新文章信息。PUT 表示整体替换，所以所有字段都必填。

**路径参数**：`id`（文章主键ID）

**请求体**（`application/json`）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| title | 文章标题 | string | 是 | 1~10个非空字符 |
| content | 文章正文 | string | 是 | |
| coverImg | 封面图像地址 | string | 是 | 必须是url地址 |
| state | 发布状态 | string | 是 | 已发布 \| 草稿 |
| categoryId | 文章分类ID | number | 是 | |

```json
{
    "title": "北京旅游攻略",
    "content": "天安门,颐和园,鸟巢,长城...爱去哪去哪...",
    "coverImg": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/9bf1cf5b-1420-4c1b-91ad-e0f4631cbed4.png",
    "state": "已发布",
    "categoryId": 2
}
```

**成功响应**：`200 OK`，返回更新后的文章（结构同 3.3）。

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 404 | 文章不存在，或不属于当前用户 |

---

### 3.5 删除文章

> **`DELETE /articles/{id}`**
>
> 该接口用于根据 ID 删除文章。

**路径参数**：`id`（文章主键ID）

请求示例：`DELETE /articles/4`

**成功响应**：`204 No Content`（无响应体）

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 404 | 文章不存在，或不属于当前用户 |

---

## 4. 其他接口

### 4.1 文件上传

> **`POST /files`**
>
> 该接口用于上传文件（单文件），文件存入阿里云 OSS。上传一个文件就是"创建一个 file 资源"。

**请求格式**：`multipart/form-data`

| 参数名称 | 说明 | 类型 | 是否必须 |
| -------- | ---- | ---- | -------- |
| file | 表单中文件字段的名字 | file | 是 |

**成功响应**：`201 Created`

| 名称 | 类型 | 说明 |
| ---- | ---- | ---- |
| url | string | 文件在阿里云上的存储地址 |

```json
{
    "url": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/b5811871-acc8-4583-8399-cf0dc73591ab.png"
}
```

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 400 | 未携带文件，或文件为空 |

---

## 附录 A：新旧接口对照

| 操作 | V1.0 | V2.0 |
| ---- | ---- | ---- |
| 注册 | `POST /user/register`（表单） | `POST /users`（JSON） |
| 登录 | `POST /user/login`（表单，返回 `data` 为字符串） | `POST /sessions`（JSON，返回 `{token}`） |
| 获取用户信息 | `GET /user/userInfo` | `GET /users/me` |
| 更新用户信息 | `PUT /user/update`（body 带 id） | `PUT /users/me`（body 不带 id） |
| 更新头像 | `PATCH /user/updateAvatar?avatarUrl=` | `PUT /users/me/avatar`（JSON） |
| 更新密码 | `PATCH /user/updatePwd`（`old_pwd` 等） | `PUT /users/me/password`（`oldPwd` 等） |
| 新增分类 | `POST /category` | `POST /categories` |
| 分类列表 | `GET /category` | `GET /categories` |
| 分类详情 | `GET /category/detail?id=` | `GET /categories/{id}` |
| 更新分类 | `PUT /category`（body 带 id） | `PUT /categories/{id}` |
| 删除分类 | `DELETE /category?id=` | `DELETE /categories/{id}` |
| 新增文章 | `POST /article` | `POST /articles` |
| 文章列表 | `GET /article?pageNum=...` | `GET /articles?pageNum=...` |
| 文章详情 | `GET /article/detail?id=` | `GET /articles/{id}` |
| 更新文章 | `PUT /article`（body 带 id） | `PUT /articles/{id}` |
| 删除文章 | `DELETE /article?id=` | `DELETE /articles/{id}` |
| 文件上传 | `POST /upload`，返回字符串 | `POST /files`，返回 `{url}` |

**响应格式的整体变化**

| | V1.0 | V2.0 |
| - | ---- | ---- |
| 成功判断 | body 里 `status == 0` | HTTP 状态码为 2xx |
| 成功数据 | 包在 `data` 字段里 | 响应体就是资源本身 |
| 失败信息 | `status: 1` + `message`，HTTP 仍为 200 | 4xx/5xx + `{status, message}` |
| 认证头 | `Authorization: <token>` | `Authorization: Bearer <token>` |

---

## 附录 B：前后端改动清单

### 后端（Spring Boot 3）

- **路径与参数绑定**
  - 类上用 `@RequestMapping("/articles")`，方法上用 `@GetMapping("/{id}")`、`@PutMapping("/{id}")`、`@DeleteMapping("/{id}")`。
  - 路径里的 ID 用 `@PathVariable Integer id` 接收；请求体用 `@RequestBody`。
  - 登录、注册不再是 `@RequestParam`，改为 `@RequestBody`（建议给登录、注册各建一个请求 DTO）。
- **状态码**
  - 创建接口加 `@ResponseStatus(HttpStatus.CREATED)`，或返回 `ResponseEntity.status(201).body(...)`。
  - 删除、更新头像、更新密码返回 204：`@ResponseStatus(HttpStatus.NO_CONTENT)`，方法返回 `void`。
- **异常处理**
  - 用 `@RestControllerAdvice` + `@ExceptionHandler` 把异常映射成 400 / 401 / 404 / 409，统一输出 `{status, message}`。
  - 参数校验失败（`MethodArgumentNotValidException`）→ 400。
  - 可以自定义 `NotFoundException`、`ConflictException` 等业务异常，在 Service 层抛出。
  - Spring Boot 3 内置了 `ProblemDetail`（RFC 9457），想更标准可以用它替换 `{status, message}`。
- **拦截器**
  - 解析 `Authorization` 时先去掉 `Bearer ` 前缀，再校验 JWT。
  - 校验失败返回 401；校验成功后把用户信息放进 `ThreadLocal`，请求结束后 `remove()`。
  - 放行路径改为：`POST /users`、`POST /sessions`。
- **越权防护**：Service / Mapper 层查询和修改分类、文章时，SQL 条件里带上当前用户 ID（`WHERE id = ? AND create_user = ?`），查不到就抛 `NotFoundException`。

### 前端（Vue3 + axios）

- **请求拦截器**：`config.headers.Authorization = \`Bearer ${token}\``。
- **响应处理**
  - 成功回调里直接使用 `res.data`（不再有 `res.data.data`）。
  - 失败统一在响应拦截器的错误分支处理：读取 `error.response.status` 和 `error.response.data.message` 弹出提示；遇到 401 清除 token 并跳转登录页。
  - 不再判断 `status === 0`。
- **接口封装**：把 API 文件里的路径和方法按附录 A 改一遍；ID 用模板字符串拼进路径（`` `/articles/${id}` ``）。
- **提交方式**：注册、登录、更新头像原先是表单或 query 参数，现在统一提交 JSON 对象。
- **修改密码**：请求体字段名改为 `oldPwd`、`newPwd`、`rePwd`；成功后（204）清除 token 并跳转登录页。
- **文件上传**：字段名仍是 `file`；返回值取 `res.data.url`。
