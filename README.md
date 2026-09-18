# Bank Platform

Investor-ready Banking-MVP mit Spring Boot Backend und Angular Frontend.

## Vision

Eine moderne Bankkonto-Verwaltung für Privatkunden und Bankmitarbeiter: Konten eröffnen, Kontostände prüfen, Geldbewegungen ausführen, Transaktionen nachvollziehen und später sicher mit Login, Rollen, Audit-Log und PostgreSQL betreiben.

## Aktueller Stand

- Spring Boot 3.5 Backend mit Java 21
- Angular 18 Frontend
- Modulare Backend-Architektur mit Ports und Adaptern
- In-Memory-Infrastruktur für schnelle MVP-Entwicklung
- PostgreSQL-/JPA-Adapter über Profil `postgres`
- Flyway-Datenbankschema und Docker Compose
- REST-API für Kunden, Konten, Buchungen, Überweisungen und Transaktionen
- Einheitliche Fehlerantworten mit `ProblemDetail`
- Spring Security mit Demo-Rollen `CUSTOMER`, `EMPLOYEE`, `ADMIN`
- Audit-Log für Kunden-, Konto- und Geldbewegungen
- Java-Service-Tests für zentrale Fachregeln

## Projektstruktur

```text
src/main/java/bank/
  api/                  REST API und DTOs
  config/               technische Spring-Konfiguration
  domain/               Fachmodell und Geschäftsregeln
  service/              Use Cases
  service/port/         Repository-Ports
  infrastructure/       Adapter, aktuell In-Memory

frontend/src/app/
  accounts/             Angular Konto-Feature
```

## Start

Backend:

```powershell
mvn spring-boot:run
```

Backend mit PostgreSQL:

```powershell
docker compose up -d postgres
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

Backend mit OIDC/JWT Resource Server:

```powershell
$env:OIDC_ISSUER_URI = "http://localhost:8081/realms/bank-platform"
mvn spring-boot:run -Dspring-boot.run.profiles=memory,oidc
```

Für OIDC/JWT muss ein echter Identity Provider laufen, zum Beispiel Keycloak, Azure Entra ID oder Auth0. Tokens müssen Rollen als `roles`-Claim mit Werten wie `CUSTOMER`, `EMPLOYEE` oder `ADMIN` enthalten.

Frontend:

```powershell
cd frontend
npm install
npm start
```

URLs:

```text
Backend:  http://localhost:8080
Frontend: http://localhost:4200
```

Das Standardprofil ist `memory`, damit die App ohne Datenbank sofort startet. Für dauerhafte Daten nutze das Profil `postgres`.

## Demo-Logins

```text
employee / employee123   Kontoeröffnung und Buchungen
customer / customer123   Konten lesen und Überweisungen ausführen
admin    / admin123      Admin-Rechte und Audit-Log
```

Wenn ein Mitarbeiter ein Konto für den Demo-Kunden anlegt, muss `ownerUsername` auf `customer` gesetzt werden. Danach sieht der Login `customer` nur dieses eigene Konto.

Für lokale Demos nutzt die App Basic Auth. Für produktionsnahe Umgebungen ist das Profil `oidc` vorbereitet und erwartet JWTs von einem Identity Provider.

## Wichtigste Dokumente

- [Architecture](ARCHITECTURE.md)
- [Top-Tier MVP Plan](docs/TOP_TIER_MVP_PLAN.md)
- [Feature Catalog](docs/FEATURE_CATALOG.md)
- [Engineering Playbook](docs/ENGINEERING_PLAYBOOK.md)
- [Quality and Test Strategy](docs/QUALITY_AND_TEST_STRATEGY.md)
- [Investor Demo Plan](docs/INVESTOR_DEMO_PLAN.md)
- [Technical Decisions](docs/TECHNICAL_DECISIONS.md)
- [24/7 Deployment Plan](docs/DEPLOYMENT_24_7_PLAN.md)
- [Public Link Setup](docs/PUBLIC_LINK_SETUP.md)
- [Render Launch Checklist](docs/RENDER_LAUNCH_CHECKLIST.md)

## Deployment-Dateien

- [render.yaml](render.yaml)
- [docker-compose.yml](docker-compose.yml)
- [Dockerfile](Dockerfile)
- [frontend/Dockerfile](frontend/Dockerfile)
- [frontend/nginx.conf](frontend/nginx.conf)

