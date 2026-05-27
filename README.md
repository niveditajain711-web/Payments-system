# Payments System

Educational **UPI-shaped payments simulation**: a multi-service stack that models how money moves from a user app through **TPAP → PSP → NPCI switch → Banks**. This is **not** production software and **not** a real TPAP, PSP, or bank integration.

## What you can try

| Flow | Description |
|------|-------------|
| **Pay (push)** | Debit payer, credit payee; same-bank or cross-bank (two ledger legs + clearing accounts) |
| **Balance** | Query balance by VPA; API returns `balancePaise` and human-readable `balanceRupees` |
| **Collect** | Create a collect request, then approve with a simulated PIN |
| **Register VPA** | Add a new payment address in the NPCI directory linked to an **existing** bank account |
| **Device bind** | Stub endpoint for demo device registration |

**Web UI** (React + Vite): sidebar navigation, balance card in ₹, status messages for pay/collect/errors.

## Architecture

```
Browser (5173) → TPAP (8080) → PSP (8081) → NPCI (8082) → Bank A (8083) / Bank B (8084)
                                                    ↓
                                              PostgreSQL
```

| Service | Port | Database | Role |
|---------|------|----------|------|
| `tpap-service` | 8080 | — | App-facing API (pay, collect, balance, VPA register) |
| `psp-service` | 8081 | — | Forwards requests to NPCI |
| `npci-switch-service` | 8082 | `npci` | Switch / orchestration, VPA directory, pay idempotency |
| `bank-service` (A) | 8083 | `bank-a` | Ledger, journal apply, balance |
| `bank-service` (B) | 8084 | `bank-b` | Second bank instance (required for cross-bank pay) |
| `web` | 5173 | — | Demo UI |

Shared DTOs and API paths live in the **`contracts`** Maven module.

## Prerequisites

- **Java 21**
- **Maven 3.9+**
- **PostgreSQL** (local install or any instance you configure)
- **Node.js 18+** and **npm** (for the web UI)

Create these databases in PostgreSQL (names match default `application.yml`):

- `bank-a`
- `bank-b`
- `npci`

Default JDBC settings in service `application.yml` files use `localhost:5432`, user `postgres`, password `admin` — change them in each module’s YAML if your setup differs.

## Build (required once, or after contract changes)

All Java modules are under `backend-services/`. Install the parent POM and modules into your local Maven repo:

```bash
cd backend-services
mvn clean install -DskipTests
```

Always run `bank-service`, `npci-switch-service`, etc. **after** this step, or from the parent with `-pl` (see below). Do not run a single service in isolation on a fresh clone without installing `contracts` first.

## Quick start — web UI

```bash
cd web
npm install
npm run dev
```

Open **http://localhost:5173**. The UI calls TPAP at `http://localhost:8080` by default.

Optional: copy `web/.env.local.example` to `web/.env.local` and set `VITE_TPAP_URL` if TPAP is not on port 8080.

## Quick start — backend (local)

Start **five** processes: Bank A, Bank B, NPCI, PSP, TPAP. Order matters: **banks → NPCI → PSP → TPAP**.

On first start, each bank with an empty `accounts` table runs **`BankDemoSeed`** and creates demo ledger accounts.

### Bank A (port 8083)

```bash
cd backend-services
mvn -pl bank-service spring-boot:run
```

### Bank B (port 8084) — second terminal

**PowerShell:**

```powershell
cd backend-services
$env:SERVER_PORT="8084"
$env:PAYMENTS_BANK_CODE="B"
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/bank-b"
mvn -pl bank-service spring-boot:run
```

**Command Prompt:**

```cmd
cd backend-services
set SERVER_PORT=8084
set PAYMENTS_BANK_CODE=B
set SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bank-b
mvn -pl bank-service spring-boot:run
```

### NPCI, PSP, TPAP

```bash
cd backend-services
mvn -pl npci-switch-service spring-boot:run
# separate terminals:
mvn -pl psp-service spring-boot:run
mvn -pl tpap-service spring-boot:run
```

| Service | URL |
|---------|-----|
| TPAP | http://localhost:8080 |
| PSP | http://localhost:8081 |
| NPCI | http://localhost:8082 |
| Bank A | http://localhost:8083 |
| Bank B | http://localhost:8084 |

### Run a single service from the parent

```bash
cd backend-services
mvn -pl tpap-service spring-boot:run
```

## Demo data

### Seeded VPAs (NPCI Flyway `V2__vpa_seed.sql`)

| VPA | Bank |
|-----|------|
| `nivedita@banka` | A |
| `rohan@banka` | A |
| `rohan@bankb` | B |

### Seeded ledger accounts (bank `BankDemoSeed` on empty DB)

| Account ID | Bank | Label | Initial balance |
|------------|------|-------|-----------------|
| `aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2` | A | Nivedita | ₹5,000 |
| `aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3` | A | Rohan A | ₹1,000 |
| `bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2` | B | Rohan B | ₹1,000 |

