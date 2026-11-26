#!/usr/bin/env bash
# ============================================================
# 商城后台管理系统 - 一键 Docker 部署脚本（Linux）
#
# 用法：
#   chmod +x deploy.sh
#   ./deploy.sh
#
# 运行后自动完成：
#   环境检查 → 生成配置 → 构建镜像 → 启动容器 → 健康检查 → 输出访问信息
#
# 本脚本为纯一键部署，不提供子命令；日常运维请直接使用 docker compose。
# 首次运行会自动创建 .env 并生成随机 JWT 密钥与数据库密码；
# 数据库与应用上传文件保存在 Docker 命名卷中，重建容器不丢数据。
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

ENV_FILE="$SCRIPT_DIR/.env"
ENV_EXAMPLE="$SCRIPT_DIR/.env.example"
MYSQL_CONTAINER="shop-admin-mysql"
APP_CONTAINER="shop-admin-app"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
BLUE='\033[0;34m'; BOLD='\033[1m'; NC='\033[0m'
log_info() { printf '%b\n' "${BLUE}[INFO]${NC} $*"; }
log_ok()   { printf '%b\n' "${GREEN}[ OK ]${NC} $*"; }
log_warn() { printf '%b\n' "${YELLOW}[WARN]${NC} $*"; }
log_err()  { printf '%b\n' "${RED}[ERR ]${NC} $*" >&2; }
die()      { log_err "$*"; exit 1; }

# ---------- 环境检查 ----------
ensure_docker() {
    command -v docker >/dev/null 2>&1 || die "未找到 docker，请先安装 Docker Engine：https://docs.docker.com/engine/install/"
    docker info >/dev/null 2>&1 || die "Docker 守护进程未运行或当前用户无权限。请执行 systemctl start docker，或将当前用户加入 docker 组。"
    if docker compose version >/dev/null 2>&1; then
        COMPOSE=(docker compose)
    elif command -v docker-compose >/dev/null 2>&1; then
        COMPOSE=(docker-compose)
    else
        die "未找到 Docker Compose（v2 插件或 docker-compose 均可）。"
    fi
}

compose() { "${COMPOSE[@]}" "$@"; }

# ---------- 配置工具 ----------
random_hex() {
    local bytes="$1"
    if command -v openssl >/dev/null 2>&1; then
        openssl rand -hex "$bytes"
    elif [ -r /dev/urandom ]; then
        LC_ALL=C tr -dc 'a-f0-9' < /dev/urandom | head -c "$((bytes * 2))"
    else
        (date +%s%N; echo "$RANDOM$RANDOM") | sha256sum | cut -c1-$((bytes * 2))
    fi
}

# 读取 .env 中某个键的值（不使用 source，避免特殊字符被解释执行）
env_value() {
    local key="$1" default="${2:-}" value=""
    if [ -f "$ENV_FILE" ]; then
        value="$(grep -E "^${key}=" "$ENV_FILE" | tail -n 1 | cut -d= -f2-)"
    fi
    printf '%s' "${value:-$default}"
}

prepare_env() {
    local jwt db_pwd secret
    if [ ! -f "$ENV_FILE" ]; then
        [ -f "$ENV_EXAMPLE" ] || die "缺少配置模板：$ENV_EXAMPLE"
        cp "$ENV_EXAMPLE" "$ENV_FILE"
        jwt="$(random_hex 32)"
        db_pwd="$(random_hex 16)"
        sed -i "s|^JWT_SECRET=.*|JWT_SECRET=$jwt|" "$ENV_FILE"
        sed -i "s|^MYSQL_ROOT_PASSWORD=.*|MYSQL_ROOT_PASSWORD=$db_pwd|" "$ENV_FILE"
        log_ok "已创建 $ENV_FILE，并生成随机 JWT 密钥与数据库密码"
        printf '%b\n' "${BOLD}----------------------------------------${NC}"
        printf 'MySQL root 密码: %b\n' "${GREEN}${db_pwd}${NC}"
        printf '%b\n' "${BOLD}----------------------------------------${NC}"
        log_warn "请保存上面的密码（已写入 .env，仅显示一次）。"
        return
    fi

    log_info "使用已有配置：$ENV_FILE"
    secret="$(env_value JWT_SECRET "")"
    if [ -z "$secret" ] || [[ "$secret" == CHANGE_ME* ]] || [ "${#secret}" -lt 32 ]; then
        jwt="$(random_hex 32)"
        sed -i "s|^JWT_SECRET=.*|JWT_SECRET=$jwt|" "$ENV_FILE"
        log_warn "检测到弱/占位 JWT_SECRET，已自动生成随机密钥（旧令牌会失效）"
    fi
    db_pwd="$(env_value MYSQL_ROOT_PASSWORD "")"
    if [ -z "$db_pwd" ] || [[ "$db_pwd" == CHANGE_ME* ]]; then
        log_warn "数据库密码仍是占位值，请手动修改 $ENV_FILE 的 MYSQL_ROOT_PASSWORD；若数据库已初始化，还需同步修改容器内密码。"
    fi
}

