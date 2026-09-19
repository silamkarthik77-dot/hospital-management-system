# Hospital Management System

A full-stack Hospital Management System: Spring Boot 3 (Java 21) REST API + React 18
frontend, MySQL database, JWT auth with role-based access control, AWS S3 for medical
report storage, Dockerized, with a GitHub Actions CI/CD pipeline.

> **Status note (read this first):** this repository was generated as a strong,
> working starting point, not a finished, battle-tested production system. The
> backend's core flows (auth, appointments, billing, reports, admin dashboard) are
> implemented end-to-end and internally consistent. Before calling this
> "production-ready," you still need to: run it, fix any compile issues that show up
> in your exact toolchain, load-test it, do a real security review, and fill in the
> gaps called out in [Known Gaps](#12-known-gaps-before-calling-this-production-ready)
> below. Treat it as a serious head start, not a black box.

---

## 1. Project Overview

| | |
|---|---|
| **Backend** | Java 21, Spring Boot 3.3, Spring Security (JWT), Spring Data JPA/Hibernate, MySQL |
| **Frontend** | React 18, React Router 6, Axios, Bootstrap 5, Chart.js |
| **Cloud** | AWS EC2 (compute), AWS RDS (MySQL), AWS S3 (medical report files), IAM, CloudWatch |
| **DevOps** | Docker, Docker Compose, GitHub Actions, Nginx reverse proxy |
| **Docs** | Swagger/OpenAPI (springdoc), Postman collection |

## 2. Features

- **Auth**: JWT login, patient self-registration, forgot/reset password, BCrypt hashing
- **RBAC**: four roles — `ADMIN`, `DOCTOR`, `PATIENT`, `RECEPTIONIST` — enforced via
  Spring Security method + URL-level `@PreAuthorize`
- **Patient**: register, view/edit profile, book/cancel appointments, appointment
  history, view prescriptions, download medical reports, pay bills
- **Doctor**: daily schedule, patient details, update diagnosis, view appointment
  history, upload prescriptions
- **Receptionist**: search patients, view/confirm appointments, billing
- **Admin**: dashboard with live stats (patients, doctors, today's appointments,
  monthly revenue, pending bills) and a trend chart
- **Billing**: consultation/lab/medicine invoices, payment recording, PDF invoice
  generation and download
- **Medical Reports**: stored in AWS S3, metadata in MySQL; upload/list/delete
- **Email notifications**: registration, appointment booking/cancellation,
  prescription upload, bill generation (async, non-blocking)
- **API docs**: Swagger UI at `/swagger-ui.html`

## 3. Architecture

```
                        ┌─────────────────────┐
   Browser  ───────────▶│   React SPA (Nginx)  │
                        └──────────┬───────────┘
                                   │ REST (JWT Bearer)
                                   ▼
                        ┌─────────────────────┐
                        │  Spring Boot API     │
                        │  (EC2 / Docker)      │
                        │  controller→service  │
                        │  →repository→entity  │
                        └──────┬───────┬───────┘
                               │       │
                    JDBC/JPA   │       │  AWS SDK
                               ▼       ▼
                     ┌─────────────┐ ┌──────────────┐
                     │  AWS RDS    │ │   AWS S3      │
                     │  (MySQL)    │ │ (report files)│
                     └─────────────┘ └──────────────┘
```

Backend package layout (`backend/src/main/java/com/hms/`):

```
controller/   REST endpoints, request/response mapping, @PreAuthorize
service/      business logic interfaces + impl/
repository/   Spring Data JPA interfaces
entity/       JPA entities (Users, Roles, Patients, Doctors, Departments,
              Appointments, Prescriptions, MedicalReports, Invoices,
              Payments, Notifications)
dto/          request/ and response/ payloads
security/     JwtService, JwtAuthenticationFilter
config/       SecurityConfig, OpenApiConfig, S3Config, JpaAuditingConfig, DataSeeder
exception/    GlobalExceptionHandler, custom exceptions, ErrorResponse
util/         BCryptHashGenerator (dev tool)
```

## 4. Database Schema

See [`database/schema.sql`](database/schema.sql) for the full normalized DDL
(3NF) and [`database/seed_data.sql`](database/seed_data.sql) for sample data.

Entity-relationship summary:

```
roles 1───* users 1───1 patients 1───* appointments *───1 doctors *───1 departments
                              │              │
                              │              ├──1 prescriptions
                              │              └──* invoices *───* payments
                              │
                              ├──* medical_reports
                              └──* notifications
```

Tables: `roles`, `users`, `departments`, `doctors`, `patients`, `appointments`,
`prescriptions`, `medical_reports`, `invoices`, `payments`, `notifications`.

`DataSeeder` (a `CommandLineRunner`) idempotently seeds the four roles and a
starter department list on every boot, so the app works even if you skip
`seed_data.sql`. `seed_data.sql` additionally adds demo doctor/patient/admin
accounts for quick manual testing.

## 5. Local Setup

### Prerequisites
- Java 21, Maven 3.9+
- Node.js 20+, npm
- MySQL 8 (or use Docker Compose, which provisions it for you)
- Docker + Docker Compose (recommended path)

### Option A — Docker Compose (fastest)

```bash
cp .env.example .env
# edit .env: set JWT_SECRET at minimum

docker compose up --build
```

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html

The MySQL container auto-runs `database/schema.sql` and `database/seed_data.sql`
on first boot via its `docker-entrypoint-initdb.d` mount.

### Option B — Run backend and frontend separately

```bash
# 1. Database
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed_data.sql   # optional demo data

# 2. Backend
cd backend
export DB_USERNAME=hms_user DB_PASSWORD=hms_password JWT_SECRET=some-long-random-string
mvn spring-boot:run

# 3. Frontend
cd frontend
cp .env.example .env
npm install
npm start
```

### Demo accounts (after seeding, password `Password@123`)
| Role | Email |
|---|---|
| Admin | admin@hms.com |
| Doctor | ananya.rao@hms.com |
| Doctor | karan.mehta@hms.com |
| Patient | ritika.sharma@example.com |
| Receptionist | priya.desk@hms.com |

> The seeded password hash is a **placeholder** — see the comment at the top of
> `database/seed_data.sql` and generate a real one with
> `backend/src/main/java/com/hms/util/BCryptHashGenerator.java` before relying
> on these logins.

## 6. API Documentation

Once the backend is running: **http://localhost:8080/swagger-ui.html**
(OpenAPI JSON at `/v3/api-docs`).

A ready-to-import Postman collection is at
[`postman/HMS-API.postman_collection.json`](postman/HMS-API.postman_collection.json) —
set the `baseUrl` and `token` collection variables (paste the JWT from
`/api/auth/login`) before running authenticated requests.

Key endpoint groups: `/api/auth`, `/api/doctors`, `/api/appointments`,
`/api/medical-reports`, `/api/billing`, `/api/patients`, `/api/receptionist`,
`/api/admin`.

## 7. Testing

```bash
cd backend
mvn test
```

JUnit 5 + Mockito unit tests are included for `AuthServiceImpl` and
`AppointmentServiceImpl` (`backend/src/test/java/com/hms/service/`) covering the
core success/failure branches (duplicate email, slot conflicts, token issuance,
email side-effects). Extend this pattern — mock the repository/service layer,
assert on the returned DTO and on `verify()`ed interactions — for the remaining
services and controllers.

## 8. Docker

- `backend/Dockerfile` — multi-stage Maven build → JRE-Alpine runtime, non-root user
- `frontend/Dockerfile` — multi-stage Node build → Nginx runtime, serves the SPA and
  reverse-proxies `/api/**` to the backend container
- `docker-compose.yml` — MySQL + backend + frontend, wired together on a bridge
  network, with a healthcheck gate so the backend waits for MySQL

## 9. CI/CD (GitHub Actions)

[`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml) runs on every push/PR
to `main`:

1. **backend-build-test** — `mvn test` + `mvn package`, uploads the jar as an artifact
2. **frontend-build** — `npm ci` + `npm run build`, uploads the build as an artifact
3. **docker-build-push** *(main only)* — builds and pushes both images to Docker Hub
4. **deploy-to-ec2** *(main only)* — SSHes into your EC2 host and runs
   `docker compose pull && up -d`

Required repo secrets: `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`, `EC2_HOST`,
`EC2_USERNAME`, `EC2_SSH_KEY`, and optionally `REACT_APP_API_BASE_URL`.

## 10. AWS Deployment Guide

This is a real, step-by-step path — not just config stubs — but you run the AWS
console/CLI steps yourself; nothing here provisions AWS resources automatically.

### 10.1 RDS (MySQL)
1. RDS console → **Create database** → Engine: MySQL 8.0 → Templates: *Free tier*
   (or Production, for Multi-AZ)
2. DB instance identifier: `hms-db`; set master username/password
3. Public access: **No** (keep the DB private; only the app tier should reach it)
4. VPC security group: create `hms-rds-sg`, inbound rule: MySQL/Aurora (3306) from
   `hms-ec2-sg` only (see 10.2)
5. Once available, note the **endpoint** — this becomes `DB_HOST`
6. From a bastion or the EC2 instance itself, run `database/schema.sql` then
   `database/seed_data.sql` (after fixing the password hash) against the RDS endpoint

### 10.2 S3 (medical reports)
1. S3 console → **Create bucket** → name it (e.g. `hms-medical-reports-<your-suffix>`,
   must be globally unique) → same region as your EC2/RDS
2. Block all public access: **On** (files are served via the backend, never directly)
3. Enable default encryption (SSE-S3) and versioning (recommended)
4. Create an IAM policy scoped to just this bucket (`s3:PutObject`, `s3:GetObject`,
   `s3:DeleteObject` on `arn:aws:s3:::hms-medical-reports-*/*`)

### 10.3 IAM
1. Create an IAM role `hms-ec2-role`, attach the S3 policy from 10.2
2. Attach this role to the EC2 instance (Instance settings → *Attach/Replace IAM Role*)
   — this lets the backend use `DefaultCredentialsProvider` with **no access keys on
   disk**, which is the safer path for anything beyond local dev
3. Only set `AWS_ACCESS_KEY_ID`/`AWS_SECRET_ACCESS_KEY` env vars for local development;
   leave them blank in production and rely on the instance role

### 10.4 EC2 (backend + frontend via Docker Compose)
1. Launch an EC2 instance (e.g. `t3.medium`, Amazon Linux 2023 or Ubuntu 22.04)
2. Security group `hms-ec2-sg`: inbound 22 (SSH, your IP only), 80/443 (HTTP/S,
   0.0.0.0/0), and allow 3306 outbound to RDS
3. Allocate and associate an **Elastic IP** so the address doesn't change on restart
4. SSH in, install Docker + Docker Compose plugin
5. `git clone` this repo (or `scp` it) into `/opt/hms`
6. Create `/opt/hms/.env` with real values (`DB_HOST` = RDS endpoint, `JWT_SECRET`,
   `S3_BUCKET_NAME`, `CORS_ORIGINS` = your domain, SMTP creds, etc.) — leave the AWS
   key vars blank since you're using the instance role
7. `docker compose up -d --build`
8. Point your domain's DNS A record at the Elastic IP; terminate TLS either at an
   Application Load Balancer (recommended) or by adding a Certbot/Let's Encrypt
   container in front of Nginx

### 10.5 CloudWatch
1. Install/enable the CloudWatch agent on the EC2 instance for system metrics
   (CPU, memory, disk)
2. Ship container logs with the `awslogs` Docker logging driver (add
   `logging: driver: awslogs` blocks to `docker-compose.yml` services) or run the
   CloudWatch Logs agent against `docker logs`
3. `/actuator/health` is exposed for external uptime checks (wire it into a
   CloudWatch Synthetics canary or an ALB health check)

### 10.6 Recommended production topology upgrade
The EC2+Compose setup above is the fastest path and fine for a portfolio/demo. For
anything closer to real production, prefer: ALB in front of an Auto Scaling Group
(or ECS/Fargate) for the backend, RDS Multi-AZ, S3 + CloudFront for the frontend
build instead of Nginx-on-EC2, and Secrets Manager instead of a `.env` file on disk.

## 11. Security Notes
- Passwords hashed with BCrypt (strength 12)
- Stateless JWT (HS256), 1-hour access token expiry (configurable)
- CORS restricted via `CORS_ORIGINS`
- All inputs validated with Jakarta Bean Validation (`@Valid` + annotated DTOs)
- Centralized exception handling — no stack traces leak to clients
- `@PreAuthorize` on every non-public endpoint, matching the RBAC matrix in
  `SecurityConfig`
- **Before production**: rotate `JWT_SECRET` to a real random 256-bit value, put
  the app behind HTTPS/TLS, and get a second pair of eyes on the security config —
  this was generated quickly and hasn't had a formal review.

## 12. Known Gaps (before calling this "production-ready")

Being upfront about what's *not* done, since the original scope asked for
everything at once and no responsible build actually finishes all of this in one
pass:

- **Presigned S3 URLs**: `MedicalReportService.getDownloadUrl()` currently returns
  the raw S3 key as a placeholder — wire up `S3Presigner` to return a real,
  time-limited HTTPS URL before shipping.
- **Refresh tokens**: only access tokens are issued; there's no refresh-token
  rotation endpoint yet.
- **Analytics endpoints**: the admin dashboard's trend chart uses placeholder data
  in the frontend — the backend only exposes point-in-time stats
  (`/api/admin/dashboard`), not monthly time series. Add a real analytics query/
  endpoint before wiring the chart to live data.
- **Doctor/Receptionist self-registration**: only patient self-registration exists;
  doctor/receptionist accounts are meant to be created by an admin — that
  admin-facing "create staff user" endpoint isn't built yet.
- **File-upload UI for prescriptions/reports**: the backend endpoints exist
  (`/api/medical-reports`), but the React app doesn't yet have the upload forms for
  doctors — only the list/download/delete API calls are wired into services.
- **Test coverage**: two services have real unit tests as a pattern to follow;
  the rest of the services and all controllers are untested.
- **Compilation not verified in this environment**: this code was written offline
  without network/Maven access to actually build it here. Run `mvn clean install`
  yourself first and expect to fix minor issues (dependency versions, occasional
  typos) before deploying.

## 13. Future Enhancements
- Refresh token rotation + logout/blacklist endpoint
- Real-time notifications (WebSocket) instead of poll-on-load
- Doctor availability calendar UI (currently just CSV day-of-week + start/end time)
- Multi-file report uploads, virus scanning on upload (e.g. via S3 + Lambda)
- Rate limiting on `/api/auth/**`
- i18n for the frontend
