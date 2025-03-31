package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.bookcategory.model.BookCategory;
import com.solve.bookstore.domain.bookcategory.model.BookCategoryId;
import com.solve.bookstore.domain.bookcategory.repository.BookCategoryRepository;
import com.solve.bookstore.infrastructure.entity.BookCategoryEntity;
import com.solve.bookstore.infrastructure.entity.BookEntity;
import com.solve.bookstore.infrastructure.entity.CategoryEntity;
import com.solve.bookstore.infrastructure.repository.mapper.BookCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class BookCategoryRepositoryImpl implements BookCategoryRepository {

    private final BookCategoryJpaRepository bookCategoryJpaRepository;
    private final BookJpaRepository bookJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final BookCategoryMapper bookCategoryMapper;

    @Override
    public List<BookCategory> findByBookIds(Set<BookId> bookIds) {
        return null;
    }

    @Override
    public BookCategory save(BookCategory bookCategory) {
        String bookIdStr = bookCategory.getBookId().toString();
        String categoryIdStr = bookCategory.getCategoryId().toString();

        BookEntity bookEntity = bookJpaRepository.findById(bookIdStr)
                .orElseThrow(() -> new IllegalArgumentException("없는 도서 ID 입니다. ID: " + bookIdStr));

        CategoryEntity categoryEntity = categoryJpaRepository.findById(categoryIdStr)
                .orElseThrow(() -> new IllegalArgumentException("없는 카테고리 ID 입니다. ID: " + categoryIdStr));

        BookCategoryEntity entity = BookCategoryEntity.create(bookEntity, categoryEntity);
        BookCategoryEntity savedEntity = bookCategoryJpaRepository.save(entity);
        return bookCategoryMapper.toDomain(savedEntity);
    }

    @Override
    public int deleteBookIds(Set<BookId> bookIds) {
        return 0;
    }

    @Override
    public List<BookCategoryId> savaAll(List<BookCategory> bookCategories) {
        return null;
    }

    @Override
    public int deleteAll() {
        return 0;
    }
}
