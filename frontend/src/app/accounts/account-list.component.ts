import { AsyncPipe, CurrencyPipe, NgFor, NgIf } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { BehaviorSubject, catchError, finalize, of, switchMap, tap } from 'rxjs';
import { AccountService } from './account.service';
import { AuthService } from '../core/auth/auth.service';

@Component({
  selector: 'app-account-list',
  standalone: true,
  imports: [AsyncPipe, CurrencyPipe, NgFor, NgIf, ReactiveFormsModule, RouterLink],
  templateUrl: './account-list.component.html',
  styleUrl: './account-list.component.css'
})
export class AccountListComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly accountService = inject(AccountService);
  private readonly refreshAccounts = new BehaviorSubject<void>(undefined);
  readonly auth = inject(AuthService);

  readonly accountForm = this.formBuilder.nonNullable.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    ownerUsername: ['customer', [Validators.required]],
    openingBalance: [0, [Validators.required, Validators.min(0)]]
  });

  readonly transactionForm = this.formBuilder.nonNullable.group({
    accountId: ['', [Validators.required]],
    type: ['deposit', [Validators.required]],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    description: ['', [Validators.required]]
  });

  readonly accounts$ = this.refreshAccounts.pipe(
    switchMap(() => this.accountService.getAccounts().pipe(catchError(() => of([]))))
  );

  readonly auditEvents$ = this.refreshAccounts.pipe(
    switchMap(() => this.accountService.getAuditEvents().pipe(catchError(() => of([]))))
  );

  loading = false;
  transactionLoading = false;
  feedback = '';

  accountsTotal(accounts: { balance: number }[]): number {
    return accounts.reduce((total, account) => total + account.balance, 0);
  }

  submit(): void {
    if (this.accountForm.invalid) {
      this.accountForm.markAllAsTouched();
      return;
    }

    const { firstName, lastName, ownerUsername, openingBalance } = this.accountForm.getRawValue();
    this.loading = true;
    this.feedback = '';
    this.accountService.registerCustomer(firstName, lastName, ownerUsername).pipe(
      switchMap(customer => this.accountService.openAccount(customer.id, openingBalance)),
      tap(() => {
        this.feedback = 'Konto wurde erfolgreich eröffnet.';
        this.accountForm.reset({ firstName: '', lastName: '', ownerUsername: 'customer', openingBalance: 0 });
        this.refreshAccounts.next();
      }),
      finalize(() => this.loading = false),
      catchError(() => {
        this.feedback = 'Die Anfrage konnte nicht verarbeitet werden.';
        return of([]);
      })
    ).subscribe();
  }

  submitTransaction(): void {
    if (this.transactionForm.invalid) {
      this.transactionForm.markAllAsTouched();
      return;
    }

    const { accountId, type, amount, description } = this.transactionForm.getRawValue();
    const request$ = type === 'withdraw'
      ? this.accountService.withdraw(accountId, amount, description)
      : this.accountService.deposit(accountId, amount, description);

    this.transactionLoading = true;
    this.feedback = '';
    request$.pipe(
      tap(() => {
        this.feedback = 'Buchung wurde verarbeitet.';
        this.transactionForm.reset({ accountId, type: 'deposit', amount: 0, description: '' });
        this.refreshAccounts.next();
      }),
      finalize(() => this.transactionLoading = false),
      catchError(() => {
        this.feedback = 'Buchung fehlgeschlagen. Prüfe Betrag und Kontostand.';
        return of(null);
      })
    ).subscribe();
  }
}
