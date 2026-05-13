package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    User save(User user);

    User findById(UUID userId);

    void deleteById(UUID userId);

    Optional<User>  findByEmail(String email);
}
