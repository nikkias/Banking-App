# Implementation Backlog

## Sprint 1: Persistent Banking Core

### B-001 PostgreSQL Dependencies

- Add `spring-boot-starter-data-jpa`
- Add `org.postgresql:postgresql`
- Add `org.flywaydb:flyway-core`
- Add Testcontainers for PostgreSQL tests

Status: Implementiert.

Acceptance:

- Backend builds.
- App can connect to local PostgreSQL.

### B-002 Flyway Schema

Create `src/main/resources/db/migration/V1__init_bank_schema.sql`.

Tables:

- `customers`
- `accounts`
- `transactions`
- `audit_events`

Status: Implementiert.

Acceptance:

- Flyway runs at startup.
- Schema is idempotent through Flyway history.

### B-003 JPA Adapter

Build infrastructure package:

```text
bank.infrastructure.jpa
  CustomerEntity
  AccountEntity
  TransactionEntity
  SpringDataCustomerJpaRepository
  SpringDataAccountJpaRepository
  SpringDataTransactionJpaRepository
  JpaCustomerRepositoryAdapter
  JpaAccountRepositoryAdapter
  JpaTransactionRepositoryAdapter
```

Status: Implementiert.

Acceptance:

- Existing `BankService` still depends only on ports.
- Service tests can still use in-memory repositories.
- Integration test uses JPA adapters.

### B-004 Optimistic Locking

Add version field to account persistence.

Status: Teilweise implementiert, `accounts.version` und JPA `@Version` sind vorbereitet.

Acceptance:

- Parallel updates cannot silently overwrite balance.

## Sprint 2: Investor Frontend

### F-001 App Shell

- Top navigation
- Dashboard route
- Accounts route
- Account detail route
- Transfer route

### F-002 Account Detail

- Show IBAN, balance, customer reference
- Show transactions
- Empty state
- Error state

Status: Implementiert.

### F-003 Transfer Form

- Source account select
- Target account select
- Amount
- Description
- Success and error state

Status: Implementiert.

### F-004 Frontend API Core

Move HTTP details to:

```text
src/app/core/api
src/app/core/errors
src/app/core/config
```

## Sprint 3: Security and Audit

### S-001 Authentication

For demo:

- local users in dev profile
- Spring Security
- frontend login state

For production direction:

- OIDC provider
- JWT validation

Status: Implementiert als lokaler Demo-Basic-Login.

### S-002 Authorization

Rules:

- `CUSTOMER` sees own accounts.
- `EMPLOYEE` can open accounts and perform assisted operations.
- `ADMIN` can inspect audit events.

Status: Implementiert mit Rollen auf API-Endpunkten und Eigentümerprüfung für Customer-Konten.

### S-003 Audit Events

Record:

- actor
- action
- account id
- amount
- timestamp
- result

Status: Implementiert für zentrale Kunden-, Konto- und Geldbewegungen.

## Sprint 4: API and Ops

### O-001 OpenAPI

- Add `springdoc-openapi-starter-webmvc-ui`
- Document endpoints
- Expose Swagger UI

### O-002 Docker Compose

Services:

- backend
- frontend
- postgres

### O-003 CI Pipeline

Steps:

- Java build
- Java tests
- Angular build
- optional Docker build

## Sprint 5: Polish and Scale

- CSV export
- transaction filters
- account status: ACTIVE, BLOCKED, CLOSED
- daily transfer limits
- notification center
- admin audit screen
- metrics dashboard

## Priority Order

1. Keycloak oder Azure Entra ID Demo Realm/App Registration
2. Frontend OIDC Client Flow
3. O-001
4. O-002
5. O-003
6. CSV Export
7. Daily Limits
