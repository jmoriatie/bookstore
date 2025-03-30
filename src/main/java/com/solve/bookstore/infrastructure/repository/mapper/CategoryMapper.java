package com.solve.bookstore.infrastructure.repository.mapper;

import com.solve.bookstore.domain.category.model.Category;
import com.solve.bookstore.domain.category.model.CategoryId;
import com.solve.bookstore.infrastructure.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    /**
     * Entity -> Domain
     */
    public Category toDomain(CategoryEntity entity){
        if(entity == null) return null;
        return new Category(
                new CategoryId(entity.getId()),
                entity.getName()
        );
    }

    /**
     * Domain -> Entity
     */
    public CategoryEntity toEntity(Category domain){
        if(domain == null) return null;
        return CategoryEntity.from(domain.getId().toString(), domain.getName());
    }
}
