    package com.nadson.myfinance.infrastructure.adapter.web.dto.response;

    import com.nadson.myfinance.domain.entity.Category;
    import com.nadson.myfinance.domain.enums.TransactionType;

    import java.util.UUID;

    public record CategoryResponse(UUID id, String name, String colorHex, TransactionType type) {
        public static CategoryResponse fromDomain(Category c) {
            return new CategoryResponse(c.getCategoryId(), c.getName(), c.getColorHex(), c.getType());
        }

    }
