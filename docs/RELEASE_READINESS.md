# Release readiness checklist

## Foundation included in this repository
- Spring Boot REST API and PostgreSQL persistence
- Flyway schema migrations
- JWT-based authentication scaffold and BCrypt password hashing
- Citizen profile, scheme catalog, eligibility, document checklist, application tracker, and scheme review endpoints
- Docker Compose for PostgreSQL/API
- Swagger/OpenAPI and actuator health endpoint
- Original HTML prototype retained

## Must be verified locally before calling this production-ready
- [ ] `mvn clean verify` succeeds on JDK 21 (or JDK 24 with target 21 compatibility)
- [ ] Flyway migrations apply to a clean PostgreSQL 16 database
- [ ] Authentication and authorization tests cover anonymous, citizen, reviewer, and admin roles
- [ ] Every endpoint enforces record ownership and role boundaries
- [ ] API integration tests pass against PostgreSQL (not only mocks)
- [ ] Frontend is wired to APIs and has loading/error/empty states
- [ ] Real schemes have authoritative source URLs, eligibility criteria, documents, application steps, and reviewed-on dates
- [ ] Hindi/English content reviewed by fluent speakers
- [ ] Privacy policy, consent, retention/deletion, and security threat model reviewed
- [ ] Production secrets, HTTPS, backups, monitoring, rate limits, and deployment are configured

Scheme data is intentionally not fabricated. A functioning eligibility engine without trusted scheme records does not make the service trustworthy.