Clearing accounts exist for cross-bank settlement (`…aaa1` on A, `…bbb1` on B).

### Money and balance API

- All amounts in APIs are in **paise** (₹1 = `100` paise).
- Balance responses include:
  - `balancePaise` — canonical ledger value
  - `balanceRupees` — display string (e.g. `"5000.00"`)
  - `currency` — `INR`

Example:

```json
{
  "vpa": "nivedita@banka",
  "balancePaise": 500000,
  "balanceRupees": "5000.00",
  "currency": "INR"
}
```

## Registering a new VPA

`POST /api/v1/tpap/register/vpa` (or the **Register VPA** screen in the UI) adds an entry to the **NPCI VPA directory**:

- **New VPA string** — any handle you choose (e.g. `shop@banka`)
- **Bank code** — `A` or `B`
- **Account ID** — must be an **existing** user account UUID at that bank (see table above)

A new VPA is an **alias** for an existing ledger account (like multiple UPI IDs on one bank account). It does **not** open a new account or add funds.

Balance lookup resolves the directory entry and reads the ledger by **account ID**, so a newly registered VPA shows the same balance as that account.

## Example API calls

**Pay** (cross-bank — requires Bank B on 8084):

```bash
curl -X POST http://localhost:8080/api/v1/tpap/payments/pay \
  -H "Content-Type: application/json" \
  -H "X-Correlation-Id: demo-1" \
  -H "Idempotency-Key: demo-pay-1" \
  -d "{\"payerVpa\":\"nivedita@banka\",\"payeeVpa\":\"rohan@bankb\",\"amountPaise\":10000}"
```

**Balance:**

```bash
curl "http://localhost:8080/api/v1/tpap/balance?vpa=nivedita@banka" \
  -H "X-Correlation-Id: demo-2"
```

**Register VPA** (alias for Nivedita’s account):

```bash
curl -X POST http://localhost:8080/api/v1/tpap/register/vpa \
  -H "Content-Type: application/json" \
  -H "X-Correlation-Id: demo-3" \
  -d "{\"vpa\":\"shop@banka\",\"displayName\":\"Shop\",\"bankCode\":\"A\",\"accountId\":\"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2\"}"
```

## Project structure

```
Payments-system/
├── backend-services/
│   ├── pom.xml                 # Parent POM (build from here)
│   ├── contracts/              # Shared DTOs, ApiPaths, headers
│   ├── bank-service/           # Run twice as Bank A and Bank B
│   ├── npci-switch-service/
│   ├── psp-service/
│   └── tpap-service/
├── web/                        # React + Vite demo UI
├── .gitignore
└── README.md
```

## Configuration

Per-service settings: `backend-services/<service>/src/main/resources/application.yml` (and optional `application-local.yml`).

| Variable | Service | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | bank, npci | JDBC URL |
| `SPRING_DATASOURCE_USERNAME` / `PASSWORD` | bank, npci | DB credentials |
| `SERVER_PORT` | all | HTTP port |
| `PAYMENTS_BANK_CODE` | bank | `A` or `B` |
| `PAYMENTS_NPCI_BANK_A_BASE_URL` | npci | Default `http://localhost:8083` |
| `PAYMENTS_NPCI_BANK_B_BASE_URL` | npci | Default `http://localhost:8084` |
| `PAYMENTS_PSP_NPCI_BASE_URL` | psp | Default `http://localhost:8082` |
| `PAYMENTS_TPAP_PSP_BASE_URL` | tpap | Default `http://localhost:8081` |
| `VITE_TPAP_URL` | web | TPAP base URL for the browser |

## Troubleshooting

| Issue | What to check |
|-------|----------------|
| Maven: `payments-system:pom` not found | Run `mvn install` from `backend-services/` first |
| Port already in use | `netstat -ano \| findstr ":8080"` (Windows) — stop the old Java process |
| Cross-bank pay fails with I/O error on **8084** | Start **Bank B** on port 8084 with `PAYMENTS_BANK_CODE=B` and DB `bank-b` |
| Balance 404 after registering VPA | Use a valid **account ID** from the demo table; restart NPCI + bank after code changes |
| Pay `npciResponseCode` **51** | Insufficient funds — amount (paise) exceeds payer balance |
| `balanceRupees` missing in JSON | Rebuild with `mvn install` and restart all Java services |
| Hibernate / Flyway errors on startup | DB exists; correct URL; only one bank instance per port |
| Web cannot reach TPAP | TPAP running on 8080; CORS enabled on TPAP; check `VITE_TPAP_URL` |

## Tech stack

- **Backend:** Java 21, Spring Boot 3.4, Spring Data JPA, Flyway, PostgreSQL
- **Frontend:** React 18, TypeScript, Vite 6
- **HTTP:** REST between services (`RestClient`); shared JSON contracts

## License

Add your license here before publishing.
