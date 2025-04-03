package com.solve.bookstore.application;

import com.solve.bookstore.application.dto.BookSearchResponse;
import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.repository.BookRepository;
import com.solve.bookstore.domain.bookcategory.model.BookCategory;
import com.solve.bookstore.domain.bookcategory.repository.BookCategoryRepository;
import com.solve.bookstore.domain.category.model.CategoryId;
import com.solve.bookstore.domain.category.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookSearchService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

    public BookSearchService(BookRepository bookRepository, CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.bookCategoryRepository = bookCategoryRepository;
    }

    // --- 필요 ---
    // TODO Exception 커스텀

    /**
     * 동일 ISBN 도서 찾기
     */
    @Cacheable(value = "sameIsbnBooks", key = "#book.isbn.toString()")
    @Transactional(readOnly = true)
    public BookSearchResponse getSameIsbnBooks(Book book) {
        return BookSearchResponse.from(bookRepository.findByIsbn(book.getIsbn()));
    }

    /**
     * 카테고리별 도서 검색
     */
    @Cacheable(value = "booksByCategory", key = "#categoryId.toString()")
    @Transactional(readOnly = true)
    public BookSearchResponse findBooksByCategory(CategoryId categoryId) {
        if (categoryId == null)
            throw new IllegalArgumentException("카테고리 ID는 필수입니다.");

        if (isNotExistCategory(categoryId)) {
            log.warn("존재하지 않는 카테고리 ID: {}", categoryId);
            return BookSearchResponse.from(Collections.emptyList()); // 정상흐름
        }
        
        List<BookCategory> bookCategories = bookCategoryRepository.findByCategoryId(categoryId);
        if (bookCategories.isEmpty()) {
            return BookSearchResponse.from(Collections.emptyList());
        }
        
        Set<BookId> bookIds = bookCategories.stream()
                .map(BookCategory::getBookId)
                .collect(Collectors.toSet());
        
        return BookSearchResponse.from(bookRepository.findByIds(bookIds));
    }

    /**
     * 제목으로 도서 검색(부분 일치)
     */
    @Cacheable(value = "booksByTitle", key = "#title")
    @Transactional(readOnly = true)
    public BookSearchResponse findBooksByTitle(String title) {
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("검색할 제목은 필수입니다.");
        return BookSearchResponse.from(bookRepository.findByTitleContaining(title));
    }

    /**
     * 지은이로 도서 검색(부분 일치)
     */
    @Cacheable(value = "booksByAuthor", key = "#author")
    @Transactional(readOnly = true)
    public BookSearchResponse findBooksByAuthor(String author) {
        if (author == null || author.trim().isEmpty())
            throw new IllegalArgumentException("검색할 지은이는 필수입니다.");
        return BookSearchResponse.from(bookRepository.findByAuthorContaining(author));
    }

    /**
     * 제목과 지은이로 도서 검색(부분 일치)
     */
    @Cacheable(value = "booksByTitleAndAuthor", key = "#title + ':' + #author")
    @Transactional(readOnly = true)
    public BookSearchResponse findBooksByTitleAndAuthor(String title, String author) {
        if ((title == null || title.trim().isEmpty()) && (author == null || author.trim().isEmpty())) {
            throw new IllegalArgumentException("검색할 제목 또는 지은이 중 하나는 필수입니다.");
        }
        
        if (title == null || title.trim().isEmpty()) {
            return findBooksByAuthor(author);
        }
        
        if (author == null || author.trim().isEmpty()) {
            return findBooksByTitle(title);
        }

        return BookSearchResponse.from(bookRepository.findByTitleContainingAndAuthorContaining(title, author));
    }

    /**
     * 특정 도서 캐시 전체 삭제 (도서 정보 변경 시 호출)
     */
    @Caching(evict = {
            @CacheEvict(value = "sameIsbnBooks", key = "#book.isbn.toString()"),
            @CacheEvict(value = "booksByTitle", allEntries = true),
            @CacheEvict(value = "booksByAuthor", allEntries = true),
            @CacheEvict(value = "booksByTitleAndAuthor", allEntries = true)
    })
    @Transactional(readOnly = true)
    public void clearBookCaches(Book book) {
        log.info("도서 관련 캐시 삭제: {}", book.getId());
        
        Set<CategoryId> categoryIds = bookCategoryRepository.findCategoryIdByBookIdIn(book.getId());
        for (CategoryId categoryId : categoryIds) {
            clearCategoryCache(categoryId);
        }
    }
    
    /**
     * 특정 카테고리 캐시 삭제
     */
    @CacheEvict(value = "booksByCategory", key = "#categoryId.toString()")
    public void clearCategoryCache(CategoryId categoryId) {
        log.info("카테고리 캐시 삭제: {}", categoryId);
    }
    
    /**
     * 모든 도서 검색 관련 캐시 삭제
     */
    @Caching(evict = {
            @CacheEvict(value = "sameIsbnBooks", allEntries = true),
            @CacheEvict(value = "booksByCategory", allEntries = true),
            @CacheEvict(value = "booksByTitle", allEntries = true),
            @CacheEvict(value = "booksByAuthor", allEntries = true),
            @CacheEvict(value = "booksByTitleAndAuthor", allEntries = true)
    })
    public void clearAllCaches() {
        log.info("모든 도서 검색 캐시 삭제");
    }

    private boolean isNotExistCategory(CategoryId categoryId) {
        return !categoryRepository.existsById(categoryId);
    }
}
