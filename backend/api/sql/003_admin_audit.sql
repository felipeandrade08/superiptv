create table if not exists admin_audit_log(
 id uuid primary key default gen_random_uuid(),
 actor_user_id uuid references app_users(id) on delete set null,
 action text not null,
 target_type text not null,
 target_id text,
 metadata jsonb not null default '{}'::jsonb,
 created_at timestamptz not null default now()
);
create index if not exists idx_admin_audit_created on admin_audit_log(created_at desc);
create index if not exists idx_admin_audit_target on admin_audit_log(target_type,target_id);
