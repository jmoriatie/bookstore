package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.bookcategory.model.BookCategory;
import com.solve.bookstore.domain.bookcategory.repository.BookCategoryRepository;
import com.solve.bookstore.infrastructure.entity.BookCategoryEntity;
import com.solve.bookstore.infrastructure.entity.BookEntity;
import com.solve.bookstore.infrastructure.entity.CategoryEntity;
import com.solve.bookstore.infrastructure.repository.mapper.BookCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Book & Category 연계 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class BookCategoryRepositoryImpl implements BookCategoryRepository {

    private final BookJpaRepository bookJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final BookCategoryJpaRepository bookCategoryJpaRepository;
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

        BookCategoryEntity savedEntity = bookCategoryJpaRepository
                .save(BookCategoryEntity.create(bookEntity, categoryEntity));
        return bookCategoryMapper.toDomain(savedEntity);
    }

    @Override
    public List<BookCategory> findByBookId(BookId bookId) {
        // book 한 개당 여러개의 category 를 가질 수 있음
        List<BookCategoryEntity> entities = bookCategoryJpaRepository.findByBook_Id(bookId.toString());
        return entities.stream()
                .map(bookCategoryMapper::toDomain)
                .toList();
    }

    @Override
    public int deleteByBookIdIn(Set<BookId> bookIds) {
        Set<String> idsStr = bookIds.stream()
                .map(BookId::toString).collect(Collectors.toSet());
        return bookCategoryJpaRepository.deleteByBook_IdIn(idsStr);
    }

    @Override
    public List<BookCategory> saveAll(List<BookCategory> bookCategories) {
        return bookCategories.stream()
                .map(this::save)
                .toList();
    }

    @Override
    public void deleteAll() {
        bookCategoryJpaRepository.deleteAll();
    }
}
