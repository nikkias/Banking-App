import { AsyncPipe, CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { BehaviorSubject, catchError, combineLatest, finalize, map, of, shareReplay, switchMap, tap } from 'rxjs';
import { AccountService } from './account.service';

@Component({
    selector: 'app-account-detail',
  standalone: true,
    imports: [AsyncPipe, CurrencyPipe, DatePipe, ReactiveFormsModule, RouterLink],
    templateUrl: './account-detail.component.html',
    styleUrl: './account-detail.component.css'
})
export class AccountDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly formBuilder = inject(FormBuilder);
  private readonly accountService = inject(AccountService);
  private readonly refreshDetail = new BehaviorSubject<void>(undefined);

  readonly accountId$ = this.route.paramMap.pipe(
    map(params => params.get('accountId') ?? ''),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  readonly accounts$ = this.refreshDetail.pipe(
    switchMap(() => this.accountService.getAccounts().pipe(catchError(() => of([]))))
  );

  readonly account$ = combineLatest([this.accountId$, this.refreshDetail]).pipe(
    switchMap(([accountId]) => this.accountService.getAccount(accountId).pipe(catchError(() => of(null))))
  );

  readonly transactions$ = combineLatest([this.accountId$, this.refreshDetail]).pipe(
    switchMap(([accountId]) => this.accountService.getTransactions(accountId).pipe(catchError(() => of([]))))
  );

  readonly transferForm = this.formBuilder.nonNullable.group({
    targetAccountId: ['', [Validators.required]],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    description: ['', [Validators.required]]
  });

  loading = false;
  feedback = '';

  submitTransfer(sourceAccountId: string): void {
    if (this.transferForm.invalid) {
      this.transferForm.markAllAsTouched();
      return;
    }

    const { targetAccountId, amount, description } = this.transferForm.getRawValue();
    this.loading = true;
    this.feedback = '';
    this.accountService.transfer(sourceAccountId, targetAccountId, amount, description).pipe(
      tap(() => {
        this.feedback = 'Überweisung wurde erfolgreich ausgeführt.';
        this.transferForm.reset({ targetAccountId: '', amount: 0, description: '' });
        this.refreshDetail.next();
      }),
      finalize(() => this.loading = false),
      catchError(() => {
        this.feedback = 'Überweisung fehlgeschlagen. Prüfe Zielkonto, Betrag und Kontostand.';
        return of(null);
      })
    ).subscribe();
  }
}
