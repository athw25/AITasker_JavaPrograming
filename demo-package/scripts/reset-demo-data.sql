-- reset-demo-data.sql
-- CANH BAO: Script nay XOA TOAN BO du lieu trong database AITasker.
-- CHI dung cho moi truong demo/dev. KHONG chay tren production.
-- Sau khi chay xong, khoi dong lai backend de Seeder tu chay lai tu dau.

USE AITasker;
GO

DELETE FROM audit_logs;
DELETE FROM analytics_events;
DELETE FROM refresh_tokens;
DELETE FROM attachments;
DELETE FROM messages;
DELETE FROM notifications;
DELETE FROM reviews;
DELETE FROM disputes;
DELETE FROM transactions;
DELETE FROM withdrawals;
DELETE FROM payments;
DELETE FROM deliveries;
DELETE FROM milestones;
DELETE FROM projects;
DELETE FROM proposals;
DELETE FROM job_posts;
DELETE FROM service_packages;
DELETE FROM portfolios;
DELETE FROM expert_profiles;
DELETE FROM users;
GO

PRINT 'Da xoa toan bo du lieu demo. Khoi dong lai backend de seeder chay lai.';
