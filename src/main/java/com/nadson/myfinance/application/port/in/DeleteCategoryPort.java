package com.nadson.myfinance.application.port.in;

import java.util.UUID;

public interface DeleteCategoryPort {
    void execute(UUID userId, UUID categoryId);
}
