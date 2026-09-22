import { Component } from '@angular/core';

import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-executive-showcase',
  standalone: true,
    imports: [RouterLink],
    templateUrl: './executive-showcase.component.html',
    styleUrl: './executive-showcase.component.css'
})
export class ExecutiveShowcaseComponent {
  readonly readinessPillars = [
    { label: 'Produktkern', metric: 'P0', text: 'Kunden, Konten, Buchungen, Überweisungen und Historie sind als nutzbarer End-to-End-Flow umgesetzt.' },
    { label: 'Trust Layer', metric: 'Audit', text: 'Rollen, Ownership-Prüfung, Admin-Audit und klare Fehlerantworten machen sensible Prozesse nachvollziehbar.' },
    { label: 'Tech Proof', metric: 'API', text: 'Spring Boot, Ports-and-Adapters, OpenAPI, PostgreSQL-Profil, Flyway und Docker-Pfad zeigen Skalierbarkeit.' },
    { label: 'Delivery Story', metric: 'MVP', text: 'Roadmap, Feature-Katalog, Qualitätsstrategie und Demo-Flow machen das Projekt präsentierbar und ausbaufähig.' }
  ];

  readonly capabilities = [
    { label: 'Regional Banking', text: 'Kontoführung, Filialprozesse, Kundenrollen, Audit und Integrationsfähigkeit.' },
    { label: 'Comparison Platform', text: 'Kundenreise, Angebotsstrecken, Conversion-KPIs und Self-Service-Denken.' },
    { label: 'Insurance Operations', text: 'Antrag, Vertrag, Schaden, TAA und Änderungsmanagement als nächste Module.' },
    { label: 'Public Sector Ready', text: 'Nachvollziehbare Rollen, standardisierte Prozesse, Governance und Reporting.' }
  ];

  readonly proofPoints = [
    'Spring Boot 3.5 + Java 21',
    'Angular 21 Standalone UI',
    'Ports-and-Adapters Architektur',
    'PostgreSQL/Flyway ready',
    'Spring Security + Rollen',
    'Customer Ownership',
    'Audit Center',
    'OpenAPI/Swagger',
    'Docker/Render ready'
  ];

  readonly deliveryDisciplines = [
    'Professional Scrum Master I',
    'Professional Scrum Product Owner I',
    'Customer Journey & Pain Point Analysis',
    'Business- und Anforderungsanalyse',
    'SAP Account Management & System Integration',
    'BPMN, Prozessanalyse & Prozessoptimierung',
    'PMO, Projektleitung & Qualitätsgates',
    'KPI-, Reporting- und Stakeholder-Management'
  ];
}
