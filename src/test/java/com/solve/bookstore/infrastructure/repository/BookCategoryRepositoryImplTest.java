package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.model.Isbn;
import com.solve.bookstore.domain.bookcategory.model.BookCategory;
import com.solve.bookstore.domain.category.model.CategoryId;
import com.solve.bookstore.infrastructure.entity.BookCategoryEntity;
import com.solve.bookstore.infrastructure.entity.BookEntity;
import com.solve.bookstore.infrastructure.entity.CategoryEntity;
import com.solve.bookstore.infrastructure.repository.mapper.BookCategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
class BookCategoryRepositoryImplTest {

    // 더미 데이터
    private BookId bookId;
    private CategoryId categoryId;
    private BookCategory bookCategory;
    private BookEntity bookEntity;
    private CategoryEntity categoryEntity;
    private BookCategoryEntity bookCategoryEntity;
    private BookCategoryEntity savedBookCategoryEntity;
    private BookCategory savedBookCategory;


    @Nested
    @SpringBootTest(properties = {
            "logging.level.com.solve.bookstore.application=DEBUG",
            "logging.level.com.solve.bookstore.infrastructure=DEBUG"
    })
    class classicTest {
        @Autowired
        private BookCategoryJpaRepository bookCategoryJpaRepository;
        @Autowired
        private BookJpaRepository bookJpaRepository;
        @Autowired
        private CategoryJpaRepository categoryJpaRepository;
        @Autowired
        private BookCategoryRepositoryImpl bookCategoryRepository;

        @BeforeEach
        void setUp() {
            bookCategoryJpaRepository.deleteAll();
            bookJpaRepository.deleteAll();
            categoryJpaRepository.deleteAll();

            log.debug("### success bookRepository.deleteAll()");
            log.debug("### success categoryRepository.deleteAll()");
            log.debug("### success bookCategoryRepository.deleteAll()");

            bookId = new BookId("book-1");
            categoryId = new CategoryId("category-1");

            bookEntity = BookEntity.create(bookId.toString(), "제목1", "작가1", null, "isbn-1");
            categoryEntity = CategoryEntity.create(categoryId.toString(), "카테고리1");

            bookCategoryEntity = BookCategoryEntity.create(bookEntity, categoryEntity);
        }

        @Test
        @DisplayName("BookCategory 저장 - 성공")
        void save_Success() {
            // given
            BookEntity savedBook = bookJpaRepository.save(bookEntity);
            CategoryEntity savedCategory = categoryJpaRepository.save(categoryEntity);
            BookCategory bookCategory = BookCategory.create(bookId, categoryId);

            // when
            BookCategory result = bookCategoryRepository.save(bookCategory);

            // then
            assertNotNull(result);
            assertEquals(result.getBookId().toString(), savedBook.getId());
            assertEquals(result.getCategoryId().toString(), savedCategory.getId());
        }

        @Test
        @DisplayName("BookCategory로 부터 Category Ids 찾기")
        void findCategoryIdByBookIdIn(){
            // given
            CategoryId categoryId1 = new CategoryId("category-1");
            CategoryId categoryId2 = new CategoryId("category-2");

            bookEntity = BookEntity.create(bookId.toString(), "제목1", "작가1", null, "isbn-1");
            bookJpaRepository.save(bookEntity);

            CategoryEntity categoryEntity1 = categoryJpaRepository.save(CategoryEntity.create(categoryId1.toString(), "카테고리-1"));
            CategoryEntity categoryEntity2 = categoryJpaRepository.save(CategoryEntity.create(categoryId2.toString(), "카테고리-2"));

            BookCategoryEntity bookCategoryEntity1 = BookCategoryEntity.create(bookEntity, categoryEntity1);
            BookCategoryEntity bookCategoryEntity2 = BookCategoryEntity.create(bookEntity, categoryEntity2);

            bookCategoryJpaRepository.save(bookCategoryEntity1);
            bookCategoryJpaRepository.save(bookCategoryEntity2);

            // when
            Set<String> foundCategoryIds = bookCategoryJpaRepository.findCategory_IdByBook_IdIn(bookId.toString());
            foundCategoryIds.forEach(log::debug);

            //then
            assertTrue(foundCategoryIds.contains(categoryId1.toString()));
            assertTrue(foundCategoryIds.contains(categoryId2.toString()));
        }

    }

    @Nested
    class MockitoTest {
        @Mock
        private BookCategoryJpaRepository bookCategoryJpaRepository;
        @Mock
        private BookJpaRepository bookJpaRepository;
        @Mock
        private CategoryJpaRepository categoryJpaRepository;
        @Mock
        private BookCategoryMapper bookCategoryMapper;
        @InjectMocks
        private BookCategoryRepositoryImpl bookCategoryRepository;

