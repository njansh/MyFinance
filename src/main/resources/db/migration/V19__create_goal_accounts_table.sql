CREATE TABLE goal_accounts (
    goal_id UUID NOT NULL,
    account_id UUID NOT NULL,
    CONSTRAINT fk_goal_accounts_goal FOREIGN KEY (goal_id) REFERENCES goals(id),
    PRIMARY KEY (goal_id, account_id) -- Isso evita duplicatas e melhora a performance
);