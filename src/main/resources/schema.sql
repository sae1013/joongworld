-- 사용자 테이블 생성
CREATE TABLE IF NOT EXISTS "user" (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    nickname VARCHAR(50) NOT NULL UNIQUE,
    phone_num VARCHAR(30) NOT NULL DEFAULT '',
    position VARCHAR(30) NOT NULL DEFAULT '',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
    
);

CREATE INDEX IF NOT EXISTS idx_user_email ON "user" (email);
CREATE INDEX IF NOT EXISTS idx_user_nickname ON "user" (nickname);

-- 카테고리 테이블 생성
CREATE TABLE IF NOT EXISTS category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    display_order INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    is_active BOOLEAN DEFAULT TRUE
);

-- 상품 테이블 생성
CREATE TABLE IF NOT EXISTS product (
    id               BIGSERIAL PRIMARY KEY,
    category_id      INTEGER NOT NULL REFERENCES category(id),
    user_id          BIGINT NOT NULL REFERENCES "user"(id),
    title            VARCHAR(200) NOT NULL,
    price            BIGINT NOT NULL,
    region           VARCHAR(100),
    safe_pay         BOOLEAN DEFAULT FALSE,
    shipping_available BOOLEAN DEFAULT FALSE,
    meetup_available   BOOLEAN DEFAULT FALSE,
    shipping_cost      BIGINT DEFAULT 0,
    condition_status  TEXT,
    description       TEXT,
    thumbnail_url     TEXT,
    image_urls        TEXT[],
    thumbnail_index   INTEGER DEFAULT 0,
    image_count       INTEGER DEFAULT 0,
    created_at        TIMESTAMPTZ DEFAULT NOW(),
    updated_at        TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_product_created_at ON product (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_product_category ON product (category_id);
CREATE INDEX IF NOT EXISTS idx_product_user ON product (user_id);
