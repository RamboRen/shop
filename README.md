# 商城后台管理系统（后端）

基于 **Spring Boot 3 + MyBatis-Plus + Spring Security + JWT + Swagger(OpenAPI 3)** 的商城后台管理后端项目，使用 **JDK 17**，仅包含后端 API，已配置全局跨域。

## 1. 功能概览

| 模块 | 能力 |
| --- | --- |
| 管理员认证 | 账号密码登录、JWT 鉴权、获取当前管理员、修改密码、退出；登录返回动态菜单与权限 |
| 动态菜单权限 | 菜单树维护、角色菜单分配、菜单显示/隐藏、接口按菜单权限动态鉴权 |
| 管理员管理 | 分页查询、新增、修改、删除、启用/禁用、重置密码；需要 `system:admin` 权限 |
| 后台数据统计 | 概览指标、7~90 天销售趋势、分类商品分布、热销商品 |
| 商品管理 | 商品分页与筛选、详情、新增、修改、删除、单个/批量上下架 |
| 订单管理 | 订单分页与筛选、详情、状态流转、已完成/已取消订单删除 |
| 用户管理 | 商城会员分页与筛选、详情（含订单统计）、新增、修改、删除、启停 |
| 系统设置 | 站点基础信息、联系方式、交易与支付配置；按分组维护、批量保存、配置映射查询；需要 `system:setting` 权限 |
| 商品规格 | 规格名称与规格值维护、分页查询、启用/禁用、排序 |
| 优惠券管理 | 满减券/折扣券 CRUD、发行量与领取/使用数据、有效期、启用/禁用 |
| 商品分类 | 分类列表、详情、新增、修改、删除；存在商品时禁止删除 |
| 图库管理 | 图片上传、分页查询、修改名称、单张删除、批量删除、静态图片访问 |
| 图库分类 | 分类列表/详情、新增、修改、删除、启停；编码唯一且创建后不可改，存在图片时禁止删除；图片按分类归档筛选 |

## 2. 技术栈

| 组件 | 版本 |
| --- | --- |
| JDK | 17 |
| Spring Boot | 3.2.5 |
| Spring Security | 6.2.x（随 Spring Boot） |
| MyBatis-Plus | 3.5.7 |
| Swagger / OpenAPI | springdoc-openapi 2.5.0 |
| JWT | jjwt 0.12.5 |
| MySQL | 8.0+ |
| 构建工具 | Maven 3.9+ |
| 测试 | JUnit 5、MockMvc、H2 |

## 3. 项目结构

```text
shop-endback
├── pom.xml
├── deploy.sh                          # 一键 Docker 部署脚本（Linux）
├── Dockerfile                         # 应用镜像：Maven 构建 + JRE 运行
├── .dockerignore                      # 镜像构建上下文排除项
├── .env.example                       # 部署配置模板（端口/密码/密钥/CORS）
├── docker-compose.yml                 # MySQL + 应用编排，数据卷持久化
├── sql/shop_admin.sql                 # 建库、建表、演示数据、默认账号
├── sql/upgrade_menu_permission.sql    # 已有 v1.0 数据库的菜单权限增量升级
├── sql/upgrade_product_menus.sql      # 已有 v1.1 数据库的商品子菜单增量升级
├── sql/upgrade_spec_coupon.sql        # 已有 v1.1/v1.2 数据库的规格、优惠券表与接口升级
├── sql/upgrade_super_admin_menus.sql  # 超级管理员全量菜单权限修复
├── sql/upgrade_order_management.sql   # 已有 v1.3.1 数据库的订单字段、明细表与菜单升级
├── sql/upgrade_user_management.sql    # 已有 v1.4.0 数据库的用户表、菜单与权限升级
├── sql/upgrade_system_setting.sql     # 已有 v1.5.0 数据库的系统设置表、菜单与权限升级
├── sql/upgrade_gallery_category.sql   # 已有数据库的图库分类表与默认分类升级
├── uploads/demo/                      # 演示商品图片
└── src
    ├── main/java/com/shop/admin
    │   ├── admin/                     # 管理员管理
    │   ├── auth/                      # 登录认证
    │   ├── gallery/                   # 图库管理与分类（category 子包）
    │   ├── coupon/                    # 优惠券管理
    │   ├── menu/                      # 菜单、角色菜单与动态权限
    │   ├── order/                     # 订单与订单明细
    │   ├── user/                      # 商城用户（会员）
    │   ├── setting/                   # 系统设置（配置项与分组）
    │   ├── product/                   # 商品与分类
    │   ├── spec/                      # 商品规格
    │   ├── stats/                     # 数据统计
    │   ├── common/                    # 统一响应、异常、枚举、常量
    │   ├── config/                    # Security、CORS、MyBatis-Plus、Swagger、上传等
    │   └── security/                  # JWT 过滤器、认证主体、安全工具
    ├── main/resources
    │   ├── application.yml
    │   └── mapper/StatsMapper.xml
    └── test                           # H2 集成测试
```

