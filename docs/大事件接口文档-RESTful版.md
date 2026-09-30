# 大事件接口文档 - RESTful 版

> 本文档与当前后端代码（Spring Boot 3 + MyBatis + Redis）保持一致。路径只用名词，资源 ID 放进路径，动作交给 HTTP 方法，成功/失败同时体现在 HTTP 状态码上。

---

## 0. 通用约定

### 0.1 基础地址

```
http://localhost:3000/api
```

端口 `3000`、上下文路径 `/api`（见 `application.yml`）。下文所有路径都省略了 `/api` 前缀。

### 0.2 接口总览

| 模块 | 操作 | 方法 | 路径 | 成功状态码 | 需登录 |
| ---- | ---- | ---- | ---- | ---------- | ------ |
| 认证 | 注册 | POST | `/auth/register` | 201 | 否 |
| 认证 | 登录 | POST | `/auth/login` | 200 | 否 |
| 用户 | 获取当前用户信息 | GET | `/users/me` | 200 | 是 |
| 用户 | 更新当前用户基本信息 | PUT | `/users/me` | 200 | 是 |
| 用户 | 更新当前用户头像 | PUT | `/users/me/avatar` | 200 | 是 |
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
| 文件 | 上传文件 | POST | `/files` | 200 | 是 |

### 0.3 认证

登录成功后服务端下发 JWT。之后除注册、登录外的所有请求，都要在请求头携带：

```
Authorization: Bearer <登录时下发的JWT令牌>
```

- 令牌缺失、格式不是 `Bearer xxx`、签名无效、已过期，或已被顶替/清除，一律返回 `401`，`msg` 为 `未授权`。
- 令牌有效期默认 24 小时（环境变量 `JWT_EXPIRATION_MS`，默认 `86400000` 毫秒）。
- **单点登录**：服务端在 Redis 中以用户 ID 为 key 只保存该用户最新的一个令牌。同一账号再次登录后，之前签发的令牌立即失效；修改密码成功后，该用户的令牌也会被清除，需要重新登录。
- **当前用户由后端从令牌中解析**（存入 `ThreadLocal`），所有接口都不需要、也不应传用户 ID，这也是 `/users/me` 这种路径的由来。
- 分类和文章属于创建它们的用户；访问不属于自己的分类/文章，一律按 `404` 处理（不暴露资源是否存在）。

### 0.4 请求约定

- 除注册、登录（表单/Query 参数）和文件上传（`multipart/form-data`）外，请求体使用 `application/json`。
- 资源 ID 放在路径里（`/articles/4`）。
- Query 参数只用于过滤、分页（`/articles?pageNum=1&pageSize=3`）。
- 路径使用小写复数名词；JSON 字段使用驼峰命名。
- 时间字段（`createTime`、`updateTime`）为 ISO-8601 格式，如 `2023-09-02T22:21:31`（时区 GMT+8）。

### 0.5 响应约定

**成功**：HTTP 状态码为 2xx，响应体为统一包装 `Result`：

```json
{
    "code": 200,
    "msg": "success",
    "data": { }
}
```

| 字段 | 类型 | 说明 |
| ---- | ---- | ---- |
| code | number | 成功时恒为 `200`（即使 HTTP 状态码是 201） |
| msg | string | 成功时恒为 `success` |
| data | any | 业务数据；无数据时为 `null` |

> 例外（响应体没有 `Result` 包装）：
> - `204 No Content` 的接口：无响应体。
> - `POST /articles`（201）：响应体直接是文章对象，没有 `code/msg/data`。

**失败**：HTTP 状态码为 4xx / 5xx，响应体同样是 `Result`，`data` 为 `null`：

```json
{
    "code": 400,
    "msg": "用户名必须是5~16位非空字符",
    "data": null
}
```

| 状态码 | 含义 | 典型场景 |
| ------ | ---- | -------- |
| 400 Bad Request | 请求参数不合法 | 参数校验失败、请求体格式错误、枚举值/参数类型不合法、两次新密码不一致、原密码错误 |
| 401 Unauthorized | 未认证 | 未登录、令牌无效/过期/被顶替、用户名或密码错误 |
| 404 Not Found | 资源不存在 | ID 不存在，或资源不属于当前用户 |
| 409 Conflict | 与现有资源冲突 | 用户名已被占用；分类下仍有文章无法删除 |
| 500 Internal Server Error | 服务端异常 | 未预料到的错误，`msg` 固定为 `Internal server error` |

