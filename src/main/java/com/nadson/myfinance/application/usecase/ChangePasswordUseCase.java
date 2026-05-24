package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ChangePasswordPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;

public class ChangePasswordUseCase implements ChangePasswordPort {
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordUseCase(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void execute(String oldPassword, String newPassword) {
        String userIdString = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepositoryPort.findById(UUID.fromString(userIdString));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessRuleException("Current password does not match");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.changePassword(newPassword, encodedNewPassword);

        userRepositoryPort.save(user);
    }
}