## 4. 快速启动

### 4.1 准备环境

- JDK 17（`java -version` 应显示 17.x）
- Maven 3.9+
- MySQL 8.0+

### 4.2 初始化数据库

方式一：使用本机 MySQL

```bash
mysql -uroot -p < sql/shop_admin.sql
```

方式二：使用一键 Docker 部署脚本（已安装 Docker 时，见 4.6）

```bash
./deploy.sh
```

脚本会自动创建 MySQL 容器与 `shop_admin` 数据库、执行初始化 SQL 并启动应用，详见 4.6。

如果已有 v1.0 数据库且需要保留原数据，请先备份，再执行菜单权限增量脚本：

```bash
mysql -uroot -p < sql/upgrade_menu_permission.sql
mysql -uroot -p < sql/upgrade_spec_coupon.sql
```

如果已有 v1.1/v1.2 数据库，执行规格与优惠券升级脚本即可，脚本会补充数据表和两个商品子菜单：

```bash
mysql -uroot -p < sql/upgrade_spec_coupon.sql
```

如果已有 v1.3.0 数据库且只需修复超级管理员菜单关联，可单独执行：

```bash
mysql -uroot -p < sql/upgrade_super_admin_menus.sql
```

如果已有 v1.3.1 数据库，执行订单管理升级脚本：

```bash
mysql -uroot -p < sql/upgrade_order_management.sql
```

如果已有 v1.4.0 数据库，执行用户管理升级脚本：

```bash
mysql -uroot -p < sql/upgrade_user_management.sql
```

如果已有 v1.5.0 数据库，执行系统设置升级脚本：

```bash
mysql -uroot -p < sql/upgrade_system_setting.sql
```

如果已有数据库需要新增图库分类（图片改由分类归档），执行图库分类升级脚本：

```bash
mysql -uroot -p < sql/upgrade_gallery_category.sql
```

> 升级脚本均可重复执行，不会覆盖已有业务数据；`sql/shop_admin.sql` 会先删除同名业务表，仅建议在全新环境或允许重置数据的环境执行。

### 4.3 修改数据库连接（可选）

默认连接为 `127.0.0.1:3306/shop_admin`，用户名/密码为 `root/root`。可通过环境变量覆盖：

```bash
# Windows PowerShell 示例
$env:DB_HOST="127.0.0.1"
$env:DB_PORT="3306"
$env:DB_NAME="shop_admin"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your-password"
```

### 4.4 启动项目

```bash
mvn spring-boot:run
```

或先打包再运行：

```bash
mvn clean package
java -jar target/shop-admin-1.4.0.jar
```

启动后：

- 接口地址：`http://localhost:34369`
- Swagger UI：`http://localhost:34369/swagger-ui.html`
- OpenAPI JSON：`http://localhost:34369/v3/api-docs`
- 健康检查：`http://localhost:34369/actuator/health`
- 上传图片访问：`http://localhost:34369/uploads/...`

### 4.5 默认账号

| 用户名 | 密码 | 角色 | 权限 |
| --- | --- | --- | --- |
| `admin` | `admin123` | 超级管理员 | 全部菜单与功能 |
| `operator` | `admin123` | 普通管理员 | 数据概览、商品列表、商品分类、规格管理、优惠券管理、图库、订单管理、用户管理；不含系统管理 |

> 首次启动后请立即修改默认密码。

### 4.6 一键 Docker 部署（服务器推荐）

把**完整项目目录**（必须包含 `pom.xml` 与 `src/`，镜像从源码构建）上传到 Linux 服务器，在项目根目录执行：

```bash
chmod +x deploy.sh
./deploy.sh
```

脚本自动完成：环境检查 → 生成 `.env`（首次自动生成随机 JWT 密钥与数据库密码）→ 多阶段构建应用镜像 → 启动 MySQL 与应用 → 等待健康检查 → 输出访问地址。

> **构建加速**：镜像构建时 Maven 依赖走阿里云仓库（`maven.aliyun.com/repository/public`），运行时 apt 包走阿里云 Ubuntu 源（`mirrors.aliyun.com`）；两者都写在 `Dockerfile` 里，无需额外配置。基础镜像仍从 Docker Hub 拉取，可在服务器上配置 Docker 镜像加速器。若服务器无法访问阿里云，删除 `Dockerfile` 中对应的 settings.xml 与 sed 替换即可回退官方源。

> **重要**：镜像由当前源码构建，包含全部最新功能。服务器上旧的 `shop-admin-1.4.0.jar` 不含用户管理、系统设置、订单统计、图库分类等新增功能，请勿用旧 jar 代替源码。

**前置条件**：Docker Engine 20.10+ 与 Docker Compose v2（或 docker-compose v1）；服务器可访问 Docker Hub、阿里云 Maven 仓库与阿里云 Ubuntu 镜像站。首次构建需下载依赖，约几分钟。应用端口默认 `34369`，MySQL 对宿主机暴露 `3306`。

