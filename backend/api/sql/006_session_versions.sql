alter table app_users add column if not exists session_version int not null default 1;
create index if not exists idx_refresh_sessions_active on refresh_sessions(user_id,device_id) where revoked_at is null;
