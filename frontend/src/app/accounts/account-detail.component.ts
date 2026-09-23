import { AsyncPipe, CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { BehaviorSubject, catchError, combineLatest, finalize, map, of, shareReplay, startWith, switchMap, tap } from 'rxjs';
import { AccountService } from './account.service';
import { Account, AccountTransaction, TransactionPage } from './account.models';

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
  private readonly transactionPageIndex = new BehaviorSubject(0);
  readonly transactionPageSize = 25;

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

  readonly transferForm = this.formBuilder.nonNullable.group({
    targetAccountId: ['', [Validators.required]],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    description: ['', [Validators.required]]
  });

  readonly transactionFilterForm = this.formBuilder.nonNullable.group({
    query: [''],
    type: ['ALL'],
    from: [''],
    to: ['']
  });

  readonly transactionFilters$ = this.transactionFilterForm.valueChanges.pipe(
    startWith(this.transactionFilterForm.getRawValue()),
    map(filters => ({
      query: filters.query ?? '',
      type: filters.type ?? 'ALL',
      from: filters.from ?? '',
      to: filters.to ?? ''
    })),
    tap(() => this.transactionPageIndex.next(0)),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  readonly transactionPage$ = combineLatest([this.accountId$, this.refreshDetail, this.transactionPageIndex, this.transactionFilters$]).pipe(
    switchMap(([accountId, , page, filters]) => this.accountService.getTransactionPage(accountId, page, this.transactionPageSize, filters).pipe(
      catchError(() => of(this.emptyTransactionPage(page))
    )),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  readonly transactions$ = this.transactionPage$.pipe(map(transactionPage => transactionPage.content));

  loading = false;
  exportLoading = false;
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

  exportCsv(account: Account, transactions: AccountTransaction[]): void {
    this.recordExport(account, 'CSV', () => this.downloadCsv(account, transactions));
  }

  printStatement(account: Account): void {
    this.recordExport(account, 'PDF', () => window.print());
  }

  previousTransactionPage(page: number): void {
    if (page > 0) {
      this.transactionPageIndex.next(page - 1);
    }
  }

  nextTransactionPage(page: number, totalElements: number): void {
    if ((page + 1) * this.transactionPageSize < totalElements) {
      this.transactionPageIndex.next(page + 1);
    }
  }

  private downloadCsv(account: Account, transactions: AccountTransaction[]): void {
    const rows = [
      ['IBAN', 'Timestamp', 'Type', 'Amount', 'Description'],
      ...transactions.map(transaction => [
        account.iban,
        transaction.timestamp,
        transaction.type,
        transaction.amount.toFixed(2),
        transaction.description
      ])
    ];
    const csv = rows.map(row => row.map(value => this.escapeCsv(value)).join(',')).join('\r\n');
    const blob = new Blob([`\uFEFF${csv}`], { type: 'text/csv;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `transactions-${account.iban.replaceAll(' ', '')}.csv`;
    link.click();
    URL.revokeObjectURL(url);
  }

  private recordExport(account: Account, format: 'CSV' | 'PDF', completeExport: () => void): void {
    this.exportLoading = true;
    this.feedback = '';
    this.accountService.recordTransactionExport(account.id, format, this.exportFilterSummary()).pipe(
      tap(completeExport),
      finalize(() => this.exportLoading = false),
      catchError(() => {
        this.feedback = 'Export konnte nicht für das Audit protokolliert werden.';
        return of(null);
      })
    ).subscribe();
  }

  private exportFilterSummary(): string {
    const { query, type, from, to } = this.transactionFilterForm.getRawValue();
    return `query=${query || 'none'}; type=${type}; from=${from || 'none'}; to=${to || 'none'}`;
  }

  private escapeCsv(value: string | number): string {
    return `"${String(value).replaceAll('"', '""')}"`;
  }

  private emptyTransactionPage(page: number): TransactionPage {
    return { content: [], page, size: this.transactionPageSize, totalElements: 0 };
  }
}
