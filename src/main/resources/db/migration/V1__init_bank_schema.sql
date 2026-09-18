create table customers (
    id uuid primary key,
    first_name varchar(120) not null,
    last_name varchar(120) not null,
    created_at timestamp with time zone not null default now()
);

create table accounts (
    id uuid primary key,
    iban varchar(34) not null unique,
    customer_id uuid not null references customers(id),
    balance numeric(19, 2) not null,
    version bigint not null default 0,
    status varchar(30) not null default 'ACTIVE',
    created_at timestamp with time zone not null default now(),
    constraint accounts_balance_non_negative check (balance >= 0)
);

create table transactions (
    id uuid primary key,
    account_id uuid not null references accounts(id),
    type varchar(40) not null,
    amount numeric(19, 2) not null,
    timestamp timestamp with time zone not null,
    description varchar(280) not null,
    correlation_id uuid,
    constraint transactions_amount_positive check (amount > 0)
);

create index idx_accounts_customer_id on accounts(customer_id);
create index idx_transactions_account_timestamp on transactions(account_id, timestamp desc);
