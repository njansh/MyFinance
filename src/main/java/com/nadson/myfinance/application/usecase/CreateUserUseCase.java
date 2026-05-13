package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateCategoryPort;
import com.nadson.myfinance.application.port.in.CreateUserPort;
import com.nadson.myfinance.application.port.out.PasswordEncoderPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.enums.TransactionType;

import java.util.UUID;

public class CreateUserUseCase implements CreateUserPort {
    private final UserRepositoryPort userRepositoryPort;
    private final CreateCategoryPort createCategoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public CreateUserUseCase(UserRepositoryPort userRepositoryPort, CreateCategoryPort createCategoryPort, PasswordEncoderPort passwordEncoderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.createCategoryPort = createCategoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public User execute(String name, String email,String password) {
        User user = new User(null, name, email," ");
        user.setPassword(passwordEncoderPort.encode(password));
        User savedUser = userRepositoryPort.save(user);

        createDefaultCategories(savedUser.getId());

        return savedUser;
    }

    private void createDefaultCategories(UUID userId) {
        createCategoryPort.execute(userId, "Salário", "#27AE60", TransactionType.INCOME);
        createCategoryPort.execute(userId, "Renda Extra", "#2980B9", TransactionType.INCOME);
        createCategoryPort.execute(userId, "Investimentos", "#8E44AD", TransactionType.INCOME);

        createCategoryPort.execute(userId, "Alimentação", "#E74C3C", TransactionType.EXPENSE);
        createCategoryPort.execute(userId, "Moradia", "#D35400", TransactionType.EXPENSE);
        createCategoryPort.execute(userId, "Transporte", "#F39C12", TransactionType.EXPENSE);
        createCategoryPort.execute(userId, "Saúde", "#C0392B", TransactionType.EXPENSE);
        createCategoryPort.execute(userId, "Lazer", "#3498DB", TransactionType.EXPENSE);
        createCategoryPort.execute(userId, "Educação", "#F1C40F", TransactionType.EXPENSE);
        createCategoryPort.execute(userId, "Outras Despesas", "#7F8C8D", TransactionType.EXPENSE);
    }
}