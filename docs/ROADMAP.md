# SchemeSathi implementation roadmap

## Foundation currently in repository
- Java 21 / Spring Boot REST API and PostgreSQL + Flyway schema.
- JWT registration/login with BCrypt password hashes.
- Citizen profile CRUD and scheme browse/detail endpoints.
- Deterministic eligibility evaluation framework.
- Document catalog (21 initial common document types) and citizen-owned readiness checklist endpoints with database migration and unit tests.
- Citizen application tracker API (INTERESTED -> PREPARING -> SUBMITTED -> UNDER_REVIEW -> APPROVED / REJECTED / ON_HOLD), private to the authenticated citizen.
- Docker Compose local environment and OpenAPI/Swagger docs.
- Original HTML prototype preserved as a UI reference.

## Next implementation milestones
1. Run full Maven build and integration tests in a Java 21 + Docker environment; fix compile/runtime issues.
2. Build a proper Next.js/TypeScript frontend and connect it to the API; replace prototype localStorage behavior.
3. Admin/reviewer workflows: scheme create/edit, source citation, review history, approve/reject, publish/unpublish, role management.
4. Assemble a curated initial Central + Madhya Pradesh scheme catalog from official sources; capture source URL, checked-at date, evidence, data owner, and update cadence. Do not publish unverified eligibility assertions.
5. Improve eligibility rule DSL with typed operators, missing-data handling, rule versioning, and explainable per-rule outcomes; add automated tests.
6. Secure deployment: externalized secrets, HTTPS, rate limiting, email verification/password reset, audit log, backups, retention/deletion policy, consent, and security review.
7. Accessibility, Hindi localization, mobile responsive UX, operational monitoring, and end-to-end tests.

## Release gates
- No seeded/demo data is presented as official or verified.
- Admin endpoints enforce reviewer/admin authorization server-side.
- Citizen data is only accessible by its owner unless a lawful, consented administrative workflow applies.
- Build and test suite pass in CI before deployment.
