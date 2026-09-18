# Bank Platform

## Zielarchitektur

Das Projekt ist als modularer Monolith aufgebaut. Backend und Frontend sind getrennt deploybar, die fachlichen Regeln liegen aber zentral im Backend.

```text
frontend/                         Angular UI
  src/app/accounts/               Konto-Feature

src/main/java/bank/
  config/                         technische Spring-Konfiguration
  api/                            REST-Controller und DTOs
  service/                        Anwendungsfälle und Transaktionen
  service/port/                   Repository-Ports für austauschbare Infrastruktur
  domain/                         Fachobjekte und Geschäftsregeln
  infrastructure/memory/           schneller lokaler Adapter im Profil `memory`
  infrastructure/jpa/              PostgreSQL-Adapter im Profil `postgres`
```

### Abhängigkeitsrichtung

`api -> service -> domain`

`service -> service/port <- infrastructure`

Die Domäne kennt weder Spring noch HTTP. Der Service orchestriert Geschäftsprozesse. Die API validiert Eingaben und formt stabile Response-DTOs. Persistenz und externe Systeme liegen hinter Ports. Der In-Memory-Speicher bleibt für schnelle Entwicklung, PostgreSQL läuft über einen JPA-Adapter im Profil `postgres`.

## Aktueller Stand

- Kunden registrieren
- Konten eröffnen
- Kontenübersicht im Angular-Frontend
- Einzahlung und Auszahlung im Angular-Frontend
- Überweisungen im Angular-Frontend
- Kontodetails und Buchungshistorie im Angular-Frontend
- Einheitliche API-Fehler mit `ProblemDetail`
- JUnit-Tests für zentrale Fachregeln
- PostgreSQL-Datenbankschema mit Flyway
- Docker Compose für lokale PostgreSQL-Datenbank
- Spring Security mit Demo-Basic-Login und Rollen
- Audit-Events für Kunden-, Konto- und Geldbewegungen
- Eigentümerprüfung: `CUSTOMER` sieht und nutzt nur eigene Konten
- OIDC/JWT Resource-Server-Profil für produktionsnahe Authentifizierung
- Container-Deployment für Backend, Frontend und PostgreSQL vorbereitet

## API-Module

```text
GET  /api/accounts
POST /api/customers
POST /api/accounts
POST /api/accounts/{accountId}/deposit
POST /api/accounts/{accountId}/withdraw
POST /api/transfers
GET  /api/accounts/{accountId}/transactions
```

Fehler werden als RFC-7807-ähnliche `ProblemDetail`-Antworten geliefert. Ungültige Eingaben erzeugen `400 Bad Request`, fachliche Konflikte wie zu niedriger Kontostand erzeugen `409 Conflict`.

## Entwicklungs­module

### Modul 1: Fundament

- Spring Boot 3 und Java 21
- Angular Standalone Application
- CORS und lokale Entwicklungsprofile
- Fehlerbehandlung und einheitliches API-Format

### Modul 2: Kunden und Konten

- Kunden anlegen
- Girokonto eröffnen
- Kontenübersicht
- IBAN-Erzeugung mit echter Prüfziffer

### Modul 3: Geldbewegungen

- Einzahlen und abheben
- Überweisungen zwischen eigenen Konten
- Buchungshistorie
- Fachliche Regeln für Saldo, Betrag und Beschreibung

### Modul 4: Persistenz

- PostgreSQL
- Spring Data JPA und Flyway-Migrationen
- Optimistisches Locking gegen parallele Kontobewegungen
- Repository-Interfaces je Aggregat

### Modul 5: Sicherheit

- Spring Security und OAuth2/OIDC
- Rollen `CUSTOMER`, `EMPLOYEE`, `ADMIN`
- Eigentümerprüfung für Konten
- Audit-Log für sensible Aktionen

### Modul 6: Frontend-Produkt

- Dashboard mit Kontoständen
- Konto- und Transaktionsdetails
- Überweisungsformular
- Loading-, Fehler- und Empty-States
- Route Guards und Session-Verwaltung

### Modul 7: Qualität und Betrieb

- Unit- und Integrationstests mit Testcontainers
- OpenAPI-Dokumentation
- CI-Pipeline
- Docker Compose für PostgreSQL, Backend und Frontend
- Observability mit strukturierten Logs, Metriken und Tracing

## Nächste beste Ausbaureihenfolge

1. Keycloak/Azure-Entra-Demo-Konfiguration für das `oidc`-Profil ergänzen.
2. Audit-Log im PostgreSQL-Profil in der Demo verifizieren.
3. OpenAPI/Swagger für die REST-API erzeugen.
4. Backend und Frontend in Docker Compose ergänzen.
5. CSV-Export und Tageslimits ergänzen.

Die detaillierte Umsetzungsreihenfolge steht in [docs/IMPLEMENTATION_BACKLOG.md](docs/IMPLEMENTATION_BACKLOG.md). Architekturentscheidungen stehen in [docs/TECHNICAL_DECISIONS.md](docs/TECHNICAL_DECISIONS.md).

## Teststrategie

- Domänentests prüfen Beträge, Saldo und Fachregeln.
- Servicetests prüfen komplette Use Cases ohne HTTP.
- Controller-Tests prüfen Statuscodes, Validierung und Fehlerformat.
- Frontend-Tests prüfen Formularvalidierung, Ladezustände und API-Interaktionen.
- Integrationstests prüfen später Backend plus PostgreSQL mit Testcontainers.

## Lokale Entwicklung

Backend (nach Installation von Maven):

```text
mvn spring-boot:run
```

Backend mit PostgreSQL:

```text
docker compose up -d postgres
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

Frontend (nach Installation von Node.js):

```text
cd frontend
npm install
npm start
```

Die Angular-Anwendung läuft auf `http://localhost:4200`, das Backend auf `http://localhost:8080`.

## Container-Start

```text
docker compose up --build
```

Der 24/7-Deployment-Plan steht in [docs/DEPLOYMENT_24_7_PLAN.md](docs/DEPLOYMENT_24_7_PLAN.md).