**配置**：首次运行生成 `.env`，按需修改其中 `APP_PORT`、`MYSQL_ROOT_PASSWORD`、`JWT_SECRET`、`CORS_ALLOWED_ORIGINS`、`HEALTH_TIMEOUT` 后重启生效；模板见 `.env.example`。

> **纯一键脚本**：`deploy.sh` 只接受无参数运行，重复执行会重新构建并更新容器（数据卷保留）；需要查看日志、重启、停止等操作时请用原生 `docker compose` 命令，例如 `docker compose logs -f app`、`docker compose ps`、`docker compose restart`、`docker compose stop`、`docker compose down`（移除容器但保留数据卷）。

**数据安全**：数据库与上传图片保存在命名卷 `shop-admin-mysql-data`、`shop-admin-uploads`；`docker compose down` 不删除卷。`sql/shop_admin.sql` 仅在数据卷首次初始化时执行，不会覆盖已有数据；升级已有库请按 4.2 的增量脚本逐个执行。

也可不用脚本：先 `cp .env.example .env` 并填好密钥，再执行 `docker compose up -d --build`。

## 5. 配置说明

主要配置位于 `src/main/resources/application.yml`。

| 环境变量 | 默认值 | 说明 |
| --- | --- | --- |
| `SERVER_PORT` | `34369` | 服务端口 |
| `DB_HOST` | `127.0.0.1` | MySQL 地址 |
| `DB_PORT` | `3306` | MySQL 端口 |
| `DB_NAME` | `shop_admin` | 数据库名 |
| `DB_URL` | 由上述参数拼接 | 完整 JDBC URL，设置后优先生效 |
| `DB_USERNAME` | `root` | 数据库用户 |
| `DB_PASSWORD` | `root` | 数据库密码 |
| `JWT_SECRET` | 内置开发密钥 | JWT 签名密钥，**生产必须替换，长度至少 32 字符** |
| `JWT_EXPIRATION_MINUTES` | `120` | 令牌有效期（分钟） |
| `CORS_ALLOWED_ORIGINS` | 本地 5173/3000 | 允许跨域的前端来源，多个用英文逗号分隔 |
| `UPLOAD_PATH` | `./uploads` | 图片本地存储根目录 |

跨域默认允许：

- 来源：`http://localhost:5173`、`http://127.0.0.1:5173`、`http://localhost:3000`、`http://127.0.0.1:3000`
- 方法：`GET/POST/PUT/DELETE/PATCH/OPTIONS`
- 请求头：全部
- 允许携带认证信息：是
- 预检缓存：3600 秒

如前端地址不同，请通过 `CORS_ALLOWED_ORIGINS` 修改。允许携带认证信息时不能配置为 `*`。

## 6. 认证与响应约定

### 6.1 登录

```http
POST /api/admin/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

响应中的 `data.token` 为 JWT 访问令牌，`data.menus` 为当前角色可显示的菜单树，`data.permissions` 为当前角色的接口权限编码。后续请求统一携带：

```http
Authorization: Bearer eyJhbGciOi...
```

登录响应示例（省略管理员字段）：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOi...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "menus": [
      {
        "id": 2,
        "parentId": 0,
        "menuName": "商品管理",
        "menuType": "CATALOG",
        "routePath": "/product",
        "component": "Layout",
        "children": [
          {
            "id": 3,
            "menuName": "商品列表",
            "menuType": "MENU",
            "routePath": "/product/list",
            "component": "product/list/index",
            "permissionCode": "product:view"
          }
        ]
      }
    ],
    "permissions": ["product:category", "product:view", "stats:view"]
  }
}
```

### 6.2 统一响应

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

- 成功：HTTP 200，`code = 200`
- 参数错误：HTTP 400，`code = 400`
- 未登录/令牌失效：HTTP 401，`code = 401`
- 无权限：HTTP 403，`code = 403`
- 资源不存在：HTTP 404，`code = 404`
- 唯一键冲突：HTTP 409，`code = 409`
- 系统异常：HTTP 500，`code = 500`

分页响应 `data`：

```json
{
  "total": 12,
  "pages": 2,
  "pageNum": 1,
  "pageSize": 10,
  "records": []
}
```

### 6.3 退出登录

JWT 为无状态令牌，`POST /api/admin/auth/logout` 会清理当前请求的安全上下文，但不会在服务端吊销已签发的令牌。前端退出时必须同步删除本地令牌。

### 6.4 动态菜单与权限控制

菜单权限数据来自 `sys_menu`（菜单定义）与 `sys_role_menu`（角色可访问菜单）。管理员每次请求都会重新加载角色菜单权限，因此修改角色菜单后无需重新登录即可生效。

登录返回的 `menus`、`permissions` 可直接用于前端：

