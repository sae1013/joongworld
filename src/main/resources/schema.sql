-- 카테고리 더미 테이블 생성
create table if not exists category (
    id serial primary key,
    name varchar(100) not null unique,
    display_order integer default 0,
    created_at timestamp with time zone default now(),
    is_active boolean default true
);

-- 포스트 더미 테이블 생성
create table if not exists post (
    id bigserial primary key,
    category_id integer not null references category(id),
    title varchar(200) not null,
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

-- 포스트 테이블 & 카테고리 테이블 JOIN
create index if not exists idx_post_category on post (category_id);

create table if not exists "user" (
    id bigserial primary key,
    email varchar(255) not null unique,
    password_hash varchar(100) not null,
    name varchar(50) not null,
    nickname varchar(50) not null unique,
    created_at timestamp with time zone default now(),
    updated_at timestamp with time zone default now()
);

create index if not exists idx_user_email on "user" (email);
create index if not exists idx_user_nickname on "user" (nickname);
