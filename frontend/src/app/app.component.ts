import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink, RouterOutlet } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from './core/auth/auth.service';
import { ExecutiveShowcaseComponent } from './showcase/executive-showcase.component';

@Component({
    selector: 'app-root',
  standalone: true,
    imports: [AsyncPipe, ExecutiveShowcaseComponent, ReactiveFormsModule, RouterLink, RouterOutlet],
    templateUrl: './app.component.html',
    styleUrl: './app.component.css'
})
export class AppComponent {
  readonly auth = inject(AuthService);
  private readonly formBuilder = inject(FormBuilder);
  readonly isPublicShowcase = window.location.pathname.endsWith('/showcase');
  readonly isStaticPages = window.location.hostname.endsWith('github.io')
    || window.location.pathname.startsWith('/Banking-App');

  readonly loginForm = this.formBuilder.nonNullable.group({
    username: ['employee', [Validators.required]],
    password: ['employee123', [Validators.required]]
  });

  loading = false;
  feedback = '';

  login(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const { username, password } = this.loginForm.getRawValue();
    this.loading = true;
    this.feedback = '';
    this.auth.login(username, password).pipe(
      finalize(() => this.loading = false)
    ).subscribe({
      error: () => this.feedback = 'Login fehlgeschlagen. Prüfe Benutzer und Passwort.'
    });
  }

  logout(): void {
    this.auth.logout();
  }
}
