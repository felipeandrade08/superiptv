alter table catalog_items add column if not exists content_type text not null default 'live' check(content_type in ('live','movie','series'));
alter table catalog_items add column if not exists description text;
alter table catalog_items add column if not exists year int;
alter table catalog_items add column if not exists season_number int;
alter table catalog_items add column if not exists episode_number int;
alter table catalog_items add column if not exists series_name text;
create index if not exists idx_catalog_type_group on catalog_items(content_type,group_name) where active=true;
