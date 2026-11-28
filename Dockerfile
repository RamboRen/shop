# ============================================================
# 商城后台管理系统 - 容器镜像
# 多阶段构建：Maven 编译 -> 精简 JRE 运行
# 构建加速：Maven 依赖走阿里云仓库，系统包走阿里云 Ubuntu 源
# 用法：docker compose build app  或  docker build -t shop-admin:local .
# ============================================================

# ---------- 阶段一：编译打包 ----------
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build

# 写入阿里云 Maven 镜像配置（public 仓库聚合 Central 与常用插件仓库）
RUN mkdir -p /root/.m2 \
 && printf '%s\n' \
    '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">' \
    '  <mirrors>' \
    '    <mirror>' \
    '      <id>aliyun-public</id>' \
    '      <name>Aliyun Maven Public</name>' \
    '      <url>https://maven.aliyun.com/repository/public</url>' \
    '      <mirrorOf>*</mirrorOf>' \
    '    </mirror>' \
    '  </mirrors>' \
    '</settings>' > /root/.m2/settings.xml

# 先复制 pom 预热依赖缓存，源码变动时无需重新下载依赖
COPY pom.xml .
RUN mvn -B -q -s /root/.m2/settings.xml -DskipTests dependency:go-offline || true

COPY src ./src
RUN mvn -B -q -s /root/.m2/settings.xml -DskipTests package

# ---------- 阶段二：运行时 ----------
FROM eclipse-temurin:17-jre-jammy

LABEL org.opencontainers.image.title="shop-admin" \
      org.opencontainers.image.description="商城后台管理系统后端服务" \
      org.opencontainers.image.vendor="shop-endback"

ENV TZ=Asia/Shanghai

# curl 用于容器健康检查；apt 源切到阿里云加速；创建非 root 用户运行，降低权限风险
# 同时兼容传统 sources.list 与新版 deb822 .sources 两种格式
RUN set -eu; \
    for f in /etc/apt/sources.list /etc/apt/sources.list.d/*.list /etc/apt/sources.list.d/*.sources; do \
        [ -f "$f" ] || continue; \
        sed -i -E 's#https?://(archive|security)\.ubuntu\.com/ubuntu/?#https://mirrors.aliyun.com/ubuntu/#g; s#https?://ports\.ubuntu\.com/ubuntu-ports/?#https://mirrors.aliyun.com/ubuntu-ports/#g' "$f"; \
    done; \
    apt-get update \
 && apt-get install -y --no-install-recommends curl tzdata \
 && rm -rf /var/lib/apt/lists/* \
 && groupadd --system app \
 && useradd --system --gid app --home-dir /app --shell /usr/sbin/nologin app \
 && mkdir -p /app/uploads \
 && chown -R app:app /app

WORKDIR /app

COPY --from=builder /build/target/shop-admin-*.jar /app/app.jar
# 初始化 SQL 引用的 demo 示例图；若使用已存在的上传卷，需在宿主机卷中补入这些文件
COPY uploads/demo /app/uploads/demo
RUN chown -R app:app /app

USER app

EXPOSE 34369

HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=5 \
    CMD curl -fsS http://127.0.0.1:34369/actuator/health || exit 1

# exec 形式确保 SIGTERM 直接传给 JVM，配合 server.shutdown=graceful 优雅停机
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS:-} -Duser.timezone=Asia/Shanghai -jar /app/app.jar"]
