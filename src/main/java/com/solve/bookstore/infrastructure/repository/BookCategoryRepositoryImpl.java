package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.bookcategory.model.BookCategory;
import com.solve.bookstore.domain.bookcategory.repository.BookCategoryRepository;
import com.solve.bookstore.domain.category.model.Category;
import com.solve.bookstore.domain.category.model.CategoryId;
import com.solve.bookstore.infrastructure.entity.BookCategoryEntity;
import com.solve.bookstore.infrastructure.entity.BookEntity;
import com.solve.bookstore.infrastructure.entity.CategoryEntity;
import com.solve.bookstore.infrastructure.repository.dto.BookCategoryQueryResult;
import com.solve.bookstore.infrastructure.repository.mapper.BookCategoryMapper;
import com.solve.bookstore.infrastructure.repository.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Book & Category 연계 레포지토리
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class BookCategoryRepositoryImpl implements BookCategoryRepository {

    private final BookJpaRepository bookJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final BookCategoryJpaRepository bookCategoryJpaRepository;
    private final BookCategoryMapper bookCategoryMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public Set<CategoryId> findCategoryIdByBookIdIn(BookId bookId) {
        Set<String> categoryIdsStr = bookCategoryJpaRepository.findCategory_IdByBook_IdIn(bookId.toString());
        return categoryIdsStr.stream()
                .map(CategoryId::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<BookId> findBookIdByCategoryIdIn(CategoryId categoryId) {
        Set<String> bookIdsStr = bookCategoryJpaRepository.findBook_IdByCategory_IdIn(categoryId.toString());
        return bookIdsStr.stream()
                .map(BookId::new)
                .collect(Collectors.toSet());
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
        log.info("saved BookCategory bookId={}, categoryId={}, bookTitle={}, categoryName={}", bookIdStr, categoryIdStr, bookEntity.getTitle(), categoryEntity.getName());
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
    public void deleteByBookIdIn(Set<BookId> bookIds) {
        Set<String> idsStr = bookIds.stream()
                .map(BookId::toString).collect(Collectors.toSet());
        bookCategoryJpaRepository.deleteByBook_IdIn(idsStr);
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

    @Override
    public List<BookCategory> findByCategoryId(CategoryId categoryId) {
        List<BookCategoryEntity> entities = bookCategoryJpaRepository.findByCategory_Id(categoryId.toString());
        return entities.stream()
                .map(bookCategoryMapper::toDomain)
                .toList();
    }

    @Override
    public Map<BookId, Set<Category>> findCategorysByBookIds(Set<BookId> bookIds) {
        Set<String> booIdsStr = bookIds.stream().map(BookId::toString).collect(Collectors.toSet());
        List<BookCategoryQueryResult> results = bookCategoryJpaRepository.findCategoryWithBookIds(booIdsStr);
        return results.stream()
                .collect(Collectors.groupingBy(
                                result -> new BookId(result.bookId()),
                                Collectors.mapping(
                                        result -> categoryMapper.toDomain(result.category()),
                                        Collectors.toSet()
                                )
                        )
                );
    }
}
