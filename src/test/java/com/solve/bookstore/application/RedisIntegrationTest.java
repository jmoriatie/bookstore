package com.solve.bookstore.application;

import com.solve.bookstore.application.dto.BookSearchResponse;
import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.Isbn;
import com.solve.bookstore.domain.book.repository.BookRepository;
import com.solve.bookstore.domain.bookcategory.repository.BookCategoryRepository;
import com.solve.bookstore.domain.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@ActiveProfiles("test")
class RedisIntegrationTest {

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private BookSearchService bookSearchService;
    @MockitoBean
    private BookRepository bookRepository;
    @MockitoBean
    private CategoryRepository categoryRepository;
    @MockitoBean
    private BookCategoryRepository bookCategoryRepository;


    @BeforeEach
    void setUp() {
        bookSearchService.clearAllCaches();
    }

    @Test
    @DisplayName("캐시 매니저 정상 주입되었는지 확인")
    void cacheManagerTest() {
        Cache booksByTitleCache = cacheManager.getCache("booksByTitle");
        Cache booksByCategoryCache = cacheManager.getCache("booksByCategory");
        Cache booksByAuthorCache = cacheManager.getCache("booksByAuthor");

        // 캐시 생성 확인
        assertThat(booksByTitleCache).isNotNull();
        assertThat(booksByCategoryCache).isNotNull();
        assertThat(booksByAuthorCache).isNotNull();
    }
    
    @Test
    @DisplayName("캐시 기능이 작동하는지 확인")
    void cacheTest() {
        // given
        String title = "자바를 잡아";
        Book book = Book.create("자바를 잡아", "이자바", "자바기초", new Isbn("ISBN-222"));
        when(bookRepository.findByTitleContaining(title)).thenReturn(List.of(book));
        // 캐시 초기화
        bookSearchService.clearAllCaches();
        
        // when - 첫 번째 호출 (캐시 저장)
        BookSearchResponse firstCall = bookSearchService.findBooksByTitle(title);

        // then - 첫 번째 result
        assertThat(firstCall.books()).hasSize(1);
        assertThat(firstCall.books().get(0).title()).isEqualTo("자바를 잡아");
        
        // when - 두 번째 호출 (캐시에서 읽기)
        BookSearchResponse secondCall = bookSearchService.findBooksByTitle(title);

        // then - 두 번째 result
        assertThat(secondCall.books()).hasSize(1);
        assertThat(secondCall.books().get(0).title()).isEqualTo("자바를 잡아");
        
        // repository 메서드 한 번만 호출되었는지 확인
        verify(bookRepository, times(1)).findByTitleContaining(title);
    }
} 