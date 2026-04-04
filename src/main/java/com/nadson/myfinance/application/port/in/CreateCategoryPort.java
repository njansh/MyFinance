package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Category;

public interface CreateCategoryPort {
    Category execute(String categoryName,String colorhex);
}
