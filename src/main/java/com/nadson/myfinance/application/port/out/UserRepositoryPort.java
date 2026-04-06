package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.User;

import java.util.UUID;

public interface UserRepositoryPort {
    User save(User user);

    User findById(UUID userId);
}