`400` 的 `msg` 有三种来源：

- 方法参数校验（注册、登录的 Query/表单参数）：直接是校验注解上的提示，如 `用户名必须是5~16位非空字符`。
- 请求体校验（`@RequestBody` + `@Validated`）：`<字段名> <提示>`，如 `title 标题必须是1~10个非空字符`；只返回第一个出错字段。
- 请求体无法解析（JSON 格式错误、`state` 不是合法枚举值等）：`请求体格式错误或参数值不合法`；Query/路径参数类型不对：`参数 xxx 的值不合法`。

后文每个接口只列出该接口特有的失败情形；参数校验失败（400）和未登录（401）是所有需登录接口的共性，不再重复。

---

## 1. 认证接口

### 1.1 注册

> **`POST /auth/register`**
>
> 该接口用于注册新用户。无需登录。

**请求参数**：Query 或 `application/x-www-form-urlencoded` 表单（**不是 JSON**）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| username | 用户名 | string | 是 | 5~16位非空字符 |
| password | 密码 | string | 是 | 5~16位非空字符 |

请求示例：`POST /auth/register?username=zhangsan&password=123456`

**成功响应**：`201 Created`

响应体不返回密码；`data` 是新用户的公开信息。

| 名称 | 类型 | 说明 |
| ---- | ---- | ---- |
| id | number | 主键ID |
| username | string | 用户名 |
| nickname | string | 昵称，新注册为 `null` |
| email | string | 邮箱，新注册为 `null` |
| userPic | string | 头像地址，新注册为 `null` |
| createTime | string | 创建时间 |
| updateTime | string | 更新时间 |

```json
{
    "code": 200,
    "msg": "success",
    "data": {
        "id": 5,
        "username": "zhangsan",
        "nickname": null,
        "email": null,
        "userPic": null,
        "createTime": "2023-09-02T22:21:31",
        "updateTime": "2023-09-02T22:21:31"
    }
}
```

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 400 | 用户名或密码不符合 5~16 位非空字符的要求 |
| 409 | 用户名已存在（`msg`: `用户名已存在`） |

---

### 1.2 登录

> **`POST /auth/login`**
>
> 该接口用于登录，成功后返回 JWT。无需登录。同一账号再次登录会使之前的令牌失效。

**请求参数**：Query 或 `application/x-www-form-urlencoded` 表单（**不是 JSON**）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| username | 用户名 | string | 是 | 5~16位非空字符 |
| password | 密码 | string | 是 | 不做格式校验，只比对是否正确 |

请求示例：`POST /auth/login?username=zhangsan&password=123456`

**成功响应**：`200 OK`

| 名称 | 类型 | 说明 |
| ---- | ---- | ---- |
| token | string | JWT 令牌 |
| username | string | 用户名 |

```json
{
    "code": 200,
    "msg": "success",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGFpbXMiOnsiaWQiOjUsInVzZXJuYW1lIjoid2FuZ2JhIn0sImV4cCI6MTY5MzcxNTk3OH0.pE_RATcoF7Nm9KEp9eC3CzcBbKWAFOL0IsuMNjnZ95M",
        "username": "zhangsan"
    }
}
```

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 400 | 用户名不符合 5~16 位非空字符的要求 |
| 401 | 用户名不存在或密码错误（两种情况返回同样的 `用户名或密码错误`，避免暴露用户名是否存在） |

---

## 2. 用户相关接口

### 2.1 获取当前用户信息

> **`GET /users/me`**
>
> 该接口用于获取当前已登录用户的详细信息。`me` 表示"令牌所属的用户"。

**请求参数**：无

**成功响应**：`200 OK`，`data` 结构同 1.1。

```json
{
    "code": 200,
    "msg": "success",
    "data": {
        "id": 5,
        "username": "zhangsan",
        "nickname": null,
        "email": null,
        "userPic": null,
        "createTime": "2023-09-02T22:21:31",
        "updateTime": "2023-09-02T22:21:31"
    }
}
```

---

### 2.2 更新当前用户基本信息

> **`PUT /users/me`**
>
> 该接口用于更新已登录用户的昵称和邮箱。用户身份完全来自令牌，请求体不含 `id`。**用户名不可修改**；头像请用 2.3，密码请用 2.4。

