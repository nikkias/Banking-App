# Top-Tier MVP Plan

## Produktziel

Die Bank Platform soll als professionelles Banking-MVP präsentierbar sein: klarer Kundennutzen, saubere Architektur, stabile Demo, nachvollziehbare Roadmap und genug technische Tiefe, damit Investoren, Dozenten oder technische Entscheider erkennen, dass das Projekt wachsen kann.

## MVP-North-Star

Ein Benutzer kann in einer sicheren, modernen Oberfläche Kunden und Konten verwalten, Geldbewegungen ausführen, Transaktionen nachvollziehen und die wichtigsten Kontorisiken sofort erkennen. Das System ist so geschnitten, dass Persistenz, Login, Audit und spätere Bankprodukte ohne Architekturbruch ergänzt werden können.

## Zielgruppen

- Privatkunde: Kontostand sehen, Geld einzahlen, auszahlen, überweisen, Historie prüfen.
- Bankmitarbeiter: Kunden anlegen, Konten eröffnen, Buchungen prüfen, Probleme erkennen.
- Administrator: Rollen, Systemeinstellungen, Audit und Betriebszustand verwalten.
- Investor/Reviewer: Demo sehen, technische Reife prüfen, Roadmap verstehen.

## Architekturprinzipien

1. Domain first: Geldregeln liegen in `domain`, nicht im Controller und nicht im Frontend.
2. Use-case first: `service` bildet Geschäftsprozesse wie Kontoeröffnung, Buchung und Überweisung ab.
3. Ports and adapters: Persistenz bleibt austauschbar. In-Memory heute, PostgreSQL morgen.
4. API contract stability: Frontend hängt an stabilen DTOs, nicht an internen Entities.
5. Testable by design: Use Cases müssen ohne HTTP und ohne Datenbank testbar bleiben.
6. Secure by default: Auth, Rollen, Ownership und Audit sind Pflicht für den Investor-MVP.
7. Operational visibility: Health, Logs, Metrics und Docker gehören zur präsentierbaren Version.

## Investor-MVP Scope

### Must Have

- Kundenanlage
- Kontoeröffnung
- Kontenübersicht
- Einzahlung
- Auszahlung
- Überweisung
- Transaktionshistorie
- Fehlerfälle mit klarer UI-Meldung
- PostgreSQL-Persistenz
- Login mit Rollen
- Audit-Log für Geldbewegungen
- OpenAPI/Swagger
- Docker Compose
- Backend-Tests und Frontend-Build

### Should Have

- Kontodetails-Seite
- Filterbare Transaktionsliste
- Demo-Daten beim Start im lokalen Profil
- Dashboard-Kennzahlen
- Export von Transaktionen als CSV
- Responsive UI für Tablet und Mobile

### Could Have

- Kartenverwaltung
- Daueraufträge
- Empfänger-Vorlagen
- Limits und Risikohinweise
- Benachrichtigungen
- Multi-Währung

### Not Yet

- Echte Zahlungsnetzwerke
- Echte KYC/AML-Anbieter
- Produktive Banklizenzen
- Reale Kundendaten
- Kreditentscheidung mit Machine Learning

## Release-Schnitte

### R0: Stabiler Kern

Ziel: Der aktuelle Code ist sauber, getestet und präsentierbar als technischer Prototyp.

- Ports/Adapter-Struktur ist vorhanden.
- REST-API liefert stabile Fehler.
- Angular UI kann Konten und Buchungen bedienen.
- Java-Tests laufen.

### R1: Persistenter MVP

Ziel: Neustart verliert keine Daten.

- PostgreSQL und Docker Compose
- Spring Data JPA Adapter hinter bestehenden Ports
- Flyway Migrationen
- Testcontainers Integrationstest
- Demo Seed Data im `dev` Profil

### R2: Sichere Banking-Demo

Ziel: Rollen und Eigentum sind sichtbar und technisch sauber.

- Spring Security
- JWT/OIDC oder lokaler Demo-Login
- Rollen `CUSTOMER`, `EMPLOYEE`, `ADMIN`
- Ownership-Prüfung bei Konten
- Audit-Log für alle Buchungen

### R3: Investor Frontend

Ziel: Eine Demo, die wie ein echtes Produkt wirkt.

- Dashboard
- Konto-Detailseite
- Transaktionshistorie mit Suche/Filter
- Überweisungsformular
- Error/Loading/Empty States
- Saubere mobile Darstellung

### R4: Betrieb und Qualität

Ziel: Das Projekt wirkt wie von einem professionellen Team gebaut.

- OpenAPI/Swagger
- CI Pipeline
- Docker Compose für Full Stack
- Health Checks
- strukturierte Logs
- Build- und Testbefehle dokumentiert

## Deep Implementation Order

1. Datenbankmodell definieren: customers, accounts, transactions, audit_events.
2. Flyway `V1__init_bank_schema.sql` erstellen.
3. JPA Entities als Infrastrukturmodelle anlegen, nicht als Domäne missbrauchen.
4. Mapper zwischen Domain und JPA bauen.
5. JPA-Repository-Adapter implementieren.
6. In-Memory Adapter nur für Tests und Demo behalten.
7. Service-Tests gegen Ports unverändert lassen.
8. Integrationstest gegen PostgreSQL/Testcontainers ergänzen.
9. Security-Konfiguration einführen.
10. Frontend-Routing ausbauen: Dashboard, Account Details, Transfer.
11. API-Client im Frontend in `core/api` zentralisieren.
12. UI State pro Feature definieren: loading, error, data, empty.
13. OpenAPI einbauen.
14. Docker Compose einbauen.
15. Demo-Skript und Seed-Daten fertigstellen.

## Definition of Investor Ready

- Ein neuer Entwickler kann das Projekt in unter 15 Minuten starten.
- Backend und Frontend bauen reproduzierbar.
- Die Demo funktioniert ohne manuelle Datenbankarbeit.
- Kritische Geldregeln sind getestet.
- API-Verträge sind dokumentiert.
- Fehler sehen in UI und API professionell aus.
- Architekturgrenzen sind sichtbar und erklärbar.
- Roadmap zeigt klar, was MVP, Beta und Production sind.
