# Quality and Test Strategy

## Qualitätsziel

Die App soll nicht nur funktionieren, sondern erklärbar, testbar und vorführbar sein. Für Banking bedeutet Qualität: korrekte Geldregeln, nachvollziehbare Transaktionen, sichere Zugriffe und reproduzierbare Builds.

## Testpyramide

```text
E2E Demo Tests               wenige, kritisch
API Integration Tests        wichtige HTTP-Flows
Service Tests                viele Use Cases
Domain Tests                 viele Fachregeln
Static Checks                Build, TypeScript, Lint
```

## Backend Tests

### Domain Tests

Pflichtfälle:

- Einzahlung erhöht Saldo.
- Auszahlung reduziert Saldo.
- Negative Beträge werden abgelehnt.
- Null-Beträge werden abgelehnt.
- Auszahlung über Saldo wird abgelehnt.

### Service Tests

Pflichtfälle:

- Kunde kann Konto eröffnen.
- Kontoeröffnung ohne Kunde wird abgelehnt.
- Überweisung verändert zwei Konten korrekt.
- Überweisung auf dasselbe Konto wird abgelehnt.
- Jede Buchung erzeugt Transaktion.

### Controller Tests

Pflichtfälle:

- `POST /api/customers` gibt `201`.
- `POST /api/accounts` gibt `201`.
- Ungültiger Betrag gibt `400`.
- Zu hohe Auszahlung gibt `409`.
- Fehlerformat enthält `title`, `detail`, `status`, `path`.

### Integration Tests

Ab Persistenzmodul:

- Testcontainers PostgreSQL
- Flyway Migration läuft
- Konto bleibt nach Repository-Neuladen erhalten
- Optimistic Locking verhindert parallele Saldo-Fehler

## Frontend Tests

### Component Tests

- Kontoformular validiert Pflichtfelder.
- Kontenliste zeigt Empty-State.
- Kontenliste zeigt Saldo und IBAN.
- Buchungsformular verhindert leeren Betrag.
- API-Fehler wird als Nutzerfeedback angezeigt.

### E2E Tests

Minimaler Investor-Flow:

1. App öffnet Dashboard.
2. Neues Konto wird erstellt.
3. Einzahlung wird durchgeführt.
4. Auszahlung wird durchgeführt.
5. Ungültige Auszahlung zeigt Fehler.
6. Transaktionshistorie wird angezeigt.

## Quality Gates

### Gate 1: Local Build

```powershell
mvn test
cd frontend
npm run build
```

### Gate 2: API Contract

- Alle Endpunkte in OpenAPI sichtbar.
- DTO-Felder sind stabil.
- Fehler verwenden `ProblemDetail`.

### Gate 3: Security

- Kein Endpunkt für Kontodaten ohne Auth, sobald Security aktiv ist.
- Kunden sehen nur eigene Konten.
- Mitarbeiteraktionen werden auditiert.

### Gate 4: Demo Readiness

- Stack startet mit einem Befehl.
- Seed-Daten vorhanden.
- Demo-Flow dauert unter 5 Minuten.
- Fehlerfall kann gezielt gezeigt werden.

## Performance-Ziele MVP

- API-Liste Konten unter 300 ms lokal.
- Buchung unter 300 ms lokal.
- Frontend Initial Bundle unter 500 kB Warnung.
- Keine N+1-Queries im Persistenzmodul.

## Security-Ziele MVP

- Passwörter nie selbst speichern, wenn OIDC genutzt wird.
- Demo-Login nur im lokalen Profil.
- Keine Secrets im Git.
- CORS nur für erlaubte Origins.
- Audit für Geldbewegungen.

## Testdaten

Demo-Seed:

- 3 Kunden
- 5 Konten
- 20 Transaktionen
- 1 Fehlerfall: Auszahlung über Saldo
- 1 Admin, 1 Mitarbeiter, 1 Kunde ab Security-Modul
