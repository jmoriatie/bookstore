package com.solve.bookstore.application;

import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.repository.BookRepository;
import com.solve.bookstore.domain.bookcategory.model.BookCategory;
import com.solve.bookstore.domain.bookcategory.repository.BookCategoryRepository;
import com.solve.bookstore.domain.category.model.CategoryId;
import com.solve.bookstore.domain.category.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public Set<BookId> getSameIsbnBooks(Book book) {
        List<Book> sameIsbnBooks = bookRepository.findByIsbn(book.getIsbn()); // isbn 으로 전체 찾기
        return sameIsbnBooks.stream()
                .map(Book::getId)
                .collect(Collectors.toSet());
    }

    /**
     * 카테고리별 도서 검색
     */
    public List<Book> findBooksByCategory(CategoryId categoryId) {
        if (categoryId == null)
            throw new IllegalArgumentException("카테고리 ID는 필수입니다.");

        if (isNotExistCategory(categoryId)) {
            log.warn("존재하지 않는 카테고리 ID: {}", categoryId);
            return Collections.emptyList(); // 정상흐름
        }
        
        // 해당 카테고리와 연결된 도서 ID 목록 조회
        List<BookCategory> bookCategories = bookCategoryRepository.findByCategoryId(categoryId);
        if (bookCategories.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 도서 ID로 도서 정보 조회
        Set<BookId> bookIds = bookCategories.stream()
                .map(BookCategory::getBookId)
                .collect(Collectors.toSet());
        
        return bookRepository.findByIds(bookIds);
    }


    /**
     * 제목으로 도서 검색(부분 일치)
     */
    public List<Book> findBooksByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("검색할 제목은 필수입니다.");
        }
        
        return bookRepository.findByTitleContaining(title);
    }

    /**
     * 지은이로 도서 검색(부분 일치)
     */
    public List<Book> findBooksByAuthor(String author) {
        if (author == null || author.trim().isEmpty())
            throw new IllegalArgumentException("검색할 지은이는 필수입니다.");
        
        return bookRepository.findByAuthorContaining(author);
    }

    /**
     * 제목과 지은이로 도서 검색(부분 일치)
     */
    public List<Book> findBooksByTitleAndAuthor(String title, String author) {
        if ((title == null || title.trim().isEmpty()) && (author == null || author.trim().isEmpty())) {
            throw new IllegalArgumentException("검색할 제목 또는 지은이 중 하나는 필수입니다.");
        }
        
        if (title == null || title.trim().isEmpty()) {
            return findBooksByAuthor(author);
        }
        
        if (author == null || author.trim().isEmpty()) {
            return findBooksByTitle(title);
        }
        
        return bookRepository.findByTitleContainingAndAuthorContaining(title, author);
    }

    private boolean isNotExistCategory(CategoryId categoryId) {
        return !categoryRepository.existsById(categoryId);
    }
}
