# Engineering Playbook

## Team-Modell

### Product Lead

- definiert MVP Scope
- priorisiert Features
- schützt den Demo-Flow
- entscheidet, was nicht gebaut wird

### Backend Lead

- schützt Domain und Use Cases
- verantwortet API-Verträge
- entscheidet Port/Adapter-Schnittstellen
- baut Persistenz, Security und Tests

### Frontend Lead

- verantwortet UI-Komponenten und State
- hält Feature-Module klein
- baut Fehler-, Lade- und Empty-States
- achtet auf responsive Darstellung

### QA Lead

- definiert Testpyramide
- baut Akzeptanztests
- prüft Regressionen
- verwaltet Demo-Checkliste

### DevOps Lead

- Docker Compose
- CI Pipeline
- Secrets-Konzept
- Health, Logs, Metrics

## Repository-Regeln

- Domain hat keine Spring-Imports.
- Controller enthalten keine Geschäftslogik.
- Services kennen Ports, nicht konkrete Datenbankklassen.
- Infrastructure implementiert Ports.
- Frontend spricht nur über Services mit der API.
- DTOs sind API-Verträge und dürfen nicht zufällig geändert werden.
- Tests werden mit jedem relevanten Feature ergänzt.

## Backend-Standards

### Packages

```text
bank.api
bank.config
bank.domain
bank.service
bank.service.port
bank.infrastructure.memory
bank.infrastructure.jpa       später
bank.security                  später
bank.audit                     später
```

### Naming

- Use Cases: `BankService` oder später spezifischer `AccountService`, `TransferService`.
- Ports: `AccountRepository`, `AuditLogRepository`.
- Adapter: `JpaAccountRepositoryAdapter`, `InMemoryAccountRepository`.
- API DTOs: `CreateCustomerRequest`, `AccountResponse`.

### Error Handling

- Validation: `400 Bad Request`.
- Fachlicher Konflikt: `409 Conflict`.
- Nicht gefunden: später `404 Not Found` mit eigener Exception.
- Unerwarteter Fehler: `500 Internal Server Error`, keine Details im Frontend.

## Frontend-Standards

### Struktur

```text
src/app/
  core/             API client, interceptors, auth, shared errors
  accounts/         Konto-Feature
  transactions/     später: Buchungen und Export
  dashboard/        später: KPI-Übersicht
  layout/           Shell, Navigation
```

### State

Jede größere View hat explizit:

```text
loading
error
data
empty
```

### UI-Regeln

- Kein Text überlappt auf Mobile.
- Formulare haben klare Labels und Fehlermeldungen.
- Kritische Aktionen zeigen sofortiges Feedback.
- Investor-Demo darf nicht von zufälliger Datenlage abhängen.

## Branch- und Commit-Modell

Für ein kleines Team:

```text
main                 immer präsentierbar
feature/postgres     Datenbankadapter
feature/security     Login und Rollen
feature/dashboard    UI-Ausbau
```

Commit-Stil:

```text
feat(accounts): add transfer endpoint
fix(api): return problem detail for insufficient funds
test(service): cover withdrawal rule
docs(plan): add investor demo flow
```

## Code Review Checkliste

- Ist die Fachregel an der richtigen Stelle?
- Ist der API-Vertrag stabil?
- Gibt es Tests für Erfolg und Fehler?
- Kann die Änderung später mit PostgreSQL funktionieren?
- Bleibt die UI bei Fehlern bedienbar?
- Sind Security- oder Audit-Aspekte betroffen?

## Definition of Done

- Feature ist implementiert.
- Tests laufen.
- Build läuft.
- Fehlerfälle sind behandelt.
- Dokumentation ist aktualisiert.
- Demo-Flow bleibt intakt.
