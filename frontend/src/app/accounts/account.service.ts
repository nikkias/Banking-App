import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Account, AccountTransaction, AuditEvent, Customer, TransactionPage } from './account.models';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  getAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(`${this.apiUrl}/accounts`);
  }

  getAccount(accountId: string): Observable<Account> {
    return this.http.get<Account>(`${this.apiUrl}/accounts/${accountId}`);
  }

  getTransactions(accountId: string): Observable<AccountTransaction[]> {
    return this.http.get<AccountTransaction[]>(`${this.apiUrl}/accounts/${accountId}/transactions`);
  }

  getTransactionPage(
    accountId: string,
    page: number,
    size: number,
    filters: { type: string; query: string; from: string; to: string }
  ): Observable<TransactionPage> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (filters.type !== 'ALL') {
      params = params.set('type', filters.type);
    }
    if (filters.query.trim()) {
      params = params.set('query', filters.query.trim());
    }
    if (filters.from) {
      params = params.set('from', `${filters.from}T00:00:00Z`);
    }
    if (filters.to) {
      params = params.set('to', `${filters.to}T23:59:59.999Z`);
    }
    return this.http.get<TransactionPage>(`${this.apiUrl}/accounts/${accountId}/transactions/page`, {
      params
    });
  }

  recordTransactionExport(accountId: string, format: 'CSV' | 'PDF', filterSummary: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/accounts/${accountId}/transaction-exports`, { format, filterSummary });
  }

  getAuditEvents(): Observable<AuditEvent[]> {
    return this.http.get<AuditEvent[]>(`${this.apiUrl}/audit-events`);
  }

  registerCustomer(firstName: string, lastName: string, ownerUsername: string): Observable<Customer> {
    return this.http.post<Customer>(`${this.apiUrl}/customers`, { firstName, lastName, ownerUsername });
  }

  openAccount(customerId: string, openingBalance: number): Observable<Account> {
    return this.http.post<Account>(`${this.apiUrl}/accounts`, { customerId, openingBalance });
  }

  deposit(accountId: string, amount: number, description: string): Observable<Account> {
    return this.http.post<Account>(`${this.apiUrl}/accounts/${accountId}/deposit`, { amount, description });
  }

  withdraw(accountId: string, amount: number, description: string): Observable<Account> {
    return this.http.post<Account>(`${this.apiUrl}/accounts/${accountId}/withdraw`, { amount, description });
  }

  transfer(sourceAccountId: string, targetAccountId: string, amount: number, description: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/transfers`, { sourceAccountId, targetAccountId, amount, description });
  }
}
