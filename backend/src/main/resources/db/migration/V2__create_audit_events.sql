create table audit_events (
    id uuid primary key,
    actor varchar(120) not null,
    action varchar(80) not null,
    resource_type varchar(80) not null,
    resource_id uuid not null,
    amount numeric(19, 2),
    timestamp timestamp with time zone not null,
    outcome varchar(40) not null,
    description varchar(500) not null
);

create index idx_audit_events_timestamp on audit_events(timestamp desc);
create index idx_audit_events_resource on audit_events(resource_type, resource_id);