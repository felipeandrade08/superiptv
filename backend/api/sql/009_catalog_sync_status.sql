create table if not exists catalog_sync_runs(
 id uuid primary key default gen_random_uuid(),
 source_name text not null,
 status text not null check(status in ('running','success','failed')),
 error_code text,
 items int not null default 0,
 bytes bigint not null default 0,
 duration_ms int,
 started_at timestamptz not null default now(),
 finished_at timestamptz
);
create index if not exists idx_catalog_sync_runs_started on catalog_sync_runs(started_at desc);
