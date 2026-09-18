import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { StoredCredentials, UserSession } from './auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly storageKey = 'bank-platform.credentials';
  private readonly sessionState = new BehaviorSubject<UserSession | null>(null);

  readonly session$ = this.sessionState.asObservable();

  constructor() {
    const credentials = this.credentials;
    if (credentials) {
      this.sessionState.next({ username: credentials.username, roles: [] });
      this.refreshSession().subscribe({ error: () => this.logout() });
    }
  }

  get credentials(): StoredCredentials | null {
    const raw = localStorage.getItem(this.storageKey);
    return raw ? JSON.parse(raw) as StoredCredentials : null;
  }

  login(username: string, password: string): Observable<UserSession> {
    const authorization = `Basic ${btoa(`${username}:${password}`)}`;
    const headers = new HttpHeaders({ Authorization: authorization });
    return this.http.get<UserSession>(`${environment.apiUrl}/auth/me`, { headers }).pipe(
      tap(session => {
        localStorage.setItem(this.storageKey, JSON.stringify({ username, authorization }));
        this.sessionState.next(session);
      })
    );
  }

  refreshSession(): Observable<UserSession> {
    return this.http.get<UserSession>(`${environment.apiUrl}/auth/me`).pipe(
      tap(session => this.sessionState.next(session))
    );
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
    this.sessionState.next(null);
  }
}
