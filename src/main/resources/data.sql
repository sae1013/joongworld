INSERT INTO report_reason (code, display_name, description)
VALUES ('SPAM', '스팸', '도배 또는 광고성 신고')
ON CONFLICT (code) DO NOTHING;

INSERT INTO report_reason (code, display_name, description)
VALUES ('FRAUD', '사기', '사기 및 금전 요구 신고')
ON CONFLICT (code) DO NOTHING;

INSERT INTO report_reason (code, display_name, description)
VALUES ('ABUSE', '욕설/혐오', '욕설, 혐오 발언, 괴롭힘 신고')
ON CONFLICT (code) DO NOTHING;

INSERT INTO report_reason (code, display_name, description)
VALUES ('ADULT', '성인/음란', '성인물 또는 음란물 신고')
ON CONFLICT (code) DO NOTHING;
