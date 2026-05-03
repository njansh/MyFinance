CREATE TABLE budgets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    category_id UUID NOT NULL,
    budget_month INT NOT NULL,
    budget_year INT NOT NULL,
    limit_amount NUMERIC(19, 2) NOT NULL,
    spent_amount NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_budget_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_budget_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE goals (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    description VARCHAR(255) NOT NULL,
    target_amount NUMERIC(19, 2) NOT NULL,
    current_amount NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_goal_user FOREIGN KEY (user_id) REFERENCES users(id)
);