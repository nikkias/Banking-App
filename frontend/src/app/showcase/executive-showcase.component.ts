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
  readonly capabilities = [
    { label: 'Regional Banking', text: 'Kontoführung, Filialprozesse, Kundenrollen, Audit und Integrationsfähigkeit.' },
    { label: 'Comparison Platform', text: 'Kundenreise, Angebotsstrecken, Conversion-KPIs und Self-Service-Denken.' },
    { label: 'Insurance Operations', text: 'Antrag, Vertrag, Schaden, TAA und Änderungsmanagement als nächste Module.' },
    { label: 'Public Sector Ready', text: 'Nachvollziehbare Rollen, standardisierte Prozesse, Governance und Reporting.' }
  ];

  readonly proofPoints = [
    'Spring Boot 3.5 + Java 21',
    'Angular 18 Standalone UI',
    'Ports-and-Adapters Architektur',
    'PostgreSQL/Flyway ready',
    'Spring Security + Rollen',
    'Customer Ownership',
    'Audit Center',
    'OpenAPI/Swagger',
    'Docker/Render ready'
  ];
}
