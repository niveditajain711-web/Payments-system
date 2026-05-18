# Payments System

Educational UPI-shaped payments simulation: a multi-service stack that models how money moves from a user app through **TPAP → PSP → NPCI → Banks**. This is **not** production software and **not** a real TPAP or bank integration.

## Architecture

```
User (Web) → TPAP (8080) → PSP (8081) → NPCI Switch (8082) → Bank A (8083) / Bank B (8084)
                                              ↓
                                         PostgreSQL
```

| Service | Port | Database | Role |
|---------|------|----------|------|
| `tpap-service` | 8080 | — | App-facing API (pay, collect, balance, VPA register) |
| `psp-service` | 8081 | — | Forwards requests to NPCI |
| `npci-switch-service` | 8082 | `npci` | Switch / orchestration, VPA directory |
| `bank-service` (A) | 8083 | `bank_a` | Ledger, balance, debit/credit |
| `bank-service` (B) | 8084 | `bank_b` | Ledger, balance, debit/credit |
| `web` | 5173 | — | React demo UI |

API routes are documented in [docs/api-routing.md](docs/api-routing.md).

## Prerequisites

- **Java 21**
- **Maven 3.9+**
- **Node.js 18+** and **npm** (for the web UI)
- **Docker** and **Docker Compose** (recommended for Postgres and full stack)

## Quick start (Docker — all services)

From the repository root:

```bash
docker compose up --build
```

This starts Postgres, both banks, NPCI, PSP, and TPAP with the `docker` Spring profile. Open the web UI separately (see below).

| Service | URL |
|---------|-----|
| TPAP | http://localhost:8080 |
| PSP | http://localhost:8081 |
| NPCI | http://localhost:8082 |
| Bank A | http://localhost:8083 |
| Bank B | http://localhost:8084 |

Stop everything:

```bash
docker compose down
```

## Quick start (local — Windows PowerShell)

Use the launcher script to open each service in its own terminal:

```powershell
.\scripts\run-local.ps1 -StartPostgresWithDocker
```

Options:

- `-StartPostgresWithDocker` — starts only the Postgres container from `docker-compose.yml`
- `-SkipWeb` — do not start the Vite dev server

Backend-only URLs are the same as in the Docker table above. With the web app:

```powershell
cd web
Copy-Item .env.local.example .env.local   # first time only
npm install
npm run dev
```

Web UI: http://localhost:5173 (calls TPAP at `http://localhost:8080` by default).

## Manual local run

### 1. Start Postgres

```bash
docker compose up -d postgres
```

Postgres listens on `localhost:5432`. Credentials and databases are created on first boot:

| Setting | Value |
|---------|-------|
| User | `payments` |
| Password | `payments` |
| Databases | `bank_a`, `bank_b`, `npci` |

Init script: [docker/postgres/init-databases.sql](docker/postgres/init-databases.sql).

### 2. Start services (order matters)

Use profile `local` and the env vars below, or rely on `application-local.yml` in each module.

**Bank A** (`bank-service`, port 8083):

```powershell
cd bank-service
$env:SPRING_PROFILES_ACTIVE="local"
$env:SERVER_PORT="8083"
$env:PAYMENTS_BANK_CODE="A"
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/bank_a"
$env:SPRING_DATASOURCE_USERNAME="payments"
$env:SPRING_DATASOURCE_PASSWORD="payments"
mvn spring-boot:run
```

**Bank B** (`bank-service`, port 8084):

```powershell
cd bank-service
$env:SPRING_PROFILES_ACTIVE="local"
$env:SERVER_PORT="8084"
$env:PAYMENTS_BANK_CODE="B"
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/bank_b"
$env:SPRING_DATASOURCE_USERNAME="payments"
$env:SPRING_DATASOURCE_PASSWORD="payments"
mvn spring-boot:run
```

**NPCI** (`npci-switch-service`, port 8082):

```powershell
cd npci-switch-service
$env:SPRING_PROFILES_ACTIVE="local"
$env:SERVER_PORT="8082"
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/npci"
$env:SPRING_DATASOURCE_USERNAME="payments"
$env:SPRING_DATASOURCE_PASSWORD="payments"
$env:PAYMENTS_NPCI_BANK_A_BASE_URL="http://localhost:8083"
$env:PAYMENTS_NPCI_BANK_B_BASE_URL="http://localhost:8084"
mvn spring-boot:run
```