        @BeforeEach
        void setUp() {
            bookId = new BookId("book-1");
            categoryId = new CategoryId("category-1");

            bookEntity = BookEntity.create(bookId.toString(), "제목1", "작가1", null, "isbn-1");

            bookCategory = BookCategory.create(bookId, categoryId);
            categoryEntity = CategoryEntity.create(categoryId.toString(), "카테고리1");
            bookCategoryEntity = BookCategoryEntity.create(bookEntity, categoryEntity);

            savedBookCategory = BookCategory.create(bookId, categoryId);
            savedBookCategoryEntity = BookCategoryEntity.create(bookEntity, categoryEntity);
        }

        @Test
        @DisplayName("BookCategory 저장 - 성공")
        void save_Success() {
            // given
            when(bookJpaRepository.findById(bookId.toString())).thenReturn(Optional.of(bookEntity));
            when(categoryJpaRepository.findById(categoryId.toString())).thenReturn(Optional.of(categoryEntity));
            when(bookCategoryJpaRepository.save(any(BookCategoryEntity.class))).thenReturn(savedBookCategoryEntity);
            when(bookCategoryMapper.toDomain(savedBookCategoryEntity)).thenReturn(savedBookCategory);

            // when
            BookCategory result = bookCategoryRepository.save(bookCategory);

            // then
            assertNotNull(result);
            assertEquals(bookId, result.getBookId());
            assertEquals(categoryId, result.getCategoryId());

            verify(bookJpaRepository).findById(bookId.toString());
            verify(categoryJpaRepository).findById(categoryId.toString());
            verify(bookCategoryJpaRepository).save(any(BookCategoryEntity.class));
            verify(bookCategoryMapper).toDomain(savedBookCategoryEntity);
        }

        @Test
        @DisplayName("BookCategory 저장 - Book ID가 존재하지 않는 경우")
        void save_BookNotFound() {
            // given
            when(bookJpaRepository.findById(bookId.toString())).thenReturn(Optional.empty());

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                bookCategoryRepository.save(bookCategory);
            });

            // 에러 메시지 검증
            assertTrue(exception.getMessage().contains("없는 도서 ID 입니다"));
            assertTrue(exception.getMessage().contains(bookId.toString()));

            verify(bookJpaRepository).findById(bookId.toString());
            verify(categoryJpaRepository, never()).findById(anyString());
            verify(bookCategoryJpaRepository, never()).save(any());
        }

        @Test
        @DisplayName("BookCategory 저장 - Category ID가 없을 때")
        void save_CategoryNotFound() {
            // given
            when(bookJpaRepository.findById(bookId.toString())).thenReturn(Optional.of(bookEntity));
            when(categoryJpaRepository.findById(categoryId.toString())).thenReturn(Optional.empty());

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                bookCategoryRepository.save(bookCategory);
            });

            // 에러 메시지 검증
            assertTrue(exception.getMessage().contains("없는 카테고리 ID 입니다"));
            assertTrue(exception.getMessage().contains(categoryId.toString()));

            verify(bookJpaRepository).findById(bookId.toString());
            verify(categoryJpaRepository).findById(categoryId.toString());
            verify(bookCategoryJpaRepository, never()).save(any());
        }

        @Test
        @DisplayName("BookCategory 저장 - Mapper 변환 예외")
        void save_MapperException() {
            // given
            when(bookJpaRepository.findById(bookId.toString())).thenReturn(Optional.of(bookEntity));
            when(categoryJpaRepository.findById(categoryId.toString())).thenReturn(Optional.of(categoryEntity));
            when(bookCategoryJpaRepository.save(any(BookCategoryEntity.class))).thenReturn(savedBookCategoryEntity);
            when(bookCategoryMapper.toDomain(any(BookCategoryEntity.class))).thenThrow(new RuntimeException("도메인 매핑 중 오류 발생"));

            // when & then
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                bookCategoryRepository.save(bookCategory);
            });

            // 에러 메시지 검증
            assertEquals("도메인 매핑 중 오류 발생", exception.getMessage());

            verify(bookJpaRepository).findById(bookId.toString());
            verify(categoryJpaRepository).findById(categoryId.toString());
            verify(bookCategoryJpaRepository).save(any(BookCategoryEntity.class));
            verify(bookCategoryMapper).toDomain(any(BookCategoryEntity.class));
        }
    }
}