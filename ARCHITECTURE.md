# Bank Platform

## Zielarchitektur

Das Projekt ist als modularer Monolith aufgebaut. Backend und Frontend sind getrennt deploybar, die fachlichen Regeln liegen aber zentral im Backend.

```text
frontend/                         Angular UI
  src/app/accounts/               Konto-Feature

backend/                          Spring Boot service module
  src/main/java/bank/
    config/                       technische Spring-Konfiguration
    api/                          REST-Controller und DTOs
    service/                      Anwendungsfälle und Transaktionen
    service/port/                 Repository-Ports für austauschbare Infrastruktur
    domain/                       Fachobjekte und Geschäftsregeln
    infrastructure/memory/        schneller lokaler Adapter im Profil `memory`
    infrastructure/jpa/           PostgreSQL-Adapter im Profil `postgres`
  src/main/resources/             Konfiguration und Flyway-Migrationen
  src/test/java/                  Unit-, Security- und PostgreSQL-Integrationstests

pom.xml                           Maven-Reaktor für JVM-Module
docker-compose.yml                lokale Plattform-Orchestrierung
docs/                             Architektur-, Betriebs- und Produktdokumentation
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
- CI-Workflow für Maven-Verifikation, Angular-Production-Build und Container-Build
- Transaktionssuche, Typ-/Datumsfilter, CSV-Export und druckbarer Kontoauszug
- CSV- und PDF-Export werden als `TRANSACTION_EXPORT` mit Nutzer, Konto, Format und Filterkontext auditiert
- Konfigurierbares Tageslimit für ausgehende Zahlungen über `BANK_DAILY_DEBIT_LIMIT`
- Prometheus-Metriken unter `/actuator/prometheus` und strukturierte JSON-Logs
- Durchgängige Korrelations-ID über `X-Correlation-Id` in Response-Headern, Fehlerantworten und strukturierten Logs
- Browser-Schutz durch Content-Security-Policy, Frame-Schutz, Referrer- und Permissions-Policy

## API-Module

```text
GET  /api/accounts
POST /api/customers
POST /api/accounts
POST /api/accounts/{accountId}/deposit
POST /api/accounts/{accountId}/withdraw
POST /api/transfers
GET  /api/accounts/{accountId}/transactions
GET  /api/accounts/{accountId}/transactions/page?page=0&size=50
```

Fehler werden als RFC-7807-ähnliche `ProblemDetail`-Antworten geliefert. Ungültige Eingaben erzeugen `400 Bad Request`, fachliche Konflikte wie zu niedriger Kontostand erzeugen `409 Conflict`.

Die paginierte Transaktionsschnittstelle begrenzt Antworten auf maximal 100 Einträge. Sie liefert `content`, `page`, `size` und `totalElements`; die Eigentümerprüfung gilt unverändert auch für paginierte Abfragen.
Sie akzeptiert optional `type`, `query`, `from` und `to`; Datumswerte sind UTC-ISO-8601-Instants. Ergebnisse werden stabil nach `timestamp DESC, id DESC` sortiert, damit Seiten bei gleicher Buchungszeit nachvollziehbar bleiben.
Das Angular-Kontodetail lädt 25 Buchungen je Seite und sendet Suche, Typ und Zeitraum als serverseitige Filter über den gesamten Kontoverlauf. CSV-Export und PDF-Druck beziehen sich auf die aktuell geladene, serverseitig gefilterte Seite; ein späterer Reporting-Release ergänzt vollständige asynchrone Export-Jobs.

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

1. Keycloak/Azure-Entra-Demo-Konfiguration für das `oidc`-Profil ergänzen und Basic Auth außerhalb lokaler Demos entfernen.
2. CI auf einen freigegebenen GitHub-hosted oder self-hosted Runner aktivieren, damit Maven, Testcontainers, Angular-Build und Container-Build als Pull-Request-Gate laufen.
3. Mandantenmodell `Organization -> Customer -> Account` mit tenant-scoped Repositories, Rollen und Datenmigration einführen.
4. Währungen, Locale, Zeitzonen und regulatorische Anforderungen als internationales Domänenmodell ergänzen.
5. Managed PostgreSQL, Secret Manager, HTTPS/WAF, Backups, Alerting und Disaster-Recovery-Tests für den Produktivbetrieb einrichten.

Die detaillierte Umsetzungsreihenfolge steht in [docs/IMPLEMENTATION_BACKLOG.md](docs/IMPLEMENTATION_BACKLOG.md). Architekturentscheidungen stehen in [docs/TECHNICAL_DECISIONS.md](docs/TECHNICAL_DECISIONS.md).

## Enterprise-Grenzen

Das Projekt ist ein sicherer, containerisierter Banking-MVP mit geprüften Fachregeln und vorbereiteten Produktionsschnittstellen. Ein internationaler Multi-Company-Produktivbetrieb benötigt jedoch Infrastruktur- und Governance-Entscheidungen, die nicht sicher als lokale Code-Defaults simuliert werden können: einen echten Identity Provider, eine Tenant- und Datenresidenzstrategie, lokale Compliance-Vorgaben, verwaltete Secrets, Monitoring-/Alerting-Ziele sowie Recovery-Ziele. Diese Entscheidungen werden vor der Datenmodell-Migration und dem Go-Live verbindlich festgelegt.

## Observability-Vertrag

Clients dürfen eine sichere Korrelations-ID über `X-Correlation-Id` senden. Die Plattform übernimmt gültige Werte, erzeugt andernfalls eine ID und liefert sie immer im Response-Header zurück. RFC-7807-Fehlerantworten enthalten zusätzlich das Feld `correlationId`. Damit lassen sich Support-Fälle, Audit-Untersuchungen und spätere OpenTelemetry-Traces eindeutig über Browser, API und Log-Aggregation verbinden.

## Browser-Sicherheitsvertrag

Nginx schützt die Angular-Anwendung mit einer restriktiven Content-Security-Policy. Frontend und API verweigern Einbettung in fremde Frames, blockieren unsichere MIME-Interpretation, minimieren Referrer-Daten und deaktivieren nicht benötigte Browser-Funktionen wie Kamera, Standort, Mikrofon und Payment APIs.

## Export- und Compliance-Vertrag

Vor einem CSV-Download oder PDF-Druck erfasst das Frontend einen autorisierten Export-Event über die API. Die Eigentümerprüfung entspricht der Kontohistorie; ein Audit-Event enthält den Akteur, das Konto, das Format und eine begrenzte Zusammenfassung der gewählten Filter. Dadurch bleiben Datenweitergaben für Revision und Kundenservice nachvollziehbar.

## Teststrategie

- Domänentests prüfen Beträge, Saldo und Fachregeln.
- Servicetests prüfen komplette Use Cases ohne HTTP.
- Controller-Tests prüfen Statuscodes, Validierung und Fehlerformat.
- Frontend-Tests prüfen Formularvalidierung, Ladezustände und API-Interaktionen.
- Integrationstests prüfen später Backend plus PostgreSQL mit Testcontainers.

## Lokale Entwicklung

Backend (nach Installation von Maven):

```text
mvn --projects backend spring-boot:run
```

Backend mit PostgreSQL:

```text
docker compose up -d postgres
mvn --projects backend spring-boot:run -Dspring-boot.run.profiles=postgres
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