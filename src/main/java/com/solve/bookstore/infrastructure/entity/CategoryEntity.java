package com.solve.bookstore.infrastructure.entity;

import com.solve.bookstore.infrastructure.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorys")
@Getter
@NoArgsConstructor
public class CategoryEntity extends BaseEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<BookCategoryEntity> bookCategorys = new ArrayList<>(); // 연관 매핑
}
