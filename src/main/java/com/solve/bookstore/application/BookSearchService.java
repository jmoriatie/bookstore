package com.solve.bookstore.application;

import com.solve.bookstore.application.dto.BookSearchResponse;
import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.model.Isbn;
import com.solve.bookstore.domain.book.repository.BookRepository;
import com.solve.bookstore.domain.bookcategory.model.BookWithCategories;
import com.solve.bookstore.domain.bookcategory.repository.BookCategoryRepository;
import com.solve.bookstore.domain.category.model.Category;
import com.solve.bookstore.domain.category.model.CategoryId;
import com.solve.bookstore.domain.category.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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


    /**
     * 동일 ISBN 도서 찾기
     */
    @Cacheable(value = "sameIsbnBooks", key = "#isbn")
    @Transactional(readOnly = true)
    public BookSearchResponse getSameIsbnBooks(String isbn) {
        if (isbn == null || isbn.trim().isEmpty())
            throw new IllegalArgumentException("검색할 ISBN은 필수입니다.");
        List<Book> books = bookRepository.findByIsbn(new Isbn(isbn));

        List<BookSearchResponse.BookInfo> bookInfos = extractBookInfos(books);

        return BookSearchResponse.from(bookInfos, bookInfos.size());
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
            return BookSearchResponse.from(Collections.emptyList(), 0); // 정상흐름 처리
        }

        Set<BookId> bookIds = bookCategoryRepository.findBookIdByCategoryIdIn(categoryId);
        if (bookIds.isEmpty())
            return BookSearchResponse.from(Collections.emptyList(), 0);

        List<BookSearchResponse.BookInfo> bookInfos = extractBookInfos(bookIds);

        return BookSearchResponse.from(bookInfos, bookInfos.size());
    }

    /**
     * 제목으로 도서 검색(부분 일치)
     */
    @Cacheable(value = "booksByTitle", key = "#title")
    @Transactional(readOnly = true)
    public BookSearchResponse findBooksByTitle(String title) {
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("검색할 제목은 필수입니다.");

        List<Book> books = bookRepository.findByTitleContaining(title);

        List<BookSearchResponse.BookInfo> bookInfos = extractBookInfos(books);

        return BookSearchResponse.from(bookInfos, bookInfos.size());
    }

    /**
     * 지은이로 도서 검색(부분 일치)
     */
    @Cacheable(value = "booksByAuthor", key = "#author")
    @Transactional(readOnly = true)
    public BookSearchResponse findBooksByAuthor(String author) {
        if (author == null || author.trim().isEmpty())
            throw new IllegalArgumentException("검색할 지은이는 필수입니다.");

        List<Book> books = bookRepository.findByAuthorContaining(author);
        List<BookSearchResponse.BookInfo> bookInfos = extractBookInfos(books);

        return BookSearchResponse.from(bookInfos, bookInfos.size());
    }

    /**
     * 제목과 지은이로 도서 검색(부분 일치)
     */
    @Cacheable(value = "booksByTitleAndAuthor", key = "#title + ':' + #author")
    @Transactional(readOnly = true)
    public BookSearchResponse findBooksByTitleAndAuthor(String title, String author) {
        if ((title == null || title.trim().isEmpty()) && (author == null || author.trim().isEmpty()))
            throw new IllegalArgumentException("검색할 제목 또는 지은이 중 하나는 필수입니다.");

        if (title == null || title.trim().isEmpty())
            return findBooksByAuthor(author);

        if (author == null || author.trim().isEmpty())
            return findBooksByTitle(title);

        List<Book> books = bookRepository.findByTitleContainingAndAuthorContaining(title, author);
        List<BookSearchResponse.BookInfo> bookInfos = extractBookInfos(books);

        return BookSearchResponse.from(bookInfos, bookInfos.size());
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

    private Set<BookId> getBookIdsFromBooks(List<Book> books) {
        if(books == null || books.isEmpty())
            return Collections.emptySet();
        return books.stream().map(Book::getId).collect(Collectors.toSet());
    }

    private List<BookWithCategories> extractBookWithCategories(List<Book> books, Map<BookId, Set<Category>> categorysByBookIdsMap) {
        if(books == null || books.isEmpty())
            return Collections.emptyList();
        return books.stream()
                .map(b -> new BookWithCategories(b, categorysByBookIdsMap.get(b.getId())))
                .toList();
    }

    private List<BookSearchResponse.BookInfo> getBookInfos(List<BookWithCategories> bookWithCategories) {
        if(bookWithCategories == null || bookWithCategories.isEmpty())
            return Collections.emptyList();
        return bookWithCategories.stream()
                .map(BookSearchResponse.BookInfo::from)
                .toList();
    }

    private List<BookSearchResponse.BookInfo> extractBookInfos(List<Book> books) {
        Map<BookId, Set<Category>> categorysByBookIdsMap
                = bookCategoryRepository.findCategorysByBookIds(getBookIdsFromBooks(books));
        return getBookInfos(extractBookWithCategories(books, categorysByBookIdsMap));
    }

    private List<BookSearchResponse.BookInfo> extractBookInfos(Set<BookId> bookIds) {
        Map<BookId, Set<Category>> categorysByBookIdsMap
                = bookCategoryRepository.findCategorysByBookIds(bookIds);
        List<Book> books = bookRepository.findByIds(bookIds);
        return getBookInfos(extractBookWithCategories(books, categorysByBookIdsMap));
    }
}
