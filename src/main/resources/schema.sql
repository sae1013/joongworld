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
    status            VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
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

-- 댓글 테이블
CREATE TABLE IF NOT EXISTS comment (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    parent_id BIGINT REFERENCES comment(id) ON DELETE CASCADE,
    depth INTEGER NOT NULL DEFAULT 0,
    author_id BIGINT NOT NULL REFERENCES "user"(id),
    content TEXT NOT NULL,
    like_count INTEGER NOT NULL DEFAULT 0,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_comment_product ON comment (product_id, created_at);
CREATE INDEX IF NOT EXISTS idx_comment_parent ON comment (parent_id);

-- 댓글 좋아요 테이블
CREATE TABLE IF NOT EXISTS comment_like (
    comment_id BIGINT NOT NULL REFERENCES comment(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (comment_id, user_id)
);

-- 신고 사유 테이블
CREATE TABLE IF NOT EXISTS report_reason (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description TEXT
);

-- 신고 테이블
CREATE TABLE IF NOT EXISTS report (
    id BIGSERIAL PRIMARY KEY,
    reporter_id BIGINT NOT NULL REFERENCES "user"(id),
    reported_user_id BIGINT REFERENCES "user"(id),
    reported_product_id BIGINT REFERENCES product(id),
    target_type VARCHAR(20) NOT NULL,
    reason_code VARCHAR(50) NOT NULL REFERENCES report_reason(code),
    description TEXT NOT NULL,
    handler_id BIGINT REFERENCES "user"(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    resolution_type VARCHAR(30),
    handler_memo TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    processed_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_report_status ON report (status);
CREATE INDEX IF NOT EXISTS idx_report_reported_user ON report (reported_user_id);
CREATE INDEX IF NOT EXISTS idx_report_reported_product ON report (reported_product_id);

-- 알림 테이블
CREATE TABLE IF NOT EXISTS notification (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    recipient_id BIGINT NOT NULL REFERENCES "user"(id),
    actor_id BIGINT REFERENCES "user"(id),
    target_type VARCHAR(50),
    target_id BIGINT,
    message TEXT,
    metadata_json TEXT NOT NULL DEFAULT '{}',
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    read_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_notification_recipient_created ON notification (recipient_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_notification_unread ON notification (recipient_id) WHERE read_at IS NULL;

-- 알림 아웃박스 테이블
CREATE TABLE IF NOT EXISTS notification_outbox (
    id BIGSERIAL PRIMARY KEY,
    notification_id BIGINT NOT NULL REFERENCES notification(id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL,
    payload TEXT NOT NULL DEFAULT '{}',
    published_at TIMESTAMPTZ,
    delivered_at TIMESTAMPTZ,
    error_message TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_notification_outbox_pending ON notification_outbox (created_at) WHERE delivered_at IS NULL;
