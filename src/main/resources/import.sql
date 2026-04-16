INSERT INTO users (id, name, email) VALUES ('feb6ff14-69c9-4e37-9d00-5ddc3de685a8', 'Nadson Jhony', 'nadson@finance.com');
INSERT INTO accounts (id, user_id, name, type, balance) VALUES ('5482bbe9-90bb-4a55-99fb-1a1d2bb6c612', 'feb6ff14-69c9-4e37-9d00-5ddc3de685a8', 'Nubank Corrente', 'CHECKING', 0);
INSERT INTO accounts (id, user_id, name, type, balance) VALUES ('69337d84-94ec-49fc-bef3-ee9983bcc09b', 'feb6ff14-69c9-4e37-9d00-5ddc3de685a8', 'Inter Lazer', 'CHECKING', 0);
INSERT INTO accounts (id, user_id, name, type, balance) VALUES ('0fde6a90-9403-4a42-8133-91f76e793211', 'feb6ff14-69c9-4e37-9d00-5ddc3de685a8', 'XP Investimentos', 'INVESTMENT', 0);
INSERT INTO accounts (id, user_id, name, type, balance) VALUES ('f9b7ffd2-43e6-4b5d-b578-d7aa8c2d91c2', 'feb6ff14-69c9-4e37-9d00-5ddc3de685a8', 'Caixa Reserva', 'CHECKING', 0);
INSERT INTO categories (id, name, color, type) VALUES ('bd5ae897-840b-4696-b5f6-4e643c31158b', 'Salário', '#3357FF', 'INCOME');
INSERT INTO categories (id, name, color, type) VALUES ('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'Freelance', '#2ECC71', 'INCOME');
INSERT INTO categories (id, name, color, type) VALUES ('299fe715-ddc6-4ef5-9323-3c0a4c873428', 'Alimentação', '#FF5733', 'EXPENSE');
INSERT INTO categories (id, name, color, type) VALUES ('f4e3d2c1-b0a9-49e8-8d7c-6f5e4d3c2b1a', 'Lazer e Viagens', '#9B59B6', 'EXPENSE');