#!/usr/bin/env bash
# =============================================================================
# start-all.sh — Nebula DB: Start all Spring Boot microservices in dependency order
#
# Order:
#   1. Eureka Server (service registry — all others depend on it)
#   2. nebula-auth (auth service)
#   3. project-service (business logic + Kafka producer)
#   4. docker-infra-service (Docker provisioner + Kafka consumer)
#   5. api-gateway (entry point — started last after all services register)
#
# Prerequisites:
#   - Java 21+ on PATH
#   - Maven 3.9+ on PATH
#   - Docker infra running: cd infra && docker-compose up -d
#
# Usage:
#   chmod +x start-all.sh
#   ./start-all.sh
# =============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$SCRIPT_DIR/logs"
mkdir -p "$LOG_DIR"

# ---- Configurable wait time between service starts (seconds) ---------------
EUREKA_WAIT=20      # Wait for Eureka to fully start before registering clients
SERVICE_WAIT=10     # Wait between each subsequent service
GATEWAY_WAIT=15     # Extra wait before starting gateway (so services have registered)

# ---- Helper functions -------------------------------------------------------

log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*"
}

wait_for_http() {
    local name="$1"
    local url="$2"
    local max_attempts="${3:-30}"
    local attempt=1

    log "Waiting for $name to be ready at $url ..."
    while [[ $attempt -le $max_attempts ]]; do
        if curl -sf "$url" > /dev/null 2>&1; then
            log "$name is UP"
            return 0
        fi
        log "  [$attempt/$max_attempts] $name not ready yet, retrying in 3s..."
        sleep 3
        ((attempt++))
    done

    log "ERROR: $name did not start within expected time. Check $LOG_DIR/$name.log"
    exit 1
}

start_service() {
    local name="$1"
    local jar_path="$2"
    local health_url="$3"
    local log_file="$LOG_DIR/${name}.log"

    log "Starting $name ..."
    nohup java -jar "$jar_path" \
        --spring.output.ansi.enabled=ALWAYS \
        > "$log_file" 2>&1 &
    echo $! > "$LOG_DIR/${name}.pid"
    log "$name started with PID $(cat "$LOG_DIR/${name}.pid") — logs: $log_file"

    if [[ -n "$health_url" ]]; then
        wait_for_http "$name" "$health_url"
    fi
}

# ---- Stop any existing running services ------------------------------------
log "===== Stopping any existing services ====="
pkill -f 'java.*\.jar' 2>/dev/null || true
sleep 3
log "Existing services stopped."

# ---- Build all modules first (skip tests for speed) ------------------------
log "===== Building all modules ====="
cd "$SCRIPT_DIR"
mvn clean package -DskipTests -q
log "Build complete"

# ---- 1. Eureka Server -------------------------------------------------------
log ""
log "===== [1/5] Starting Eureka Server ====="
start_service "eureka-server" \
    "$SCRIPT_DIR/cloud/eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar" \
    "http://localhost:8761/actuator/health"

log "Waiting ${EUREKA_WAIT}s for Eureka to stabilise..."
sleep "$EUREKA_WAIT"

# ---- 2. nebula-auth ---------------------------------------------------------
log ""
log "===== [2/5] Starting nebula-auth ====="
start_service "nebula-auth" \
    "$SCRIPT_DIR/service-infra/nebula-auth/target/nebula-auth-service-0.0.1-SNAPSHOT.jar" \
    "http://localhost:8002/actuator/health"
sleep "$SERVICE_WAIT"

# ---- 3. project-service -----------------------------------------------------
log ""
log "===== [3/5] Starting project-service ====="
start_service "project-service" \
    "$SCRIPT_DIR/service-infra/project-service/target/project-service-0.0.1-SNAPSHOT.jar" \
    "http://localhost:8000/actuator/health"
sleep "$SERVICE_WAIT"

# ---- 4. docker-infra-service ------------------------------------------------
log ""
log "===== [4/5] Starting docker-infra-service ====="
start_service "docker-infra-service" \
    "$SCRIPT_DIR/service-infra/docker-infra-service/target/docker-infra-service-0.0.1-SNAPSHOT.jar" \
    "http://localhost:8001/actuator/health"
sleep "$SERVICE_WAIT"

# ---- 5. api-gateway ---------------------------------------------------------
log ""
log "===== [5/5] Starting api-gateway ====="
log "Waiting ${GATEWAY_WAIT}s for all services to register with Eureka before starting gateway..."
sleep "$GATEWAY_WAIT"

start_service "api-gateway" \
    "$SCRIPT_DIR/cloud/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar" \
    "http://localhost:8888/actuator/health"

# ---- Summary ----------------------------------------------------------------
log ""
log "============================================"
log "  All services are running!"
log "  API Gateway : http://localhost:8888"
log "  Eureka UI   : http://localhost:8761"
log "  Auth Service: http://localhost:8002"
log "  Project Svc : http://localhost:8000"
log "  Docker Infra: http://localhost:8001"
log "============================================"
log ""
log "Quick API Test:"
log "  POST http://localhost:8888/api/v1/auth/signup"
log "  POST http://localhost:8888/api/v1/auth/login"
log "  POST http://localhost:8888/api/v1/projects  (requires: Authorization: Bearer <token>)"
log ""
log "To stop all services:"
log "  for f in $LOG_DIR/*.pid; do kill \$(cat \"\$f\") 2>/dev/null || true; rm -f \"\$f\"; done"
