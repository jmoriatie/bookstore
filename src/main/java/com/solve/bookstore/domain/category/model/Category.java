package com.solve.bookstore.domain.category.model;

import lombok.Getter;

@Getter
public class Category {

    private CategoryId id;
    private String name;

    public Category(CategoryId id, String name) {
        validate(name);
        this.id = id != null? id : new CategoryId();
        this.name = name;
    }

    private void validate(String name){
        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("카테고리명은 필수값 입니다.");
        }
    }
}
