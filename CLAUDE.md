# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**OverMoney** is a distributed personal finance tracker (no bank integration) with transaction recognition from text and voice. Deployed at `https://overmoney.tech/overmoney`. Documentation in Russian.

## Build & Test Commands

```bash
# Build all modules
mvn clean package

# Full CI verification (tests + checkstyle + JaCoCo coverage) — required before merging
mvn clean verify

# Build a single service, skipping checks
cd orchestrator_service && mvn clean package -DskipTests -Dcheckstyle.skip

# Run a single service locally
cd orchestrator_service && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend

```bash
cd frontend/app && npm install && npm run dev    # Vue 3 app (Vite, port 5173)
cd frontend/landing && npm install && npm run dev # Landing page
```

### Local development databases

```bash
docker-compose -f docker-compose.dev.yaml up -d
```

Three separate PostgreSQL 14 instances start on ports 5433 (orchestrator), 5435 (telegram-bot), 5436 (payment).

## Architecture

Microservices communicating via **OpenFeign HTTP clients**:

| Service | Port | Role |
|---|---|---|
| `orchestrator_service` | 8081 | Core API, JWT auth, transactions, analytics |
| `recognizer_service` | 8080 | Text/voice recognition, LLM integration |
| `telegram_bot_service` | 8082 | Telegram bot interface, broadcast mailing |
| `payment_service` | 8084 | Robokassa payment gateway, subscriptions |
| `wit_ai_go_proxy` | 3333 | Go proxy wrapping Wit.AI voice API |
| `frontend/app` | 5173 | Vue 3 + TypeScript SPA |

The `orchestrator_service` is the central hub — all frontend and bot traffic routes through it. Bot and recognizer services call orchestrator for business logic; orchestrator calls recognizer and payment services.

**Inter-service auth:** All internal Feign calls include `X-INTERNAL-KEY` header (env var `INTERNAL_KEY_HEADER`).

**Shared modules:**
- `dto/` — shared DTOs used across services (included as Maven dependency)
- `mask_log_starter/` — custom Spring Boot starter for masking sensitive data in logs
- `actuator_git_info_starter/` — custom Spring Boot starter exposing git commit info via actuator

## Technology Stack

- **Backend:** Java 11, Spring Boot 2.7.10, Spring Cloud 2021.0.6
- **Database:** PostgreSQL + JPA/Hibernate; migrations via **Liquibase** (XML changelogs in `src/main/resources/db/changelog/`)
- **Auth:** JWT (JJWT 0.11.5), Spring Security
- **Scheduling:** ShedLock (distributed locks in DB)
- **Caching:** Caffeine
- **External APIs:** DeepSeek (LLM analysis), Ollama/Llama 3.2 (local keyword extraction), Wit.AI (voice-to-text), Robokassa (payments), Telegram Bot API
- **Observability:** Micrometer + Prometheus (`/actuator/prometheus`), Logstash JSON logging
- **API docs:** Springdoc OpenAPI at `/swagger-ui.html`
- **Code quality:** Checkstyle (120-char line limit, 4-space indent), JaCoCo (orchestrator requires 65% coverage minimum)
- **Tests:** JUnit + Spring Boot Test + embedded PostgreSQL (OTJ)

## CI/CD

GitHub Actions workflows in `.github/workflows/`:
- **`test_and_verify_codestyle.yml`** — runs on PRs to `master`; executes `mvn -B package` then `mvn verify`
- **`deploy_vps.yml`** — runs on push to `master`; detects changed services by path, builds Docker images, pushes to `ghcr.io/nekromant322/`, deploys to VPS via SSH (only changed services restart)

Every merge to `master` triggers automatic deployment. PRs must pass the verify workflow before merging.

## Application Profiles

- `dev` — local development (default)
- `prod` — production (injected via env var `SPRING_APPLICATION_PROFILE=prod`)
- `test` — integration tests (uses embedded PostgreSQL)
