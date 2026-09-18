import { Routes } from '@angular/router';
import { AccountDetailComponent } from './accounts/account-detail.component';
import { AccountListComponent } from './accounts/account-list.component';

export const routes: Routes = [
  { path: '', component: AccountListComponent },
  { path: 'accounts/:accountId', component: AccountDetailComponent },
  { path: '**', redirectTo: '' }
];
