create table if not exists customer_catalog_groups(
 user_id uuid not null references app_users(id) on delete cascade,
 group_name text not null,
 allowed boolean not null default true,
 updated_at timestamptz not null default now(),
 primary key(user_id,group_name)
);
create index if not exists idx_customer_catalog_groups_user on customer_catalog_groups(user_id);
