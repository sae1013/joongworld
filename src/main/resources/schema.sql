--
CREATE TYPE "PRODUCT_CONDITION" AS ENUM ('NEW', 'USED');

-- 카테고리 더미 테이블 생성
CREATE TABLE IF NOT EXISTS category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    display_order INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    is_active BOOLEAN DEFAULT TRUE
    );

-- 포스트 더미 테이블 생성
CREATE TABLE IF NOT EXISTS post (
    id               BIGSERIAL PRIMARY KEY,
    category_id      INTEGER NOT NULL REFERENCES category(id),
    title            VARCHAR(200) NOT NULL,
    price            BIGINT NOT NULL,
    region           VARCHAR(100),
    safe_pay         BOOLEAN DEFAULT FALSE,
    shipping_available BOOLEAN DEFAULT FALSE,
    meetup_available   BOOLEAN DEFAULT FALSE,
    shipping_cost    VARCHAR(200),
    "condition"      PRODUCT_CONDITION NOT NULL DEFAULT 'USED',  -- ENUM 적용
    description      TEXT,
    thumbnail_url    TEXT,
    image_urls       TEXT[],
    created_at       TIMESTAMPTZ DEFAULT NOW(),
    updated_at       TIMESTAMPTZ DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_post_created_at ON post (created_at DESC);

-- 포스트 테이블 & 카테고리 테이블 JOIN
CREATE INDEX IF NOT EXISTS idx_post_category ON post (category_id);

CREATE TABLE IF NOT EXISTS "user" (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    nickname VARCHAR(50) NOT NULL UNIQUE,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_user_email ON "user" (email);
CREATE INDEX IF NOT EXISTS idx_user_nickname ON "user" (nickname);
