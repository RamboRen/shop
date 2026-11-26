# ============================================================
# 商城后台管理系统 - 容器镜像
# 多阶段构建：Maven 编译 -> 精简 JRE 运行
# 用法：docker compose build app  或  docker build -t shop-admin:local .
# ============================================================

# ---------- 阶段一：编译打包 ----------
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build

# 先复制 pom 预热依赖缓存，源码变动时无需重新下载依赖
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline || true

COPY src ./src
RUN mvn -B -q -DskipTests package

# ---------- 阶段二：运行时 ----------
FROM eclipse-temurin:17-jre-jammy

LABEL org.opencontainers.image.title="shop-admin" \
      org.opencontainers.image.description="商城后台管理系统后端服务" \
      org.opencontainers.image.vendor="shop-endback"

ENV TZ=Asia/Shanghai

# curl 用于容器健康检查；创建非 root 用户运行，降低权限风险
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl tzdata \
 && rm -rf /var/lib/apt/lists/* \
 && groupadd --system app \
 && useradd --system --gid app --home-dir /app --shell /usr/sbin/nologin app \
 && mkdir -p /app/uploads \
 && chown -R app:app /app

WORKDIR /app

COPY --from=builder /build/target/shop-admin-*.jar /app/app.jar
RUN chown app:app /app/app.jar

USER app

EXPOSE 34369

HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=5 \
    CMD curl -fsS http://127.0.0.1:34369/actuator/health || exit 1

# exec 形式确保 SIGTERM 直接传给 JVM，配合 server.shutdown=graceful 优雅停机
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS:-} -Duser.timezone=Asia/Shanghai -jar /app/app.jar"]
