package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.CreateCategoryPort;
import com.nadson.myfinance.application.port.in.GetCategoriesPort;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.CategoryRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.CategoryResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CreateCategoryPort createCategoryPort;
    private final GetCategoriesPort getCategoriesPort;

    public CategoryController(CreateCategoryPort createCategoryPort, GetCategoriesPort getCategoriesPort) {
        this.createCategoryPort = createCategoryPort;
        this.getCategoriesPort = getCategoriesPort;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        String authenticatedUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Category category = createCategoryPort.execute(UUID.fromString(authenticatedUserId), request.name(), request.colorHex(), request.type());
        return ResponseEntity.status(201).body(CategoryResponse.fromDomain(category));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        String authenticatedUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Category> categories = getCategoriesPort.execute(UUID.fromString(authenticatedUserId));

        List<CategoryResponse> response = categories.stream()
                .map(CategoryResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(response);
    }
}