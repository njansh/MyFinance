package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringUserRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserPersistence implements UserRepositoryPort {
    private final SpringUserRepository repository;

    public UserPersistence(SpringUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity=new UserJpaEntity(user);
        return repository.save(entity).toDomain();
    }

    @Override
    public User findById(UUID userId) {
  return  repository.findById(userId).map(UserJpaEntity::toDomain).orElse(null);
    }
}
