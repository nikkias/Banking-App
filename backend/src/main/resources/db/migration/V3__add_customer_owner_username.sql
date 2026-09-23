alter table customers add column owner_username varchar(120) not null default 'system';

create index idx_customers_owner_username on customers(owner_username);