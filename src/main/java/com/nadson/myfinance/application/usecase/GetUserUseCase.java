package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetUserPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.exception.UserNotFoundException;

import java.util.UUID;

public class GetUserUseCase implements GetUserPort {
    private final UserRepositoryPort userRepositoryPort;

    public GetUserUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User execute(UUID userId) {
        User user = userRepositoryPort.findById(userId);

        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        return user;
    }
}
