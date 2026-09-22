# Module Guide

Dieses Dokument erklärt die wichtigsten Module der Bank Platform so, dass das Projekt in Demo, Review und Bewerbungsgespräch schnell verständlich ist.

## Executive Summary

Bank24 ist ein Fullstack-Banking-MVP mit Spring Boot Backend und Angular Frontend. Der Kern ist bewusst als modularer Monolith gebaut: fachliche Regeln liegen zentral im Backend, technische Adapter bleiben austauschbar, und das Frontend zeigt eine klare Customer- und Operations-Journey.

## Backend-Module

### `bank.BankApplication`

Startpunkt der Spring-Boot-Anwendung. Dieses Modul lädt Konfiguration, REST-Controller, Services und Adapter abhängig vom aktiven Profil.

### `bank.api`

REST-Schicht für HTTP, Validierung und stabile API-Antworten. Hier liegen Controller, Request-/Response-DTOs und die zentrale Fehlerbehandlung. Das Modul entscheidet nicht über Geldregeln, sondern übersetzt Web-Anfragen in Use Cases.

Wichtige Endpunkte:

- `GET /api/accounts`: Konten lesen, rollen- und eigentümerabhängig.
- `POST /api/customers`: Kunden anlegen.
- `POST /api/accounts`: Konto eröffnen.
- `POST /api/accounts/{accountId}/deposit`: Einzahlung buchen.
- `POST /api/accounts/{accountId}/withdraw`: Auszahlung buchen.
- `POST /api/transfers`: Überweisung ausführen.
- `GET /api/accounts/{accountId}/transactions`: Buchungshistorie lesen.
- `GET /api/audit-events`: Audit-Log für Admins.

### `bank.domain`

Fachmodell der Plattform. `Customer`, `Account`, `Transaction` und `AuditEvent` beschreiben die Bankobjekte unabhängig von HTTP, Datenbank und Angular. Dieses Modul ist der wichtigste Beweis, dass das Projekt nicht nur aus CRUD-Code besteht.

### `bank.service`

Use-Case-Schicht für Geschäftsprozesse. `BankService` orchestriert Kontoeröffnung, Einzahlungen, Auszahlungen, Überweisungen, Transaktionshistorie, Eigentümerprüfung und Audit-Ereignisse.

### `bank.service.port`

Port-Schnittstellen für Persistenz. Die Services sprechen gegen `AccountRepository`, `CustomerRepository`, `TransactionRepository` und `AuditEventRepository`, ohne zu wissen, ob die Daten aus Memory oder PostgreSQL kommen.

### `bank.infrastructure.memory`

Schneller In-Memory-Adapter für lokale Demo und Entwicklung. Dieses Modul macht den MVP sofort startbar, ohne Datenbankinstallation.

### `bank.infrastructure.jpa`

PostgreSQL-Adapter für produktionsnahe Persistenz. JPA-Entities und Spring-Data-Repositories bleiben bewusst in der Infrastruktur und vermischen sich nicht mit dem Domain-Modell.

### `bank.config`

Technische Spring-Konfiguration: Security, OpenAPI, Demo-Daten und Bean-Verdrahtung. Dieses Modul macht Rollen, Swagger, lokale Startdaten und Profilverhalten sichtbar.

### `src/main/resources/db/migration`

Flyway-Migrationen für das Datenbankschema. Dieses Modul ist der Vertrag zwischen Anwendung und PostgreSQL.

## Frontend-Module

### `frontend/src/app/core/auth`

Login, Session-Verwaltung und Auth-Interceptor. Dieses Modul gibt der Demo Rollenbewusstsein und schützt API-Aufrufe über Basic Auth im lokalen Profil.

### `frontend/src/app/accounts`

Hauptproduktmodul für das Banking-Cockpit. Es zeigt Konten, Kennzahlen, Kontoeröffnung, Einzahlungen, Auszahlungen, Kontodetails und Transaktionen.

### `frontend/src/app/admin`

Admin-Modul für Audit-Ereignisse. Es zeigt Compliance- und Nachvollziehbarkeit, statt sensible Aktionen nur im Backend zu verstecken.

### `frontend/src/app/showcase`

Executive-Showcase für Reviewer, Investoren oder Bewerbungsgespräche. Diese Seite erklärt Produktwert, technische Reife, Module und Delivery-Fähigkeit kompakt sichtbar in der App.

### `frontend/src/environments`

Umgebungswerte wie Backend-API-URL. Dieses Modul trennt lokale Entwicklung und spätere Deployment-Konfiguration.

## Betriebs- und Deployment-Module

### `Dockerfile` und `frontend/Dockerfile`

Containerisierung von Backend und Frontend. Sie machen das Projekt deploybar und reproduzierbarer als ein rein lokales Setup.

### `docker-compose.yml`

Lokaler Infrastrukturstart, insbesondere PostgreSQL. Der Fullstack-Pfad ist damit auf echte Persistenz erweiterbar.

### `render.yaml`

Deployment-Vorbereitung für Render. Dieses Modul zeigt, dass der MVP nicht nur lokal gedacht ist.

### `docs/`

Projektkommunikation: Architektur, Feature-Katalog, MVP-Plan, Qualitätsstrategie, Launch-Checklisten und technische Entscheidungen. Für ein professionelles MVP ist diese Dokumentation Teil des Produkts.

## Testmodule

### `src/test/java/bank/service`

Service- und Fachregeltests. Sie prüfen zentrale Geldprozesse ohne HTTP und ohne Datenbank.

### `src/test/java/bank/api`

Controller- und Security-Tests. Sie sichern Rollen, Zugriffsschutz und HTTP-Verhalten ab.

## Warum diese Modulstruktur stark ist

- Fachlogik bleibt testbar und unabhängig von Frameworkdetails.
- In-Memory und PostgreSQL können über Ports ausgetauscht werden.
- Das Frontend erzählt eine echte Produktgeschichte statt nur API-Formulare zu zeigen.
- Security, Audit, OpenAPI und Deployment sind früh sichtbar.
- Die Dokumentation macht den MVP erklärbar, nicht nur lauffähig.

## Nächste Top-MVP-Verbesserungen

1. Fullstack-Docker-Compose mit Backend, Frontend und PostgreSQL als Ein-Befehl-Demo fertigstellen.
2. Frontend-Transferformular im Cockpit stärker hervorheben und mit Fehlerdetails verbinden.
3. Testcontainers-Integrationstest für das PostgreSQL-Profil ergänzen.
4. Audit-Events im UI filterbar machen.
5. CSV-Export für Transaktionen als Investor-Feature ergänzen.