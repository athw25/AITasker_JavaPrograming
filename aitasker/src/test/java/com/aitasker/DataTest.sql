SELECT *
FROM users;

SELECT password
FROM users
WHERE email='test@gmail.com';

ALTER TABLE users ADD status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

SELECT *
FROM projects
WHERE proposal_id = 3;

EXEC sp_helpindex 'projects';

DELETE FROM disputes; DELETE FROM dispute_messages; DELETE FROM dispute_evidences;
DELETE FROM messages; DELETE FROM notifications; DELETE FROM reviews;
DELETE FROM transactions; DELETE FROM withdrawals; DELETE FROM payments;
DELETE FROM deliveries; DELETE FROM milestones; DELETE FROM projects;
DELETE FROM proposals; DELETE FROM recommendations; DELETE FROM job_posts;
DELETE FROM portfolios; DELETE FROM service_packages; DELETE FROM expert_profiles;
DELETE FROM attachments; DELETE FROM users WHERE role <> 'ADMIN';