DELETE
FROM accounts
WHERE email = 'manager@mail.com';
INSERT INTO accounts(password, email, first_name, last_name, role, status, created_at)
VALUES ('$2a$10$/DiVNFqiuEtfB7VXMFj8Uu7mppTXloNDFdE.ai23EaXpjoYsOM9e6', 'manager@mail.com', 'manager', 'manager',
        'ROLE_MANAGER', 'ACTIVE', now());