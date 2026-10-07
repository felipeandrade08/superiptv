alter table catalog_sources add column if not exists source_url_encrypted text;
alter table catalog_items add column if not exists stream_url_encrypted text;
alter table catalog_sources alter column source_url drop not null;
alter table catalog_items alter column stream_url drop not null;
alter table catalog_sources add constraint catalog_sources_url_present check(source_url_encrypted is not null or source_url is not null) not valid;
alter table catalog_items add constraint catalog_items_url_present check(stream_url_encrypted is not null or stream_url is not null) not valid;