**请求体**（`application/json`）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| nickname | 昵称 | string | 是 | 1~10位非空字符（不能含空白） |
| email | 邮箱 | string | 否 | 满足邮箱格式；不传/传 `null` 会把邮箱置空 |

```json
{
    "nickname": "wb",
    "email": "wb@itcast.cn"
}
```

**成功响应**：`200 OK`，`data` 为更新后的用户信息（结构同 2.1）。

```json
{
    "code": 200,
    "msg": "success",
    "data": {
        "id": 5,
        "username": "zhangsan",
        "nickname": "wb",
        "email": "wb@itcast.cn",
        "userPic": null,
        "createTime": "2023-09-02T22:21:31",
        "updateTime": "2023-09-03T09:10:02"
    }
}
```

---

### 2.3 更新当前用户头像

> **`PUT /users/me/avatar`**
>
> 该接口用于更新已登录用户的头像。头像是用户的子资源，PUT 表示整体替换它。

**请求体**（`application/json`）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| avatarUrl | 头像地址 | string | 是 | 合法的 URL 地址 |

```json
{
    "avatarUrl": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/9bf1cf5b-1420-4c1b-91ad-e0f4631cbed4.png"
}
```

**成功响应**：`200 OK`，`data` 为 `null`

```json
{
    "code": 200,
    "msg": "success",
    "data": null
}
```

---

### 2.4 更新当前用户密码

> **`PUT /users/me/password`**
>
> 该接口用于更新已登录用户的密码。修改成功后，当前用户所有已登录的令牌都会失效，需要重新登录。

**请求体**（`application/json`）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| oldPassword | 原密码 | string | 是 | 不能为空 |
| newPassword | 新密码 | string | 是 | 5~16位非空字符 |
| confirmNewPassword | 确认新密码 | string | 是 | 必须与 newPassword 一致 |

```json
{
    "oldPassword": "123456",
    "newPassword": "234567",
    "confirmNewPassword": "234567"
}
```

**成功响应**：`204 No Content`（无响应体）

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 400 | 字段为空或新密码不符合长度要求；`新密码与确认密码不一致`；`原密码错误` |

---

## 3. 文章分类相关接口

### 3.1 新增文章分类

> **`POST /categories`**
>
> 该接口用于新增文章分类，分类归属当前用户。

**请求体**（`application/json`）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| categoryName | 分类名称 | string | 是 | 不能为空白 |
| categoryAlias | 分类别名 | string | 是 | 不能为空白 |

```json
{
    "categoryName": "人文",
    "categoryAlias": "rw"
}
```

**成功响应**：`201 Created`，`data` 为新创建的分类。

```json
{
    "code": 200,
    "msg": "success",
    "data": {
        "id": 7,
        "categoryName": "人文",
        "categoryAlias": "rw",
        "createTime": "2023-09-03T12:00:00",
        "updateTime": "2023-09-03T12:00:00"
    }
}
```

---

### 3.2 文章分类列表

> **`GET /categories`**
>
> 该接口用于获取当前已登录用户创建的所有文章分类（不分页）。

**请求参数**：无

**成功响应**：`200 OK`，`data` 是数组。

| 名称 | 类型 | 说明 |
| ---- | ---- | ---- |
| id | number | 主键ID |
| categoryName | string | 分类名称 |
| categoryAlias | string | 分类别名 |
| createTime | string | 创建时间 |
| updateTime | string | 修改时间 |

```json
{
    "code": 200,
    "msg": "success",
    "data": [
        {
            "id": 3,
            "categoryName": "美食",
            "categoryAlias": "my",
            "createTime": "2023-09-02T12:06:59",
            "updateTime": "2023-09-02T12:06:59"
        },
        {
            "id": 4,
            "categoryName": "娱乐",
            "categoryAlias": "yl",
            "createTime": "2023-09-02T12:08:16",
            "updateTime": "2023-09-02T12:08:16"
        }
    ]
}
```

---

