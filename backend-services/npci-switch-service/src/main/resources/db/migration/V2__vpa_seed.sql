INSERT INTO vpa_directory (vpa, bank_code, account_id) VALUES
    ('nivedita@banka', 'A', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2'),
    ('rohan@banka', 'A', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3'),
    ('rohan@bankb', 'B', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2')
ON CONFLICT (vpa) DO NOTHING;
