package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.UpdateUserUsePort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.User;
import java.util.UUID;


public class UpdateUserUseCase implements UpdateUserUsePort {
    private final UserRepositoryPort userRepositoryPort;

    public UpdateUserUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public User execute(UUID userId, String name, String email) {
        User user = userRepositoryPort.findById(userId);
        if (user == null) throw new RuntimeException("User not found");

        user.updateProfile(name, email);
        return userRepositoryPort.save(user);
    }
}