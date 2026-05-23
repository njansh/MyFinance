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
    public User execute(String name, String email, String password) {
        String hashedPassword = passwordEncoderPort.encode(password);

        User user = new User(null, name, email, hashedPassword);

        User savedUser = userRepositoryPort.save(user);
        createDefaultCategories(savedUser.getId());
        return savedUser;
    }

    private void createDefaultCategories(UUID userId) {
        createCategoryPort.execute(userId, "Salário", "#4CAF50", TransactionType.INCOME);
        createCategoryPort.execute(userId, "Renda Extra", "#8BC34A", TransactionType.INCOME);
        createCategoryPort.execute(userId, "Investimentos", "#388E3C", TransactionType.INCOME);

        createCategoryPort.execute(userId, "Alimentação", "#FF9800", TransactionType.EXPENSE);
        createCategoryPort.execute(userId, "Moradia", "#795548", TransactionType.EXPENSE);
        createCategoryPort.execute(userId, "Transporte", "#607D8B", TransactionType.EXPENSE);
        createCategoryPort.execute(userId, "Saúde", "#F44336", TransactionType.EXPENSE);
        createCategoryPort.execute(userId, "Lazer", "#E91E63", TransactionType.EXPENSE);
        createCategoryPort.execute(userId, "Educação", "#3F51B5", TransactionType.EXPENSE);
        createCategoryPort.execute(userId, "Outros", "#9E9E9E", TransactionType.EXPENSE);
        createCategoryPort.execute(userId, "Fatura Paga", "#9C27B0", TransactionType.EXPENSE);
    }
}
