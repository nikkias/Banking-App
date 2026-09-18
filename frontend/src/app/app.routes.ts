import { Routes } from '@angular/router';
import { AuditEventsComponent } from './admin/audit-events.component';
import { AccountDetailComponent } from './accounts/account-detail.component';
import { AccountListComponent } from './accounts/account-list.component';
import { ExecutiveShowcaseComponent } from './showcase/executive-showcase.component';

export const routes: Routes = [
  { path: '', component: AccountListComponent },
  { path: 'accounts/:accountId', component: AccountDetailComponent },
  { path: 'admin/audit', component: AuditEventsComponent },
  { path: 'showcase', component: ExecutiveShowcaseComponent },
  { path: '**', redirectTo: '' }
];
