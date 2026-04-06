package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.User;

import java.util.UUID;

public interface GetUserPort {
    User execute(UUID userId);
}


