package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateUserPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.User;

public class CreateUserUseCase implements CreateUserPort {
   private final UserRepositoryPort userRepositoryPort;

    public CreateUserUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User execute(String name, String email) {
        User user = new User(null,name, email);
        return userRepositoryPort.save(user);
    }



}