### 3.3 获取文章分类详情

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
    "code": 200,
    "msg": "success",
    "data": {
        "id": 6,
        "categoryName": "风土人情",
        "categoryAlias": "ftrq",
        "createTime": "2023-09-03T11:07:13",
        "updateTime": "2023-09-03T11:13:39"
    }
}
```

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 404 | 分类不存在，或不属于当前用户（`msg`: `分类不存在`） |

---

### 3.4 更新文章分类

> **`PUT /categories/{id}`**
>
> 该接口用于更新文章分类。ID 在路径里，请求体中不含 ID。

**路径参数**：`id`（分类主键ID）

**请求体**（`application/json`）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| categoryName | 分类名称 | string | 是 | 不能为空白 |
| categoryAlias | 分类别名 | string | 是 | 不能为空白 |

```json
{
    "categoryName": "风土人情",
    "categoryAlias": "ftrq"
}
```

**成功响应**：`200 OK`，`data` 为更新后的分类（结构同 3.3）。

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 404 | 分类不存在，或不属于当前用户 |

---

### 3.5 删除文章分类

> **`DELETE /categories/{id}`**
>
> 该接口用于根据 ID 删除文章分类。分类下还有文章时不允许删除。

**路径参数**：`id`（分类主键ID）

请求示例：`DELETE /categories/6`

**成功响应**：`204 No Content`（无响应体）

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 404 | 分类不存在，或不属于当前用户 |
| 409 | 该分类下还有文章，无法删除（`msg`: `该分类下还有文章，无法删除`） |

---

## 4. 文章管理相关接口

**发布状态 `state`**：只有两个合法取值，且必须使用中文原文——`已发布`、`草稿`。传其他值返回 400。

### 4.1 新增文章

> **`POST /articles`**
>
> 该接口用于新增文章（发布文章）。

**请求体**（`application/json`）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| title | 文章标题 | string | 是 | 1~10个非空字符（不能含空白） |
| content | 文章正文 | string | 是 | 不能为空白 |
| coverImg | 封面图像地址 | string | 是 | 必须是合法的 URL 地址 |
| state | 发布状态 | string | 是 | `已发布` \| `草稿` |
| categoryId | 文章分类ID | number | 是 | 必须是当前用户自己的分类 |

```json
{
    "title": "陕西旅游攻略",
    "content": "兵马俑,华清池,法门寺,华山...爱去哪去哪...",
    "coverImg": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/9bf1cf5b-1420-4c1b-91ad-e0f4631cbed4.png",
    "state": "草稿",
    "categoryId": 2
}
```

**成功响应**：`201 Created`

> 注意：本接口**没有** `Result` 包装，响应体直接是新创建的文章。

```json
{
    "id": 5,
    "title": "陕西旅游攻略",
    "content": "兵马俑,华清池,法门寺,华山...爱去哪去哪...",
    "coverImg": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/9bf1cf5b-1420-4c1b-91ad-e0f4631cbed4.png",
    "state": "草稿",
    "categoryId": 2,
    "createTime": "2023-09-03T11:55:30",
    "updateTime": "2023-09-03T11:55:30"
}
```

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 400 | 参数不合法（含 `state` 不是合法取值） |
| 404 | `categoryId` 对应的分类不存在，或不属于当前用户（`msg`: `分类不存在`） |

---

### 4.2 文章列表（条件分页）

> **`GET /articles`**
>
> 该接口用于查询当前用户的文章，带分页和可选过滤条件。

**Query 参数**

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| pageNum | 当前页码 | number | 否 | 默认 `1` |
| pageSize | 每页条数 | number | 否 | 默认 `10` |
| categoryId | 文章分类ID | number | 否 | 不传则不按分类过滤 |
| state | 发布状态 | string | 否 | `已发布` \| `草稿`；不传或传空串则不按状态过滤；其他值返回 400 |

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
| items[].state | string | 发布状态：`已发布` \| `草稿` |
| items[].categoryId | number | 文章分类ID |
| items[].createTime | string | 创建时间 |
| items[].updateTime | string | 更新时间 |

```json
{
    "code": 200,
    "msg": "success",
    "data": {
        "total": 1,
        "items": [
            {
                "id": 5,
                "title": "陕西旅游攻略",
                "content": "兵马俑,华清池,法门寺,华山...爱去哪去哪...",
                "coverImg": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/9bf1cf5b-1420-4c1b-91ad-e0f4631cbed4.png",
                "state": "草稿",
                "categoryId": 2,
                "createTime": "2023-09-03T11:55:30",
                "updateTime": "2023-09-03T11:55:30"
            }
        ]
    }
}
```

---

### 4.3 获取文章详情

> **`GET /articles/{id}`**
>
> 该接口用于根据 ID 获取文章详细信息。

**路径参数**：`id`（文章主键ID）

请求示例：`GET /articles/4`

**成功响应**：`200 OK`

```json
{
    "code": 200,
    "msg": "success",
    "data": {
        "id": 4,
        "title": "北京旅游攻略",
        "content": "天安门,颐和园,鸟巢,长城...爱去哪去哪...",
        "coverImg": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/9bf1cf5b-1420-4c1b-91ad-e0f4631cbed4.png",
        "state": "已发布",
        "categoryId": 2,
        "createTime": "2023-09-03T11:35:04",
        "updateTime": "2023-09-03T11:40:31"
    }
}
```

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 404 | 文章不存在，或不属于当前用户（`msg`: `文章不存在`） |

---

### 4.4 更新文章

> **`PUT /articles/{id}`**
>
> 该接口用于更新文章信息。PUT 表示整体替换，所以所有字段都必填，校验规则与新增文章（4.1）一致。可以通过 `categoryId` 把文章移到当前用户的另一个分类。

**路径参数**：`id`（文章主键ID）

**请求体**（`application/json`）

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| title | 文章标题 | string | 是 | 1~10个非空字符（不能含空白） |
| content | 文章正文 | string | 是 | 不能为空白 |
| coverImg | 封面图像地址 | string | 是 | 必须是合法的 URL 地址 |
| state | 发布状态 | string | 是 | `已发布` \| `草稿` |
| categoryId | 文章分类ID | number | 是 | 必须是当前用户自己的分类 |

```json
{
    "title": "北京旅游攻略",
    "content": "天安门,颐和园,鸟巢,长城...爱去哪去哪...",
    "coverImg": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/9bf1cf5b-1420-4c1b-91ad-e0f4631cbed4.png",
    "state": "已发布",
    "categoryId": 2
}
```

**成功响应**：`200 OK`，`data` 为更新后的文章（结构同 4.3）。

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 400 | 参数不合法（含请求体无法解析，如 `state` 不是合法取值） |
| 404 | 文章不存在或不属于当前用户（`msg`: `文章不存在`）；或 `categoryId` 对应的分类不存在/不属于当前用户（`msg`: `分类不存在`） |

---

### 4.5 删除文章

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

## 5. 其他接口

### 5.1 文件上传

> **`POST /files`**
>
> 该接口用于上传单个文件，文件以 `UUID + 原扩展名` 重命名后存入阿里云 OSS。

**请求格式**：`multipart/form-data`

| 参数名称 | 说明 | 类型 | 是否必须 | 备注 |
| -------- | ---- | ---- | -------- | ---- |
| file | 表单中文件字段的名字 | file | 是 | 文件名必须带扩展名（如 `.png`） |

**成功响应**：`200 OK`（注意不是 201）

| 名称 | 类型 | 说明 |
| ---- | ---- | ---- |
| url | string | 文件在阿里云上的存储地址 |

```json
{
    "code": 200,
    "msg": "success",
    "data": {
        "url": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/b5811871-acc8-4583-8399-cf0dc73591ab.png"
    }
}
```

**失败情形**

| 状态码 | 场景 |
| ------ | ---- |
| 500 | 未携带文件、文件名无扩展名，或上传 OSS 失败（当前未做专门校验，统一走 500） |

---

## 附录：与理想 RESTful 风格的已知差异

以下是当前实现中不完全统一的地方，前端对接时需要注意：

| 项 | 现状 |
| -- | ---- |
| 注册、登录 | 参数走 Query/表单而非 JSON，路径为 `/auth/register`、`/auth/login`，不是 `/users`、`/sessions` |
| 成功状态码 | 上传文件、更新头像返回 `200`；新增文章返回 `201` 但无 `Result` 包装 |
| 响应包装 | `Result.code` 成功时恒为 `200`，与 HTTP 状态码 201 并不相同；错误信息字段名是 `msg` |
| 文件上传 | 参数缺失/异常统一 500 |
| 时间格式 | ISO-8601（`yyyy-MM-ddTHH:mm:ss`），未按 `yyyy-MM-dd HH:mm:ss` 格式化 |
