package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.enums.TransactionType;

import java.util.UUID;

public interface UpdateCategoryPort {
    Category execute(UUID userId, UUID categoryId, String name, String colorHex, String icon, TransactionType type);
}