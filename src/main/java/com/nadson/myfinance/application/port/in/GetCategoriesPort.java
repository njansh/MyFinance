package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Category;

import java.util.List;

public interface GetCategoriesPort {
    List<Category> execute();
}