- 侧边栏/路由菜单：按 `menus` 树递归渲染，忽略 `visible = 0` 和 `status = 0` 的节点（后端已过滤）。
- 按钮/操作级判断：使用 `permissions.includes('product:view')` 等判断权限编码。
- 刷新页面或切换权限后：调用 `GET /api/admin/auth/menus` 获取最新的菜单树与权限编码。
- 前端路由守卫应同时校验 `routePath` 是否存在于后端返回的菜单树中，不能只依赖隐藏按钮。

后台菜单结构：

```text
数据概览 /dashboard          stats:view
商品管理 /product            —
├── 商品列表   /product/list    product:view
├── 商品分类   /product/category product:category
├── 规格管理   /product/spec    product:spec
└── 优惠券管理 /product/coupon  product:coupon
图库管理 /gallery            gallery:view
订单管理 /order              order:view
用户管理 /user               user:view
系统管理 /system             —
├── 管理员管理 /system/admin   system:admin
├── 菜单权限   /system/menu    system:menu
└── 系统设置   /system/setting system:setting
```

> 规格管理与优惠券管理已提供数据表、分页 CRUD、状态切换接口，接口权限分别为 `product:spec`、`product:coupon`。图库分类管理为图库的子功能，不单独占菜单，复用 `gallery:view` 权限。

内置权限编码：

| 权限编码 | 对应菜单/接口 |
| --- | --- |
| `stats:view` | 数据概览、统计接口 |
| `product:view` | 商品列表、商品接口 |
| `product:category` | 商品分类、分类接口 |
| `product:spec` | 规格管理菜单、商品规格接口 |
| `product:coupon` | 优惠券管理菜单、优惠券接口 |
| `gallery:view` | 图库管理、图库接口、图库分类接口 |
| `order:view` | 订单管理、订单接口 |
| `user:view` | 用户管理、用户接口 |
| `system:admin` | 管理员管理、管理员接口 |
| `system:menu` | 菜单权限、菜单维护接口；角色菜单分配仅超级管理员 |
| `system:setting` | 系统设置菜单、系统设置接口（默认仅超级管理员） |

规则：

- `SUPER_ADMIN` 固定拥有全部菜单，不需要也不能通过角色菜单接口重新分配；初始化、新增或修改菜单时会自动补齐其 `sys_role_menu` 关联，保证超级管理员始终拥有全部菜单权限。
- 普通角色默认只分配数据概览、商品目录/列表/分类/规格/优惠券、图库、订单、用户管理；超级管理员可在“菜单权限”页动态调整角色菜单。
- 菜单有 `CATALOG`（目录）和 `MENU`（页面）两种类型；`MENU` 类型必须配置权限编码，目录不能配置权限编码。
- 权限编码同时用于接口鉴权：`product:view` 控制商品接口，`order:view` 控制订单接口，`system:admin` 控制管理员接口，`system:menu` 控制菜单维护接口；角色菜单分配接口额外限制为仅超级管理员可调用，`SUPER_ADMIN` 始终放行。
- 给角色分配子菜单时，后端会自动补充其父目录，避免菜单树出现孤立节点。
- `visible = 0` 只控制菜单是否显示，不回收接口权限；`status = 0` 会同时禁用菜单显示与接口权限。
- 内置菜单的权限编码与后端接口约定绑定，修改后需同步调整前端菜单与接口权限约定，避免出现“菜单可见但接口 403”。

## 7. API 一览

### 7.1 认证

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/admin/auth/login` | 管理员登录（公开），返回令牌、菜单树与权限 |
| GET | `/api/admin/auth/me` | 当前管理员信息 |
| GET | `/api/admin/auth/menus` | 当前管理员菜单树与权限编码 |
| PUT | `/api/admin/auth/password` | 修改当前密码 |
| POST | `/api/admin/auth/logout` | 退出登录 |

### 7.2 管理员管理（`system:admin`）

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/admins` | 分页查询，支持 `keyword/roleCode/status` |
| GET | `/api/admin/admins/{id}` | 管理员详情 |
| POST | `/api/admin/admins` | 新增管理员 |
| PUT | `/api/admin/admins/{id}` | 修改管理员 |
| DELETE | `/api/admin/admins/{id}` | 删除管理员 |
| PUT | `/api/admin/admins/{id}/status` | 启用/禁用 |
| PUT | `/api/admin/admins/{id}/password` | 重置密码 |

安全约束：不能删除/禁用当前登录账号；不能修改当前账号角色；系统必须至少保留一名启用的超级管理员。

