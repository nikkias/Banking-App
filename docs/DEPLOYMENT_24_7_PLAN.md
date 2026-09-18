# 24/7 Deployment Plan

## Aktueller Deploy-Stand

Die App ist jetzt für Container-Deployment vorbereitet:

- Backend Dockerfile
- Frontend Dockerfile mit Nginx
- Nginx Proxy von `/api` zum Backend
- Docker Compose für PostgreSQL, Backend und Frontend
- PostgreSQL-Profil im Backend
- OIDC/JWT-Profil für produktionsnahe Authentifizierung

Lokal kann der Stack mit einem Befehl gestartet werden:

```powershell
docker compose up --build
```

Danach:

```text
Frontend: http://localhost:4200
Backend:  http://localhost:8080
```

## Was ich im Repo erledigen kann

- Dockerfiles erstellen
- Docker Compose erstellen
- Produktionsprofile vorbereiten
- Datenbankmigrationen ergänzen
- Health-Checks ergänzen
- CI/CD YAML vorbereiten
- Deployment-Dokumentation schreiben
- API/Frontend so umbauen, dass sie hinter Domain und HTTPS laufen
- OpenAPI, Monitoring, Audit und Tests ergänzen

## Was ich ohne externe Zugänge nicht selbst erledigen kann

- Cloud-Account anlegen
- Zahlungsdaten hinterlegen
- Domain kaufen oder DNS ändern
- Secrets/API-Keys erzeugen
- Azure/GitHub/Vercel/Render/Railway Login durchführen
- Einen echten öffentlichen 24/7-Link live schalten
- Produktive OIDC-App-Registration in Azure Entra ID/Auth0/Keycloak Cloud anlegen

Dafür brauche ich eine Zielplattform und ggf. deine Anmeldung im Browser oder Terminal.

## Beste Hosting-Optionen

### Option A: Render/Railway/Fly.io

Gut für schnelles Investor-MVP.

Vorteile:

- schnell online
- HTTPS automatisch
- Postgres leicht dazu
- GitHub-Deploy einfach

Nachteile:

- Free-Tier kann schlafen oder limitiert sein
- weniger Enterprise-Feeling

Empfehlung: sehr gut für erste öffentliche Demo.

### Option B: Azure Container Apps

Beste professionelle Option, wenn du Microsoft/Azure zeigen willst.

Vorteile:

- production-nah
- Container-native
- Managed HTTPS
- Skalierung möglich
- Azure Database for PostgreSQL passt dazu
- später Entra ID möglich

Nachteile:

- Azure Account und Kosten nötig
- etwas mehr Setup

Empfehlung: beste Investor-/Enterprise-Richtung.

### Option C: VPS mit Docker Compose

Gut, wenn du volle Kontrolle willst.

Vorteile:

- günstig
- läuft 24/7
- Compose direkt nutzbar

Nachteile:

- du verwaltest Server, Updates, Firewall, Backups, TLS
- mehr Betriebsverantwortung

Empfehlung: gut zum Lernen, nicht ideal für seriöse Bankprodukt-Demo ohne Ops-Erfahrung.

### Option D: Frontend Vercel/Netlify + Backend Render/Azure

Gut für schöne Frontend-Demo.

Vorteile:

- Frontend sehr einfach
- Preview Links
- HTTPS automatisch

Nachteile:

- Backend/CORS/Secrets sauber konfigurieren
- zwei Deployments

Empfehlung: gut, wenn UI im Fokus steht.

## Meine Empfehlung

Für dein Ziel `investorentaugliches MVP mit 24/7 Link`:

1. GitHub Repository erstellen.
2. Docker Compose lokal testen.
3. Backend + Postgres auf Azure Container Apps oder Render deployen.
4. Frontend als Container oder Static Site deployen.
5. HTTPS-Domain aktivieren.
6. OIDC später über Azure Entra ID oder Keycloak ergänzen.

Kurz:

```text
Schnellster Demo-Link: Render/Railway
Professionellste Richtung: Azure Container Apps + Azure PostgreSQL
```

## Was noch fehlt für Top-Tier World App

### Produkt

- echte Onboarding-Story
- Demo-Daten automatisch seedbar
- Kontodetail UX polieren
- Transaktionsfilter, Suche, Export
- Admin Audit Screen als eigene Seite
- verständliche Fehlermeldungen je Problem

### Backend

- OpenAPI/Swagger
- echte `404 Not Found` Exceptions statt allgemeiner `IllegalArgumentException`
- Testcontainers für PostgreSQL
- Audit für fehlgeschlagene Aktionen
- Tageslimits und Konto-Status
- OIDC-Integration mit echtem Provider
- Rate Limiting
- strukturierte Logs
- Health/Readiness Checks

### Frontend

- Route Guards
- OIDC Client Flow
- dedizierte Layout-Komponenten
- Toasts/Notifications
- Skeleton Loading
- Transaction filters
- Admin audit route
- bessere Formularfehlermeldungen
- E2E Tests

### Betrieb

- CI/CD Pipeline
- Container Registry
- HTTPS
- Secrets Management
- Backups für PostgreSQL
- Monitoring/Alerting
- Log Aggregation
- Uptime Check

### Security

- Basic Auth aus Produktion entfernen
- OIDC/JWT erzwingen
- Rollen aus Identity Provider mappen
- Account Ownership überall prüfen
- CSRF/CORS production-hardening
- Security Headers
- Audit Trail gegen Manipulation schützen

## 24/7 Go-Live Checklist

- GitHub Repo ist remote verfügbar
- `docker compose up --build` läuft lokal
- `mvn test` ist grün
- `npm run build` ist grün
- Secrets sind nicht im Git
- Postgres Passwort über Environment Variable
- HTTPS aktiv
- Domain gesetzt
- Health Endpoint verfügbar
- Backup-Plan für Datenbank
- Demo-Logins oder OIDC User vorbereitet

## Entscheidung, die ich von dir brauche

Wähle eine Zielplattform:

1. Render/Railway/Fly.io für schnellsten Demo-Link
2. Azure Container Apps für professionelle Cloud-Richtung
3. VPS mit Docker Compose für maximale Kontrolle
4. Vercel/Netlify Frontend plus separater Backend-Host

Danach kann ich die passenden Deployment-Dateien exakt für diese Plattform erstellen.

Die konkrete Schritt-für-Schritt-Anleitung zum öffentlichen Link steht in [PUBLIC_LINK_SETUP.md](PUBLIC_LINK_SETUP.md).
