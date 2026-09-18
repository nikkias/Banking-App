# Technical Decisions

## TD-001 Modular Monolith First

Decision: Das MVP bleibt ein modularer Monolith.

Reason:

- schneller zu entwickeln
- einfacher lokal zu starten
- weniger Infrastrukturkosten
- gute Architekturgrenzen trotzdem möglich

Consequence:

- Pakete müssen streng getrennt bleiben.
- Spätere Extraktion einzelner Module bleibt möglich.

## TD-002 Ports and Adapters

Decision: Services hängen an Repository-Ports, nicht an JPA oder In-Memory.

Reason:

- Tests bleiben schnell.
- PostgreSQL kann ohne Service-Umbau ergänzt werden.
- Fachlogik bleibt unabhängig von Infrastruktur.

Consequence:

- Infrastructure darf Ports implementieren.
- Domain darf Infrastructure nicht importieren.

## TD-003 Spring Boot Backend, Angular Frontend

Decision: Backend und Frontend bleiben getrennte Anwendungen.

Reason:

- klare Verantwortlichkeiten
- einfacher Investor-Demo-Flow
- moderne Full-Stack-Struktur
- später unabhängig deploybar

Consequence:

- API-Verträge müssen stabil bleiben.
- CORS und Environment-Konfiguration sind Pflicht.

## TD-004 Java 21

Decision: Java 21 statt Java 25.

Reason:

- lokal installiert
- LTS
- stabil mit Spring Boot
- buildfähig ohne Release-Fehler

Consequence:

- `pom.xml` nutzt `<java.version>21</java.version>`.

## TD-005 ProblemDetail for API Errors

Decision: API-Fehler werden mit Spring `ProblemDetail` geliefert.

Reason:

- standardnah
- frontendfreundlich
- klar für Investoren und Entwickler erklärbar

Consequence:

- Controller werfen keine rohen technischen Fehler zum UI.
- Frontend kann Fehler zentral auswerten.

## TD-006 PostgreSQL Next

Decision: PostgreSQL ist der nächste große technische Schritt.

Reason:

- Daten müssen Neustarts überleben.
- Banking-Demo braucht echte Persistenz.
- Flyway und JPA zeigen professionelle Backend-Reife.

Consequence:

- In-Memory bleibt für Tests.
- JPA wird als Adapter hinter bestehenden Ports gebaut.

## TD-007 Security Before Public Demo

Decision: Vor einer echten Präsentation außerhalb lokaler Entwicklung muss Security eingebaut werden.

Reason:

- Bankdaten sind sensibel.
- Rollen und Eigentümerprüfung sind Kernanforderungen.
- Audit ist für Geldbewegungen Pflicht.

Consequence:

- Demo-Login kann lokal einfach sein.
- Produktive Richtung soll OIDC/JWT sein.

## TD-008 OIDC/JWT Production Profile

Decision: Lokale Entwicklung nutzt Basic Auth, produktionsnahe Umgebungen nutzen das Spring Profil `oidc` als OAuth2 Resource Server.

Reason:

- Basic Auth ist für Demos einfach, aber nicht die Zielarchitektur.
- OIDC/JWT passt zu Keycloak, Azure Entra ID und Auth0.
- Rollen können standardisiert aus Token-Claims gelesen werden.

Consequence:

- `oidc` erwartet `spring.security.oauth2.resourceserver.jwt.issuer-uri`.
- Tokens müssen Rollen im Claim `roles` enthalten.
- Das Frontend braucht für echte Produktion einen OIDC-Client-Flow statt Basic Auth.
