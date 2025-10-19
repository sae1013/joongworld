create table if not exists post (
    id bigserial primary key,
    title varchar(200) not null,
    category varchar(100),
    price integer not null,
    region varchar(100),
    safe_pay boolean default false,
    condition_text varchar(200),
    description text,
    thumbnail_url text,
    image_urls text[],
    created_at timestamp with time zone default now(),
    updated_at timestamp with time zone default now()
);

create index if not exists idx_post_created_at on post (created_at desc);
