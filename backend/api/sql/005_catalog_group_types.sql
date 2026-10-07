create table if not exists catalog_group_types(
 group_name text primary key,
 content_type text not null check(content_type in ('live','movie','series')),
 updated_at timestamptz not null default now()
);
create index if not exists idx_catalog_group_types_type on catalog_group_types(content_type);
update catalog_items i set content_type=g.content_type from catalog_group_types g where g.group_name=i.group_name;
