package com.solve.bookstore.infrastructure.repository.dto;

import com.solve.bookstore.infrastructure.entity.CategoryEntity;

public record BookCategoryQueryResult(String bookId, CategoryEntity category) {
}
