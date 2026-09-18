# Feature Catalog

## Feature Matrix

| ID | Feature | Nutzer | Priorität | Status | Akzeptanzkriterium |
| --- | --- | --- | --- | --- | --- |
| F-001 | Kunden anlegen | Mitarbeiter | P0 | Implementiert | Kunde wird mit ID und Name erzeugt |
| F-002 | Konto eröffnen | Mitarbeiter | P0 | Implementiert | Konto hat IBAN, Kundenzuordnung und Startsaldo |
| F-003 | Konten anzeigen | Kunde/Mitarbeiter | P0 | Implementiert | Liste zeigt IBAN, ID und Saldo |
| F-004 | Einzahlung | Mitarbeiter | P0 | Implementiert | Saldo erhöht sich, Transaktion entsteht |
| F-005 | Auszahlung | Mitarbeiter | P0 | Implementiert | Saldo sinkt, zu hoher Betrag wird abgelehnt |
| F-006 | Überweisung | Kunde/Mitarbeiter | P0 | Implementiert | Quelle sinkt, Ziel steigt, zwei Buchungen entstehen |
| F-007 | Transaktionshistorie | Kunde/Mitarbeiter | P0 | Implementiert | Buchungen sind je Konto abrufbar |
| F-008 | Kontodetails | Kunde/Mitarbeiter | P1 | Implementiert | Detailseite zeigt Saldo und Historie |
| F-009 | PostgreSQL Persistenz | System | P0 | Offen | Daten bleiben nach Neustart erhalten |
| F-010 | Audit Log | Admin/Compliance | P0 | Implementiert | Jede wichtige Aktion erzeugt Audit-Eintrag |
| F-011 | Login | Alle | P0 | Implementiert | Nutzer meldet sich an und erhält Rolle |
| F-012 | Rollenprüfung | System | P0 | Implementiert | Kunden sehen nur eigene Konten |
| F-013 | OpenAPI | Entwickler | P1 | Implementiert | Swagger UI zeigt alle Endpunkte |
| F-014 | Docker Compose | Entwickler | P1 | Offen | Stack startet mit einem Befehl |
| F-015 | Demo Seed Data | Investor/Reviewer | P1 | Offen | Demo enthält realistische Startdaten |
| F-016 | CSV Export | Kunde/Mitarbeiter | P2 | Offen | Transaktionen können exportiert werden |
| F-017 | Limits | Kunde/Mitarbeiter | P2 | Offen | Tageslimit verhindert zu hohe Abgänge |
| F-018 | Notifications | Kunde | P3 | Offen | Nutzer sieht Buchungsbenachrichtigungen |
| F-019 | Admin Audit Center | Admin/Compliance | P0 | Implementiert | Audit-Events sind in eigener Admin-Seite sichtbar |

## MVP User Journeys

### Journey 1: Kontoeröffnung

1. Mitarbeiter öffnet die Angular-App.
2. Mitarbeiter gibt Vorname, Nachname und Startguthaben ein.
3. System erstellt Kunde und Konto.
4. Konto erscheint sofort in der Übersicht.
5. Startguthaben wird als Transaktion protokolliert.

### Journey 2: Auszahlung mit Fachregel

1. Mitarbeiter wählt ein Konto.
2. Mitarbeiter gibt Auszahlungsbetrag ein.
3. System prüft positiven Betrag.
4. System prüft ausreichenden Saldo.
5. Bei Erfolg sinkt der Saldo.
6. Bei Fehler wird `409 Conflict` mit professioneller UI-Meldung angezeigt.

### Journey 3: Überweisung

1. Nutzer wählt Quellkonto und Zielkonto.
2. Nutzer gibt Betrag und Beschreibung ein.
3. System verhindert gleiche Quelle und gleiches Ziel.
4. System bucht `TRANSFER_OUT` auf Quelle.
5. System bucht `TRANSFER_IN` auf Ziel.
6. Beide Kontostände sind sofort aktualisiert.

### Journey 4: Investor Demo

1. Backend und Frontend starten.
2. Demo zeigt Dashboard mit bestehenden Konten.
3. Reviewer eröffnet neues Konto.
4. Reviewer macht Einzahlung und Auszahlung.
5. Reviewer macht Überweisung.
6. Reviewer öffnet Transaktionshistorie.
7. Reviewer sieht saubere Fehler bei zu hoher Auszahlung.
8. Reviewer loggt sich als Admin ein und sieht das Audit-Log.
9. Entwickler erklärt Architektur: Domain, Service, Ports, Adapter, API, Security, Audit, Angular Feature.

## Fachliche Regeln

- Geldbeträge müssen positiv sein, außer Startsaldo darf 0 sein.
- Saldo darf durch Auszahlung oder Überweisung nicht negativ werden.
- Ein Konto gehört genau einem Kunden.
- Überweisung benötigt unterschiedliche Konten.
- Jede erfolgreiche Geldbewegung erzeugt eine Transaktion.
- Jede produktive Geldbewegung erzeugt ein Audit Event.
- API gibt keine Java-Exceptions an das Frontend weiter.

## Datenmodell Zielbild

```text
Customer
  id
  first_name
  last_name
  created_at

Account
  id
  customer_id
  iban
  balance
  status
  version
  created_at

Transaction
  id
  account_id
  type
  amount
  timestamp
  description
  correlation_id

AuditEvent
  id
  actor_id
  action
  resource_type
  resource_id
  timestamp
  metadata_json
```
