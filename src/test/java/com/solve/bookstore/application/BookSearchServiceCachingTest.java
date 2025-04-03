package com.solve.bookstore.application;

import com.solve.bookstore.application.dto.BookSearchResponse;
import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.Isbn;
import com.solve.bookstore.domain.book.repository.BookRepository;
import com.solve.bookstore.domain.bookcategory.model.BookCategory;
import com.solve.bookstore.domain.bookcategory.repository.BookCategoryRepository;
import com.solve.bookstore.domain.category.model.Category;
import com.solve.bookstore.domain.category.model.CategoryId;
import com.solve.bookstore.domain.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@EnableCaching
@ActiveProfiles("test")
class BookSearchServiceCachingTest {

    @MockitoBean
    private BookRepository bookRepository;
    @MockitoBean
    private CategoryRepository categoryRepository;
    @MockitoBean
    private BookCategoryRepository bookCategoryRepository;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private BookSearchService bookSearchService;

    private Book book1;
    private Book book2;
    private Category category;
    private CategoryId categoryId;
    private BookCategory bookCategory1;
    private BookCategory bookCategory2;

    @BeforeEach
    void setUp() {
        // book data
        book1 = Book.create("이것이 개발이다", "김개발", "개발 방법론", new Isbn("ISBN-111"));
        book2 = Book.create("자바를 잡아", "이자바", "자바기초", new Isbn("ISBN-222"));

        // category data
        categoryId = new CategoryId("cid-1");
        category = new Category(categoryId, "프로그래밍");
        
        // bookCategory data - 연관관계ㅖ
        bookCategory1 = BookCategory.create(book1.getId(), categoryId);
        bookCategory2 = BookCategory.create(book2.getId(), categoryId);
        
        // 캐시 비우기
        bookSearchService.clearAllCaches();
    }

    @Test
    @DisplayName("제목 검색 결과가 캐싱되고 두 번째 호출에서는 캐시를 사용함")
    void findBooksByTitleWithCaching() {
        // given
        String title = "이것이";
        when(bookRepository.findByTitleContaining(title)).thenReturn(List.of(book1));

        // when
        // 첫 번째 호출 - 실제 DB 호출 / 캐싱
        BookSearchResponse response1 = bookSearchService.findBooksByTitle(title);

        // 두 번째 호출 - 캐시 호출
        BookSearchResponse response2 = bookSearchService.findBooksByTitle(title);

        // then
        assertThat(response1.books()).hasSize(1);
        assertThat(response1.books().get(0).title()).isEqualTo("이것이 개발이다");
        
        assertThat(response2.books()).hasSize(1);
        assertThat(response2.books().get(0).title()).isEqualTo("이것이 개발이다");
        
        // 실제 호출 1회 확인
        verify(bookRepository, times(1)).findByTitleContaining(title);
    }

    @Test
    @DisplayName("카테고리별 도서 검색 결과가 캐싱되고 두 번째 호출에서는 캐시를 사용함")
    void findBooksByCategoryWithCaching() {
        // given
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(bookCategoryRepository.findByCategoryId(categoryId)).thenReturn(List.of(bookCategory1, bookCategory2));
        when(bookRepository.findByIds(any())).thenReturn(List.of(book1, book2));

        // when
        // 첫 번째 호출 - 실제 DB 호출 / 캐싱
        BookSearchResponse response1 = bookSearchService.findBooksByCategory(categoryId);

        // 두 번째 호출 - 캐시 호출
        BookSearchResponse response2 = bookSearchService.findBooksByCategory(categoryId);

        // then
        assertThat(response1.books()).hasSize(2);
        assertThat(response2.books()).hasSize(2);
        
        // existsById, findByCategoryId, findByIds는 각각 한 번씩만 호출되어야 함
        verify(categoryRepository, times(1)).existsById(categoryId);
        verify(bookCategoryRepository, times(1)).findByCategoryId(categoryId);
        verify(bookRepository, times(1)).findByIds(any());
    }
    
    @Test
    @DisplayName("ISBN 기반 도서 검색이 캐싱됨")
    void getSameIsbnBooksWithCaching() {
        // given
        when(bookRepository.findByIsbn(any())).thenReturn(List.of(book1, book2));

        // when
        // 첫 번째 호출 - 실제 DB 호출 / 캐싱
        BookSearchResponse response1 = bookSearchService.getSameIsbnBooks(book1);

        // 두 번째 호출 - 캐시 호출
        BookSearchResponse response2 = bookSearchService.getSameIsbnBooks(book1);

        // then
        assertThat(response1.books()).hasSize(2);
        assertThat(response2.books()).hasSize(2);
        
        // findByIsbn 메서드는 한 번만 호출되어야 함
         verify(bookRepository, times(1)).findByIsbn(any());
    }
    
    @Test
    @DisplayName("캐시 갱신 테스트")
    void refreshCacheTest() {
        // given
        String title = "자바를";
        when(bookRepository.findByTitleContaining(title)).thenReturn(List.of(book2));

        // when
        // 첫 번째 호출 - 실제 DB 호출 / 캐싱
        BookSearchResponse firstResponse = bookSearchService.findBooksByTitle(title);

        // 데이터 변경 & 캐시 갱신 (실제 bookRepository가 업데이트 되는 상황 가정)
        Book updatedBook = Book.create("자바를 잡아 NEW 개정판", "이자바", "자바기초 New", new Isbn("ISBN-333"));
        when(bookRepository.findByTitleContaining(title)).thenReturn(List.of(updatedBook));
        
        // 캐시 비우기
        bookSearchService.clearAllCaches();
        
        // 세 번째 호출 - 다시 저장소 접근
        BookSearchResponse thirdResponse = bookSearchService.findBooksByTitle(title);
        // then
        assertThat(firstResponse.books().get(0).title()).isEqualTo("자바를 잡아");
        assertThat(thirdResponse.books().get(0).title()).isEqualTo("자바를 잡아 NEW 개정판");
    }
    
    @Test
    @DisplayName("특정 도서 관련 캐시 삭제 테스트")
    void clearBookCachesTest() {
        // given
        String title = "이것이";
        when(bookRepository.findByTitleContaining(title)).thenReturn(List.of(book1));
        when(bookCategoryRepository.findCategoryIdByBookIdIn(book1.getId())).thenReturn(Set.of(categoryId));
        
        // when
        // 첫 번째 호출 - 캐싱
        bookSearchService.findBooksByTitle(title);
        
        // 캐시 삭제 - book1
        bookSearchService.clearBookCaches(book1);
        
        // 두 번째 호출 - 캐시 삭제 -> DB 다시 호출
        bookSearchService.findBooksByTitle(title);

        // then
        // 메서드 두 번 호출 (처음과 캐시 삭제 후)
        verify(bookRepository, times(2)).findByTitleContaining(title);
    }
} 