check_port() {
    local port
    port="$(env_value APP_PORT 34369)"
    if command -v ss >/dev/null 2>&1; then
        ss -ltn 2>/dev/null | grep -qE "[:.]${port}[[:space:]]" && log_warn "宿主机端口 $port 已被占用，容器可能无法绑定端口。"
    elif command -v netstat >/dev/null 2>&1; then
        netstat -ltn 2>/dev/null | grep -qE "[:.]${port}[[:space:]]" && log_warn "宿主机端口 $port 已被占用，容器可能无法绑定端口。"
    fi
}

wait_healthy() {
    local name="$1" timeout="$2" elapsed=0 status=""
    while [ "$elapsed" -lt "$timeout" ]; do
        status="$(docker inspect --format='{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' "$name" 2>/dev/null | tr -d '\r\n' || true)"
        case "$status" in
            healthy|running)
                log_ok "$name 已就绪"
                return 0
                ;;
            unhealthy|exited|dead)
                log_err "$name 状态异常：$status"
                docker logs --tail=60 "$name" >&2 2>/dev/null || true
                return 1
                ;;
        esac
        elapsed=$((elapsed + 5))
        printf '.'
        sleep 5
    done
    printf '\n'
    log_err "$name 在 ${timeout} 秒内未就绪（当前状态：${status:-未知}）"
    docker logs --tail=80 "$name" >&2 2>/dev/null || true
    return 1
}

show_summary() {
    local port cors db_name
    port="$(env_value APP_PORT 34369)"
    cors="$(env_value CORS_ALLOWED_ORIGINS 'http://localhost:5173')"
    db_name="$(env_value MYSQL_DATABASE shop_admin)"
    cat <<EOF

============================================================
  部署完成
============================================================
  应用地址    : http://<服务器IP>:${port}
  Swagger     : http://<服务器IP>:${port}/swagger-ui.html
  健康检查    : http://<服务器IP>:${port}/actuator/health
  默认账号    : admin / admin123（请登录后尽快修改）
  数据库      : ${db_name}（容器 shop-admin-mysql）
  数据卷      : shop-admin-mysql-data / shop-admin-uploads
  CORS 白名单 : ${cors}

  运维请使用原生 docker compose 命令：
    查看日志 : docker compose logs -f app
    查看状态 : docker compose ps
    重启服务 : docker compose restart
    停止服务 : docker compose stop
    移除容器 : docker compose down     （数据卷保留）
============================================================
EOF
}

# ---------- 一键部署主流程 ----------
main() {
    if [ "$#" -gt 0 ]; then
        log_warn "本脚本为一键部署，不接受参数（已忽略：$*）"
    fi

    log_info "开始一键 Docker 部署..."
    ensure_docker
    [ -f "$SCRIPT_DIR/pom.xml" ] && [ -d "$SCRIPT_DIR/src" ] \
        || die "当前目录缺少 pom.xml 或 src，镜像需要由源码构建。请把完整项目目录上传到服务器后在根目录运行本脚本（不要只放旧的 jar）。"
    prepare_env
    check_port

    log_info "构建应用镜像（首次构建需下载 Maven 依赖，可能需要几分钟）..."
    if ! compose build app; then
        die "镜像构建失败，请查看上方 Maven 输出（常见原因：网络无法访问 Maven 仓库）。"
    fi

    log_info "启动 MySQL 与应用容器..."
    compose up -d || die "容器启动失败，请执行 docker compose logs app 查看原因。"

    local timeout
    timeout="$(env_value HEALTH_TIMEOUT 180)"
    log_info "等待服务就绪（最多 ${timeout} 秒）..."
    wait_healthy "$MYSQL_CONTAINER" "$timeout"
    wait_healthy "$APP_CONTAINER" "$timeout"
    show_summary
}

main "$@"
