# SchemeSathi local setup (Windows 11)

## Requirements
- JDK 21 (the Docker image builds with JDK 21; JDK 24 can usually compile with `--release 21`, but JDK 21 is the reproducible choice)
- Apache Maven 3.9+ (Maven 3.10.0 is fine)
- Docker Desktop with Compose

## Start PostgreSQL
From the project root (`SchemeSathi-Real`):

```powershell
Copy-Item .env.example .env
# Edit .env and set unique local values for DATABASE_PASSWORD and JWT_SECRET.
docker compose up -d db
```

The Compose service is named `db` (not `postgres`).

## Build and test the API
Open CMD or PowerShell:

```powershell
cd backend
mvn clean verify
```

## Run the API
```powershell
mvn spring-boot:run
```

API base URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Health: `http://localhost:8080/actuator/health`

## Run everything in Docker
From the project root:

```powershell
docker compose up --build
```

## Important limitations before production
- Populate the scheme catalog only with data verified against authoritative Central/State department sources; record source URL and verification date for each record.
- Admin provisioning, rate limiting, deployment secrets, backup/restore, monitoring, privacy notices, consent, and security review are required before public launch.
- Prototype HTML files are preserved under `frontend/prototype`; they are not yet a production frontend fully wired to every API endpoint.
- Do not collect Aadhaar/PAN or upload identity document images unless the product has a legally reviewed need, consent, retention/deletion policy, and adequate security controls.
