package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.User;
import java.util.UUID;

public interface UpdateUserUsePort {
    User execute(UUID userId, String name, String email);
}
