package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.enums.TransactionType;

import java.util.UUID;

public interface CreateCategoryPort {
    Category execute(UUID userId, String categoryName, String colorhex, TransactionType type);
}