### 7.3 数据统计

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/stats/overview` | 概览：销售额、订单、商品、图库、管理员、低库存等 |
| GET | `/api/admin/stats/sales-trend?days=7` | 销售趋势，`days` 取值 7~90，自动补零 |
| GET | `/api/admin/stats/category-distribution` | 分类商品分布 |
| GET | `/api/admin/stats/top-products?limit=5` | 热销商品，`limit` 取值 3~20 |
| GET | `/api/admin/stats/orders?startDate=2026-11-01&endDate=2026-11-30` | 订单统计：核心指标、各状态分布、支付方式分布；日期可选，`startDate`/`endDate` 格式 `yyyy-MM-dd` |

统计中的交易指标来自 `shop_order`；订单管理 API 见 7.10。订单统计的"有效订单"口径为待发货/待收货/已完成（状态 1/2/3），核心指标含订单总数、有效订单数、商品总额、应付金额与客单价；`statusDistribution` 固定返回全部 5 种状态（无数据的状态补零），`payTypeDistribution` 按支付方式聚合，未付款订单归入 `UNKNOWN`（名称"未付款"）。开始日期晚于结束日期返回 `400`。

演示库 `sql/shop_admin.sql` 与测试库 `src/test/resources/data-test.sql` 均内置了覆盖**近24小时、近一周、近一个月（含超过一周区间）及一个月范围外**的订单，日期按运行时相对时间生成（MySQL 用 `NOW()`、H2 用 `CURRENT_TIMESTAMP` 加减天数），便于直接验证按日期区间的分层统计效果；不传日期参数则统计全部订单。

### 7.4 商品

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/products` | 分页查询，支持 `keyword/categoryId/status/minPrice/maxPrice` |
| GET | `/api/admin/products/{id}` | 商品详情 |
| POST | `/api/admin/products` | 新增商品 |
| PUT | `/api/admin/products/{id}` | 修改商品 |
| DELETE | `/api/admin/products/{id}` | 删除商品（逻辑删除） |
| PUT | `/api/admin/products/{id}/status` | 单个上下架 |
| PUT | `/api/admin/products/batch-status` | 批量上下架，单次最多 50 个 |

商品状态：`0` 下架，`1` 上架。新增时商品编码留空会自动生成。

### 7.5 商品分类

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/product-categories?status=` | 分类列表 |
| GET | `/api/admin/product-categories/{id}` | 分类详情 |
| POST | `/api/admin/product-categories` | 新增分类 |
| PUT | `/api/admin/product-categories/{id}` | 修改分类 |
| DELETE | `/api/admin/product-categories/{id}` | 删除分类，存在商品时禁止 |

### 7.6 图库

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/admin/gallery/images` | 上传图片，`multipart/form-data`，支持 `categoryCode` 分类属性 |
| GET | `/api/admin/gallery/images` | 分页查询，支持 `keyword/imageGroup` |
| GET | `/api/admin/gallery/images/category/{categoryCode}` | 按分类查询图片（分页），支持 `keyword/pageNum/pageSize` |
| PUT | `/api/admin/gallery/images/{id}/name` | 修改图片名称（仅展示名，URL 不变） |
| DELETE | `/api/admin/gallery/images/{id}` | 删除图片及本地文件 |
| DELETE | `/api/admin/gallery/images/batch` | 批量删除，单次最多 50 张 |

两种按分类查询方式的区别：

- `GET /api/admin/gallery/images?imageGroup=编码`：只接受**已启用**分类，禁用分类返回 `400`，分类名只从启用分类映射读取。
- `GET /api/admin/gallery/images/category/{categoryCode}`：按路径中的分类编码查询，**允许查询已禁用分类下的历史图片**，且分类名不会退化成编码；分类不存在返回 `404`。此接口会忽略查询参数中的 `imageGroup`，以路径编码为准。

