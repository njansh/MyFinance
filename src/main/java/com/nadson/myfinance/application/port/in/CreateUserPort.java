package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.User;

public interface CreateUserPort {
    User execute(String name, String email);


}
