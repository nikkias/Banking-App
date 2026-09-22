import { AsyncPipe, DatePipe, CurrencyPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, of } from 'rxjs';
import { AccountService } from '../accounts/account.service';

@Component({
    selector: 'app-audit-events',
  standalone: true,
    imports: [AsyncPipe, CurrencyPipe, DatePipe, RouterLink],
    templateUrl: './audit-events.component.html',
    styleUrl: './audit-events.component.css'
})
export class AuditEventsComponent {
  private readonly accountService = inject(AccountService);

  readonly events$ = this.accountService.getAuditEvents().pipe(
    catchError(() => of([]))
  );

  eventTone(action: string): string {
    if (action.includes('TRANSFER')) {
      return 'transfer';
    }
    if (action.includes('ACCOUNT')) {
      return 'account';
    }
    if (action.includes('CUSTOMER')) {
      return 'customer';
    }
    return 'money';
  }
}
