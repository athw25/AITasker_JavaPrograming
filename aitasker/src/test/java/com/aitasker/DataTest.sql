SELECT *
FROM users;

SELECT password
FROM users
WHERE email='test@gmail.com';

ALTER TABLE users ADD status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';