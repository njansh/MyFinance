CREATE EXTENSION IF NOT EXISTS pgcrypto;
UPDATE users SET password = crypt('123456', gen_salt('bf'));