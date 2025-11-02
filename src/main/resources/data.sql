-- 서버 재시작 마다 테이블 비우고 더미데이터로 강제셋업
truncate table product restart identity cascade;
truncate table category restart identity cascade;
truncate table "user" restart identity cascade;

-- 카테고리 테이블에 더미삽입
insert into category (name, display_order) values
    ('시계/주얼리', 1),
    ('디지털/가전', 2),
    ('가구/인테리어', 3),
    ('생활/취미', 4);

-- 유저 테이블에 더미삽입
insert into "user" (email, password_hash, name, nickname, is_admin)
values
    ('alice@example.com', '$2a$10$alicehashalicehashaliceu', '앨리스', 'alice', false),
    ('bob@example.com', '$2a$10$bobhashbobhashbobhashboo', '밥', 'bobby', true),
    ('charlie@example.com', '$2a$10$charliehashcharlieha', '찰리', 'charlie', false);

-- 상품 테이블에 더미삽입
insert into product (category_id, user_id, title, price, region, safe_pay, shipping_available, meetup_available, shipping_cost,
                     condition_status, description, thumbnail_url, image_urls, created_at, updated_at)
values
    ((select id from category where name = '시계/주얼리'),
     (select id from "user" where nickname = 'alice'),
     '롤렉스 익스플로러 I 36mm', 10800000, '서울 강남', true, true, false, 0,
     '중고',
     '정기 점검 완료된 제품으로 상태 우수합니다. 보증 카드 포함, 안전결제 가능합니다.',
     'https://picsum.photos/seed/watch01/400/300',
     ARRAY['https://picsum.photos/seed/watch01_1/1200/900','https://picsum.photos/seed/watch01_2/1200/900'],
     now() - interval '2 hours',
     now() - interval '1 hours'),

    ((select id from category where name = '디지털/가전'),
     (select id from "user" where nickname = 'bobby'),
     '애플 아이패드 프로 11 3세대', 920000, '서울 마포', true, true, true, 3000,
     '새상품',
     '박스 및 충전기 모두 포함된 상태 좋은 아이패드입니다. 펜슬 2세대까지 함께 드립니다.',
     'https://picsum.photos/seed/ipad/400/300',
     ARRAY['https://picsum.photos/seed/ipad1/1200/900','https://picsum.photos/seed/ipad2/1200/900'],
     now() - interval '5 hours',
     now() - interval '3 hours'),

    ((select id from category where name = '가구/인테리어'),
     (select id from "user" where nickname = 'charlie'),
     '스탠딩 책상 (1200폭)', 140000, '인천 송도', false, false, true, 0,
     '중고',
     '높이 조절 가능한 전동 스탠딩 책상입니다. 분해 후 차량에 실어 드릴 수 있습니다.',
     'https://picsum.photos/seed/desk/400/300',
     ARRAY['https://picsum.photos/seed/desk1/1200/900','https://picsum.photos/seed/desk2/1200/900'],
     now() - interval '12 hours',
     now() - interval '9 hours'),

    ((select id from category where name = '생활/취미'),
     (select id from "user" where nickname = 'alice'),
     '캠핑 폴딩체어 세트', 68000, '부산 해운대', false, true, true, 4000,
     '중고',
     '1인용 폴딩체어 2개 세트입니다. 사용감 있지만 천 상태 양호하며 전용 수납가방 포함입니다.',
     'https://picsum.photos/seed/chair/400/300',
     ARRAY['https://picsum.photos/seed/chair1/1200/900','https://picsum.photos/seed/chair2/1200/900'],
     now() - interval '1 days',
     now() - interval '20 hours');
