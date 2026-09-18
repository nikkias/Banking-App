# Investor Demo Plan

## Demo-Ziel

In 5 bis 7 Minuten zeigen, dass die Bank Platform mehr ist als eine Übung: ein sauber strukturiertes Banking-MVP mit echtem Produktfluss, stabiler Architektur und klarer Roadmap.

## Demo-Setup

```powershell
mvn spring-boot:run
cd frontend
npm start
```

Später mit Docker:

```powershell
docker compose up --build
```

## Demo-Story

### 1. Produktproblem

Kleine Banken, Schulungsbanken oder FinTech-Prototypen brauchen eine klare Konto-Verwaltung, die schnell demonstrierbar ist und trotzdem professionell wachsen kann.

### 2. Live Flow

1. Dashboard öffnen.
2. Bestehende Konten zeigen.
3. Neuen Kunden und Konto anlegen.
4. Einzahlung durchführen.
5. Auszahlung durchführen.
6. Zu hohe Auszahlung versuchen und saubere Fehlermeldung zeigen.
7. Kontodetailseite öffnen.
8. Transaktionshistorie erklären.
9. Überweisung zwischen Konten ausführen.
10. Als `admin` einloggen und Audit-Log zeigen.
11. Als `customer` einloggen und zeigen, dass nur eigene Konten sichtbar sind.

### 3. Technik erklären

Kurz und stark:

```text
Angular UI -> REST API -> Use Cases -> Domain -> Repository Ports -> Adapter
```

Der wichtigste Satz:

> Die Geschäftsregeln hängen nicht an der Datenbank und nicht am Frontend. Deshalb kann das MVP schnell wachsen, ohne bei jedem Feature neu gebaut zu werden.

### 4. Warum investierbar

- Die Domäne ist testbar.
- Die API ist erweiterbar.
- Persistenz, Security und Audit sind im MVP technisch sichtbar.
- Das Produkt hat einen sichtbaren Nutzerfluss.
- Die Roadmap zeigt, was MVP, Beta und Production bedeutet.

## Demo-Risiken und Gegenmaßnahmen

| Risiko | Gegenmaßnahme |
| --- | --- |
| Backend startet nicht | Build vor Demo ausführen |
| Frontend findet API nicht | Environment prüfen |
| Keine Daten sichtbar | Seed-Datenmodul bauen |
| Fehler wirkt technisch | `ProblemDetail` + UI-Fehlermeldung nutzen |
| Reviewer fragt nach Sicherheit | Security-Modul und Audit-Roadmap zeigen |
| Reviewer fragt nach Persistenz | PostgreSQL-Port/Adapter-Architektur zeigen |

## Investor-Fragen vorbereiten

### Wie wird das produktionsreif?

Mit PostgreSQL, Security, Audit, CI/CD, Docker, Monitoring und Integrationstests. Die Architektur ist bereits darauf vorbereitet.

### Kann das skalieren?

Das MVP ist ein modularer Monolith. Das ist für frühe Produkte richtig: einfach zu betreiben, aber sauber getrennt. Wenn einzelne Module wachsen, können Konten, Transaktionen oder Auth später als Services getrennt werden.

### Wie verhindert ihr falsche Geldbewegungen?

Durch zentrale Domain-Regeln, Service-Tests, Datenbanktransaktionen, Optimistic Locking und Audit Events.

### Was ist der nächste wichtigste Schritt?

Eigentümerprüfung je Konto, OpenAPI/Swagger und ein vollständiger Docker-Stack.

## Demo Done Checklist

- Backend build grün
- Tests grün
- Frontend build grün
- Seed-Daten vorhanden
- Demo-Skript einmal trocken durchlaufen
- Browser auf Dashboard bereit
- API-Doku bereit
- Architekturdiagramm bereit