**PSP** (`psp-service`, port 8081):

```powershell
cd psp-service
$env:SPRING_PROFILES_ACTIVE="local"
$env:SERVER_PORT="8081"
$env:PAYMENTS_PSP_NPCI_BASE_URL="http://localhost:8082"
mvn spring-boot:run
```

**TPAP** (`tpap-service`, port 8080):

```powershell
cd tpap-service
$env:SPRING_PROFILES_ACTIVE="local"
$env:SERVER_PORT="8080"
$env:PAYMENTS_TPAP_PSP_BASE_URL="http://localhost:8081"
mvn spring-boot:run
```

## Configuration

### Spring profiles

| Profile | Use case |
|---------|----------|
| *(default)* | Localhost URLs for manual runs without extra env |
| `local` | Explicit localhost DB and service URLs — see `application-local.yml` per module |
| `docker` | Used by `docker-compose.yml` (container hostnames, Postgres service name) |

### Environment variables

| Variable | Service | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | bank, npci | JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | bank, npci | DB user |
| `SPRING_DATASOURCE_PASSWORD` | bank, npci | DB password |
| `PAYMENTS_BANK_CODE` | bank | `A` or `B` |
| `PAYMENTS_NPCI_BANK_A_BASE_URL` | npci | Bank A base URL |
| `PAYMENTS_NPCI_BANK_B_BASE_URL` | npci | Bank B base URL |
| `PAYMENTS_PSP_NPCI_BASE_URL` | psp | NPCI base URL |
| `PAYMENTS_TPAP_PSP_BASE_URL` | tpap | PSP base URL |
| `VITE_TPAP_URL` | web | TPAP API base (default `http://localhost:8080`) |

Web: copy [web/.env.local.example](web/.env.local.example) to `web/.env.local` if you need a custom TPAP URL.

### Databases

- **bank_a** / **bank_b**: Flyway migrations in `bank-service/src/main/resources/db/migration/`
- **npci**: Flyway migrations in `npci-switch-service/src/main/resources/db/migration/` (includes sample VPAs in `V2__vpa_seed.sql`)

Sample VPAs after NPCI migration:

- `nivedita@banka` (Bank A)
- `rohan@banka` (Bank A)
- `rohan@bankb` (Bank B)

Register new VPAs via `POST /api/v1/tpap/register/vpa` or the web UI.

## Example API call

Pay from one VPA to another:

```bash
curl -X POST http://localhost:8080/api/v1/tpap/payments/pay \
  -H "Content-Type: application/json" \
  -H "X-Correlation-Id: demo-1" \
  -H "Idempotency-Key: demo-pay-1" \
  -d "{\"payerVpa\":\"rohan@banka\",\"payeeVpa\":\"rohan@bankb\",\"amountPaise\":10000}"
```

Check balance:

```bash
curl "http://localhost:8080/api/v1/tpap/balance?vpa=rohan@banka" \
  -H "X-Correlation-Id: demo-2"
```

## Project structure

```
payments-system/
├── bank-service/           # Bank ledger (run twice as A and B)
├── npci-switch-service/  # UPI switch simulation
├── psp-service/          # PSP gateway
├── tpap-service/         # TPAP app API
├── contracts/            # Shared DTOs
├── web/                  # React + Vite demo UI
├── docker/               # Dockerfiles, Postgres init
├── scripts/              # run-local.ps1
├── docs/                 # API routing reference
└── docker-compose.yml
```

Build all Java modules from the root:

```bash
mvn clean install
```

## Troubleshooting

| Issue | What to check |
|-------|----------------|
| Bank/NPCI fails on startup | Postgres running; databases `bank_a`, `bank_b`, `npci` exist; port 5432 free |
| Connection refused between services | Start banks before NPCI, then PSP, then TPAP |
| Flyway validation error | Drop volumes and recreate: `docker compose down -v` then `docker compose up -d postgres` |
| Web cannot reach TPAP | `VITE_TPAP_URL` in `web/.env.local`; TPAP on port 8080 |
| Port already in use | Stop other instances or change `SERVER_PORT` per service |

## License

Add your license here before publishing.
