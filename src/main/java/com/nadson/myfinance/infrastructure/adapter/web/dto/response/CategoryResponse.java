    package com.nadson.myfinance.infrastructure.adapter.web.dto.response;

    import com.nadson.myfinance.domain.entity.Category;

    import java.util.UUID;

    public record CategoryResponse(UUID id, String name, String colorHex) {
        public static CategoryResponse fromDomain(Category c) {
            return new CategoryResponse(c.getCategoryId(), c.getName(), c.getColorHex());
        }

    }