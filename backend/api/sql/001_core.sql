create extension if not exists pgcrypto;
create table if not exists app_users(id uuid primary key default gen_random_uuid(),role text not null check(role in ('master','customer')),name text not null,email text not null unique,password_hash text not null,status text not null default 'active' check(status in ('active','blocked','expired')),expires_at timestamptz,device_limit int not null default 2,created_at timestamptz not null default now());
create table if not exists devices(id uuid primary key default gen_random_uuid(),user_id uuid not null references app_users(id) on delete cascade,device_key text not null,name text not null,platform text not null,active boolean not null default true,last_seen_at timestamptz not null default now(),unique(user_id,device_key));
create table if not exists catalog_sources(id uuid primary key default gen_random_uuid(),name text not null,source_url text not null,active boolean not null default true,updated_at timestamptz not null default now());
create table if not exists catalog_items(id text primary key,name text not null,group_name text not null,logo_url text,stream_url text not null,active boolean not null default true,updated_at timestamptz not null default now());
create table if not exists refresh_sessions(id uuid primary key default gen_random_uuid(),user_id uuid not null references app_users(id) on delete cascade,device_id uuid references devices(id) on delete cascade,token_hash text not null unique,expires_at timestamptz not null,revoked_at timestamptz,created_at timestamptz not null default now());
create index if not exists idx_catalog_group on catalog_items(group_name);
create index if not exists idx_devices_user on devices(user_id);
create index if not exists idx_sessions_user on refresh_sessions(user_id);
