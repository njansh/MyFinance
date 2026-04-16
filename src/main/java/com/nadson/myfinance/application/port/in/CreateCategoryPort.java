package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.enums.TransactionType;

public interface CreateCategoryPort {
    Category execute(String categoryName, String colorhex, TransactionType type);
}
