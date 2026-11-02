# SchemeSathi — Government Scheme Discovery Platform

> **Empowering Indian citizens to discover, check eligibility for, and track government schemes.**

SchemeSathi is a full-stack GovTech application that helps citizens discover Central and State government schemes they may be eligible for, understand their document readiness, and track application progress.

## Features

- **Citizen Registration & Login** — Secure JWT-based authentication with BCrypt password hashing
- **Profile Management** — Complete citizen profile for accurate eligibility matching
- **Scheme Discovery** — Browse verified Central & State government schemes with filtering
- **Eligibility Engine** — Deterministic rule-based eligibility assessment with clear explanations
- **Document Checklist** — Track document readiness against a comprehensive catalog
- **Application Tracker** — Track scheme application progress from interest to approval
- **Admin Review** — Role-based scheme review and publishing workflow with audit trail
- **Data Provenance** — All scheme data sourced from official .gov.in portals with verification timestamps

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21+ / Spring Boot 3.5 |
| Database | PostgreSQL 17+ |
| Migrations | Flyway |
| Auth | JWT (jjwt) + BCrypt |
| API Docs | SpringDoc OpenAPI (Swagger) |
| Frontend | Vanilla JS SPA (served by Spring Boot) |
| Testing | JUnit 5 + Mockito |

## Quick Start (Windows)

### Prerequisites
- Java 21+ (tested with Java 24)
- Maven 3.9+
- PostgreSQL 17+ running on `localhost:5433`

### 1. Database Setup

Open PowerShell or psql and run:
```sql
-- Connect as postgres superuser
CREATE DATABASE schemesathi;
CREATE USER schemesathi WITH PASSWORD 'your-strong-password';
GRANT ALL PRIVILEGES ON DATABASE schemesathi TO schemesathi;
ALTER DATABASE schemesathi OWNER TO schemesathi;
\c schemesathi
GRANT ALL ON SCHEMA public TO schemesathi;
```

### 2. Set Environment Variables

```powershell
$env:DATABASE_URL = "jdbc:postgresql://localhost:5433/schemesathi"
$env:DATABASE_USERNAME = "schemesathi"
$env:DATABASE_PASSWORD = "your-strong-password"
$env:JWT_SECRET = "generate-a-random-secret-at-least-32-bytes"
```

### 3. Build & Run

```powershell
cd backend
mvn clean verify         # Compile, test, and package
mvn spring-boot:run      # Start the application
```

### 4. Access the Application

- **Frontend**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs
- **Health**: http://localhost:8080/actuator/health

## API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/auth/register` | No | Register a new citizen |
| POST | `/api/v1/auth/login` | No | Login and receive JWT token |
| GET | `/api/v1/profile` | Yes | Get citizen profile |
| PUT | `/api/v1/profile` | Yes | Create/update citizen profile |
| GET | `/api/v1/schemes` | No | List published schemes (filters: state, category) |
| GET | `/api/v1/schemes/{id}` | No | Get scheme details |
| GET | `/api/v1/eligibility/{schemeId}` | Yes | Check eligibility for a scheme |
| GET | `/api/v1/documents/catalog` | No | List document types |
| GET | `/api/v1/documents/my-checklist` | Yes | Get citizen's document checklist |
| PUT | `/api/v1/documents/my-checklist` | Yes | Add/update document status |
| DELETE | `/api/v1/documents/my-checklist/{id}` | Yes | Remove from checklist |
| GET | `/api/v1/my-applications` | Yes | List tracked applications |
| POST | `/api/v1/my-applications` | Yes | Add scheme to tracker |
| PUT | `/api/v1/my-applications/{id}` | Yes | Update application status |
| DELETE | `/api/v1/my-applications/{id}` | Yes | Remove from tracker |
| GET | `/api/v1/admin/scheme-reviews/queue` | Admin | Get schemes pending review |
| POST | `/api/v1/admin/scheme-reviews/{id}/approve` | Admin | Approve and publish scheme |
| POST | `/api/v1/admin/scheme-reviews/{id}/reject` | Admin | Reject scheme |

## Project Structure

```
SchemeSathi-Real/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/in/schemesathi/
│       │   ├── auth/          # JWT auth, registration, login
│       │   ├── common/        # Error handling, API error format
│       │   ├── config/        # Security, CORS
│       │   ├── documents/     # Document catalog & checklist
│       │   ├── eligibility/   # Rule-based eligibility engine
│       │   ├── profile/       # Citizen profile CRUD
│       │   ├── scheme/        # Scheme CRUD, review workflow
│       │   └── tracker/       # Application tracking
│       └── resources/
│           ├── application.yml
│           ├── db/migration/  # Flyway SQL migrations
│           └── static/        # Frontend SPA
├── frontend/prototype/        # Original HTML prototypes
├── docs/                      # Additional documentation
├── .env.example               # Environment variable template
└── README.md
```

## Security

- Passwords hashed with BCrypt (cost factor 12)
- JWT tokens with HMAC-SHA signing
- Role-based access control (CITIZEN, REVIEWER, ADMIN)
- CORS restricted to configured origins
- Stateless session management
- Input validation on all endpoints
- No hardcoded credentials
- Secrets via environment variables only

## Scheme Data Policy

- All scheme data sourced from official `.gov.in` / `.nic.in` portals
- Each scheme record includes `source_url`, `source_name`, and `verified_at` timestamp
- Unverified records marked with `review_status = DRAFT`
- Admin review workflow required before publishing
- Audit trail for all review decisions

## License

This project is for educational and civic technology purposes.
