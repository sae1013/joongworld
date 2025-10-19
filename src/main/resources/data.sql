truncate table post restart identity cascade;

insert into post (title, category, price, region, safe_pay, condition_text, description, thumbnail_url, image_urls,
                  created_at)
values ('오메가 아쿠아테라 쿼츠 36mm 은판', '시계/주얼리', 1800000, '서울 강남', true,
        '중고 · 가벼운 흠집',
        '정기적으로 관리한 제품입니다. 보증서와 케이스 포함, 직거래 및 안전결제 모두 가능합니다.',
        'https://picsum.photos/seed/omega/400/300', ARRAY['https://picsum.photos/seed/omega1/1200/900',
        'https://picsum.photos/seed/omega2/1200/900', 'https://picsum.photos/seed/omega3/1200/900'],
        now() - interval '2 days'),

       ('닌텐도 스위치 OLED 네온', '디지털/가전', 360000, '부산 해운대', false,
        '중고 · 생활기스',
        '박스/구성품 완비, 성능 이상 없음. 택배 가능하며 직거래는 해운대역에서 가능합니다.',
        'https://picsum.photos/seed/switch/400/300', ARRAY['https://picsum.photos/seed/switch1/1200/900',
        'https://picsum.photos/seed/switch2/1200/900'],
        now() - interval '1 day'),

       ('아이패드 에어 5세대 64GB 스카이블루', '디지털/가전', 720000, '대구 수성', true,
        '중고 · 사용감 적음',
        '유튜브 시청 용도로만 사용해서 상태가 깨끗합니다. 애플케어 남아있으며, 스마트폴리오 함께 드립니다.',
        'https://picsum.photos/seed/ipad/400/300', ARRAY['https://picsum.photos/seed/ipad1/1200/900',
        'https://picsum.photos/seed/ipad2/1200/900'],
        now() - interval '5 hours'),

       ('스탠딩 책상 (1200폭)', '가구/인테리어', 140000, '인천 송도', false,
        '중고 · 양호',
        '높이 조절 가능한 전동 스탠딩 책상입니다. 분해 후 차량에 실어 드릴 수 있습니다.',
        'https://picsum.photos/seed/desk/400/300', ARRAY['https://picsum.photos/seed/desk1/1200/900',
        'https://picsum.photos/seed/desk2/1200/900'],
        now() - interval '12 hours');