图库分类接口（复用 `gallery:view` 权限）：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/gallery/categories` | 分类列表（含各分类图片数量），支持 `status` 筛选 |
| GET | `/api/admin/gallery/categories/{id}` | 分类详情 |
| POST | `/api/admin/gallery/categories` | 新增分类 |
| PUT | `/api/admin/gallery/categories/{id}` | 修改分类（编码不可改） |
| DELETE | `/api/admin/gallery/categories/{id}` | 删除分类，分类下存在图片时禁止 |

新增分类请求字段：`categoryCode`（大写字母开头，仅大写字母/数字/下划线，2~30 位，唯一）、`categoryName`（2~30 位，唯一）、`sort`、`status`、`remark`。修改请求不含 `categoryCode`——编码是图片与分类的关联键，创建后不可更改。内置 4 个分类（商品图/轮播图/头像/其他）可改名、排序、启停；禁用后不能再上传或按该分类筛选。

修改名称：`PUT /api/admin/gallery/images/{id}/name`，请求体 `{"originalName":"首页主图.png"}`。`originalName` 必填、最长 255 位，会自动去掉路径前缀与控制字符；**只改展示名称**，服务器存储文件名（`fileName`）和访问地址（`url`）不变，改后按新名称关键字即可搜到。图片不存在返回 `404`。

上传参数：

- `file`：图片文件，必填
- `categoryCode`：图库分类编码，须为**已启用**的分类编码（内置 `PRODUCT` / `BANNER` / `AVATAR` / `OTHER`，或自定义分类），不存在或已禁用返回 `400`；不传时默认 `OTHER`

上传限制：

- 仅支持 JPG、PNG、GIF，最大 10MB
- 通过 `ImageIO` 校验真实图片格式和尺寸，不依赖文件扩展名
- 使用 UUID 重命名，按 `yyyy/MM/dd` 分目录存储
- 图片尺寸上限 10000 × 10000，像素总量上限 4000 万
- 对外访问路径 `/uploads/**`，Spring Security 已放行

### 7.7 菜单与角色权限（`system:menu`）

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/menus` | 全部菜单平铺列表，含隐藏/禁用菜单 |
| GET | `/api/admin/menus/tree` | 全部菜单树，含隐藏/禁用菜单 |
| POST | `/api/admin/menus` | 新增菜单 |
| PUT | `/api/admin/menus/{id}` | 修改菜单 |
| DELETE | `/api/admin/menus/{id}` | 删除菜单，存在子菜单时禁止 |
| GET | `/api/admin/menus/roles/{roleCode}` | 查询角色已分配菜单（仅超级管理员；`SUPER_ADMIN` 返回全部菜单） |
| PUT | `/api/admin/menus/roles/{roleCode}` | 分配角色菜单（仅超级管理员），`roleCode` 取 `ADMIN` |

分配角色菜单请求：

```json
{
  "menuIds": [1, 2, 3, 4, 5]
}
```

新增菜单请求示例：

```json
{
  "parentId": 6,
  "menuName": "操作日志",
  "menuType": "MENU",
  "routePath": "/system/log",
  "component": "system/log/index",
  "icon": "Document",
  "permissionCode": "system:log",
  "sort": 3,
  "visible": 1,
  "status": 1
}
```

`SUPER_ADMIN` 不能通过该接口重新分配，固定拥有全部菜单；角色菜单分配接口仅超级管理员可调用，普通角色未分配菜单时，`menus` 与 `permissions` 均为空，对应接口返回 403。

### 7.8 商品规格（`product:spec`）

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/product-specs` | 分页查询，支持 `keyword/status` |
| GET | `/api/admin/product-specs/{id}` | 规格详情 |
| POST | `/api/admin/product-specs` | 新增规格 |
| PUT | `/api/admin/product-specs/{id}` | 修改规格 |
| DELETE | `/api/admin/product-specs/{id}` | 删除规格 |
| PUT | `/api/admin/product-specs/{id}/status` | 启用/禁用规格 |

新增规格请求：

```json
{
  "name": "颜色",
  "values": ["黑色", "白色", "蓝色"],
  "status": 1,
  "sort": 1,
  "remark": "商品颜色规格"
}
```

- `name` 唯一，长度不超过 50 位。
- `values` 至少 1 个、最多 20 个，单个值不超过 50 位且不能重复。
- `status` 为 `0` 禁用、`1` 启用。

### 7.9 优惠券（`product:coupon`）

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/coupons` | 分页查询，支持 `keyword/couponType/status` |
| GET | `/api/admin/coupons/{id}` | 优惠券详情 |
| POST | `/api/admin/coupons` | 新增优惠券 |
| PUT | `/api/admin/coupons/{id}` | 修改优惠券 |
| DELETE | `/api/admin/coupons/{id}` | 删除优惠券 |
| PUT | `/api/admin/coupons/{id}/status` | 启用/禁用优惠券 |

新增优惠券请求：

```json
{
  "couponCode": "CP20260004",
  "name": "双十一满减券",
  "couponType": "AMOUNT",
  "discountValue": 30.00,
  "thresholdAmount": 199.00,
  "totalCount": 500,
  "validStartTime": "2026-11-01 00:00:00",
  "validEndTime": "2026-11-15 23:59:59",
  "status": 1,
  "description": "满 199 元减 30 元"
}
```

- `couponType`：`AMOUNT` 满减券，`DISCOUNT` 折扣券。
- `discountValue`：满减券为减免金额；折扣券为折扣值，`8.5` 表示 8.5 折。
- `thresholdAmount`：使用门槛，`0` 表示无门槛；满减券的减免金额不能大于使用门槛。
- `validStartTime`、`validEndTime` 格式为 `yyyy-MM-dd HH:mm:ss`，开始时间不能晚于结束时间。
- `couponCode` 留空时由系统生成；修改接口不允许修改编码。
- `receivedCount`、`usedCount` 由后续领券/核销业务维护，当前管理接口只读；修改发行总量时不能小于已领取数量。
- 响应包含 `remainingCount`（剩余数量）、`expired`（是否已过期）以及 `couponTypeName`、`statusName`。

### 7.10 订单管理（`order:view`）

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/orders` | 分页查询，支持 `keyword/status/startDate/endDate` |
| GET | `/api/admin/orders/{id}` | 订单详情，包含商品明细 |
| PUT | `/api/admin/orders/{id}/status` | 订单状态流转 |
| DELETE | `/api/admin/orders/{id}` | 删除订单，仅限已完成或已取消 |

查询参数：

- `keyword`：订单号、收货人或收货手机号
- `status`：`0` 待付款，`1` 待发货，`2` 待收货，`3` 已完成，`4` 已取消
- `startDate`、`endDate`：创建日期范围，格式 `yyyy-MM-dd`

状态流转规则：

| 当前状态 | 允许变更 |
| --- | --- |
| 0 待付款 | 1 待发货、4 已取消 |
| 1 待发货 | 2 待收货、4 已取消 |
| 2 待收货 | 3 已完成 |
| 3 已完成 / 4 已取消 | 不允许继续变更 |

状态变更请求：

```json
{
  "status": 2
}
```

修改状态时自动记录 `payTime`、`shipTime`、`finishTime`、`cancelTime`。状态更新以读取时的状态作为条件，若期间被其他操作修改则返回 `409`（需刷新后重试），避免并发覆盖。删除接口只允许删除已完成或已取消订单，并同步逻辑删除订单明细。

### 7.11 用户管理（`user:view`）

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/users` | 分页查询，支持 `keyword/gender/status/startDate/endDate` |
| GET | `/api/admin/users/{id}` | 用户详情，包含订单数量与累计消费 |
| POST | `/api/admin/users` | 新增用户，用户编号自动生成 |
| PUT | `/api/admin/users/{id}` | 修改用户，登录账号不允许修改 |
| DELETE | `/api/admin/users/{id}` | 删除用户 |
| PUT | `/api/admin/users/{id}/status` | 启用/禁用用户 |

查询参数：

- `keyword`：用户编号、登录账号、昵称或手机号
- `gender`：`MALE` 男、`FEMALE` 女、`UNKNOWN` 未知
- `status`：`0` 禁用，`1` 启用
- `startDate`、`endDate`：注册日期范围，格式 `yyyy-MM-dd`

用户详情中的 `orderCount` 为该用户全部订单数，`totalSpent` 为待发货、待收货、已完成订单的应付金额合计。登录账号与手机号必须唯一，重复返回 `409`；状态更新同样带并发条件，冲突返回 `409`。

### 7.12 系统设置（`system:setting`）

系统设置以 key-value 配置项形式存储，按分组维护，默认仅超级管理员可访问。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/settings` | 分页查询，支持 `keyword/configGroup` |
| GET | `/api/admin/settings/groups` | 查询全部分组及其配置项，供分组编辑页使用 |
| GET | `/api/admin/settings/map` | 返回全部配置的 key-value 映射，供其他模块读取 |
| GET | `/api/admin/settings/{id}` | 设置项详情 |
| POST | `/api/admin/settings` | 新增设置项，配置键需唯一 |
| PUT | `/api/admin/settings/{id}` | 修改设置项，配置键不允许修改 |
| DELETE | `/api/admin/settings/{id}` | 删除设置项 |
| PUT | `/api/admin/settings/batch` | 按分组批量保存配置项的值 |

查询参数：

- `keyword`：配置键或配置名称
- `configGroup`：`BASIC` 基础设置、`CONTACT` 联系方式、`TRADE` 交易设置、`PAYMENT` 支付设置

配置项字段包含 `configKey`（全局唯一）、`configValue`、`configGroup`、`configName`、`valueType`（`STRING`/`TEXT`/`NUMBER`/`BOOLEAN`）、`sort`、`remark`。批量保存时校验每个配置键已存在且归属于请求指定分组，否则返回 `400`/`404`；配置键重复新增返回 `409`。预置了站点名称、客服电话、默认运费、支付方式等 13 项默认配置。

### 7.13 数据表

| 表名 | 说明 | 关键字段 |
| --- | --- | --- |
| `shop_product_spec` | 商品规格字典 | `name`（唯一）、`spec_values`（JSON 数组）、`status`、`sort`、`remark`、审计字段、`deleted` |
| `shop_coupon` | 优惠券 | `coupon_code`（唯一）、`name`、`coupon_type`、`discount_value`、`threshold_amount`、`total_count`、`received_count`、`used_count`、`valid_start_time`、`valid_end_time`、`status`、`description`、审计字段、`deleted` |
| `shop_order` | 订单主表 | `order_no`（唯一）、收货信息、商品总额/运费/优惠/应付金额、支付方式、状态、商品数量、支付/发货/完成/取消时间、备注、审计字段、`deleted` |
| `shop_order_item` | 订单明细 | `order_id`、`product_id`、商品名称/图片快照、单价、数量、小计、审计字段、`deleted` |
| `shop_user` | 商城用户 | `user_no`（唯一）、`username`（唯一）、`phone`（唯一）、昵称、头像、性别、出生日期、状态、最近登录时间、审计字段、`deleted` |
| `sys_setting` | 系统设置 | `config_key`（唯一）、`config_value`、`config_group`、`config_name`、`value_type`、`sort`、`remark`、审计字段、`deleted` |
| `shop_gallery_category` | 图库分类 | `category_code`（唯一）、`category_name`（唯一）、`sort`、`status`、`remark`、审计字段、`deleted` |

表均使用逻辑删除；规格按 `status + sort` 建立索引，优惠券按 `coupon_type + status` 与 `valid_end_time` 建立索引，订单按 `status + create_time`、收货手机号和会员编号建立索引，用户按 `status + create_time` 建立索引，系统设置按 `config_key` 唯一索引与 `config_group + sort` 建立索引，图库分类按 `category_code` 唯一索引与 `status + sort` 建立索引。图片通过 `image_group`（分类编码）关联分类，分类编码创建后不可修改。完整 DDL 与演示数据见 `sql/shop_admin.sql`，增量升级见 `sql/upgrade_spec_coupon.sql`、`sql/upgrade_order_management.sql`、`sql/upgrade_user_management.sql`、`sql/upgrade_system_setting.sql`、`sql/upgrade_gallery_category.sql`。

## 8. 测试

```bash
mvn test
```

测试使用 H2（MySQL 兼容模式）与 Mockito，当前共 61 个用例，覆盖：

- 登录成功/失败、JWT 未登录拦截、权限不足 403
- 动态菜单：角色菜单过滤、角色菜单分配即时生效、子菜单自动补父级、菜单新增/删除
- 超级管理员：全量菜单权限、新建菜单自动关联、禁止重新分配
- 跨域预检、OpenAPI 文档
- 管理员新增、禁用、重置密码、删除
- 商品新增、查询、修改、上下架、删除、参数校验
- 商品规格新增、查询、修改、启停、删除、重复校验
- 优惠券新增、查询、修改、启停、删除、金额/折扣/有效期/发行量校验
- 订单分页、详情、状态流转、非法流转拦截、删除限制、并发状态覆盖拦截
- 用户分页、详情订单统计、新增、修改、启停、删除、账号/手机号唯一校验与权限拦截
- 系统设置分页、分组查询、配置映射、新增/修改/删除、批量保存与分组校验、参数校验、权限拦截
- 图片上传（含 `categoryCode` 分类属性、默认分类、非法分类拦截）、查询、修改名称（URL 不变、关键字可搜、路径清洗、空名称/不存在/未认证）、删除、非法文件拦截
- 图库分类列表/详情、新增、修改、删除、启停、编码/名称唯一校验、存在图片禁止删除、自定义分类上传与按分类筛选、按分类查询图片（含禁用分类历史图片、关键字筛选、分类不存在、权限拦截）
- 统计概览、销售趋势、分类分布、热销商品、订单统计（状态/支付方式分布、日期筛选、参数校验、近24小时/近一周/近一个月时间窗口数据分层）

生产打包：

```bash
mvn clean package
```

## 9. 阿里巴巴 Java 开发手册约定

项目按《阿里巴巴 Java 开发手册》的常见约束组织：

- 分层清晰：`Controller -> Service -> Mapper`，实体使用 `DO`，入参使用 `DTO/Request`，出参使用 `VO`
- 统一响应、统一异常处理、业务异常与参数校验分离
- 枚举替代魔法值，状态值、分组值、低库存阈值等集中定义
- 金额使用 `BigDecimal`，时间使用 `LocalDateTime`
- 数据库字段下划线、Java 属性驼峰，逻辑删除统一 `deleted`
- 写操作使用 `@Transactional(rollbackFor = Exception.class)`
- 公开类、接口、方法提供 Javadoc，命名使用完整业务语义
- 密码 BCrypt 存储，接口不返回密码字段
- 参数使用 Jakarta Validation 校验，文件上传做类型、大小、路径安全校验
- Mapper 使用参数绑定，统计 SQL 集中放在 XML，避免拼接 SQL

## 10. 生产环境注意事项

1. 修改默认管理员密码，删除或禁用演示账号。
2. 通过 `JWT_SECRET` 设置随机且不少于 32 字符的密钥；定期轮换。
3. 将 `CORS_ALLOWED_ORIGINS` 收敛为真实前端域名，使用 HTTPS。
4. 图库默认存储本地磁盘，多实例部署时请替换为对象存储（OSS/S3/MinIO）。
5. 本项目使用逻辑删除；如需物理归档，请配合定期清理任务。
6. `spring.sql.init.mode` 已设为 `never`，生产请使用 Flyway/Liquibase 或运维流程管理数据库变更；升级到本版本时请按需执行 `sql/upgrade_spec_coupon.sql`、`sql/upgrade_order_management.sql`、`sql/upgrade_user_management.sql` 与 `sql/upgrade_system_setting.sql`，创建商品规格/优惠券/订单/用户/系统设置相关表、补充字段并补齐菜单权限。已有 v1.3.0 环境可执行 `sql/upgrade_super_admin_menus.sql` 修复超级管理员的全量菜单关联。
7. 动态菜单权限依赖 `sys_menu` / `sys_role_menu`，请限制 `system:menu` 权限的授予范围，并定期审计角色菜单分配。
8. 建议在网关层增加登录限流、审计日志和异常告警。
