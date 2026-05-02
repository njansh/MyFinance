package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Category;

import java.util.List;
import java.util.UUID;

public interface GetCategoriesPort {
    List<Category> execute(UUID userId);
}