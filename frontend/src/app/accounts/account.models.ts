export interface Account {
  id: string;
  iban: string;
  customerId: string;
  balance: number;
}

export interface Customer {
  id: string;
  fullName: string;
  ownerUsername: string;
}

export interface AccountTransaction {
  id: string;
  accountId: string;
  type: 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER_IN' | 'TRANSFER_OUT';
  amount: number;
  timestamp: string;
  description: string;
}

export interface TransactionPage {
  content: AccountTransaction[];
  page: number;
  size: number;
  totalElements: number;
}

export interface AuditEvent {
  id: string;
  actor: string;
  action: string;
  resourceType: string;
  resourceId: string;
  amount: number | null;
  timestamp: string;
  outcome: string;
  description: string;
}
