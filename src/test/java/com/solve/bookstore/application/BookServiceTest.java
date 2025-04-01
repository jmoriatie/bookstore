package com.solve.bookstore.application;

import com.solve.bookstore.application.dto.BookCreateRequest;
import com.solve.bookstore.application.dto.CategoryUpdateRequest;
import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.model.BookStatus;
import com.solve.bookstore.domain.book.model.Isbn;
import com.solve.bookstore.domain.book.repository.BookRepository;
import com.solve.bookstore.domain.bookcategory.model.BookCategory;
import com.solve.bookstore.domain.bookcategory.repository.BookCategoryRepository;
import com.solve.bookstore.domain.category.model.Category;
import com.solve.bookstore.domain.category.model.CategoryId;
import com.solve.bookstore.domain.category.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Slf4j
class BookServiceTest {

    @Nested
    @ExtendWith(MockitoExtension.class)
    class MockitoTest {
        @InjectMocks
        BookService bookService;
        @Mock
        BookRepository bookRepository;
        @Mock
        CategoryRepository categoryRepository;
    }

    @Nested
    @SpringBootTest(properties = {
            "logging.level.com.solve.bookstore.application=DEBUG",
            "logging.level.com.solve.bookstore.infrastructure=DEBUG"
    })
    class ClassicTest {
        @Autowired
        BookService bookService;
        @Autowired
        BookRepository bookRepository;
        @Autowired
        CategoryRepository categoryRepository;
        @Autowired
        BookCategoryRepository bookCategoryRepository;
        @Autowired
        TransactionTemplate transactionTemplate;

        @BeforeEach
        void beforeEach() {
            bookRepository.deleteAll();
            categoryRepository.deleteAll();
            bookCategoryRepository.deleteAll();

            log.debug("### success bookRepository.deleteAll()");
            log.debug("### success categoryRepository.deleteAll()");
            log.debug("### success bookCategoryRepository.deleteAll()");
        }

        @Test
        @DisplayName("책 생성 - 성공")
        void createBook_success() {
            // given
            Category category = new Category(new CategoryId("c3"), "NEW-CATEGORY-1");
            Category savedCategory = categoryRepository.save(category);

            // request
            BookCreateRequest request = new BookCreateRequest(
                    "책1",
                    "일작가",
                    "설설명명",
                    "ISBN-aaa",
                    List.of(category.getId().toString())
            );

            // when
            Book savedBook = bookService.createBook(request);
            List<BookCategory> foundBookCategories = bookCategoryRepository.findByBookId(savedBook.getId());
            BookCategory foundBookCategory = foundBookCategories.get(0);

            // then
            assertEquals(foundBookCategories.size(),1);
            assertEquals(foundBookCategory.getBookId(), savedBook.getId());
            assertEquals(foundBookCategory.getCategoryId(), savedCategory.getId());
        }

        @Test
        @DisplayName("도서 create - 카테고리 null, emptyList exception")
        void createBook_nullCategory() {
            // given
            Category category = new Category(new CategoryId("c3"), "NEW-CATEGORY-1");
            Category savedCategory = categoryRepository.save(category);

            // request
            BookCreateRequest emptyCategoryRequest = new BookCreateRequest(
                    "책1",
                    "일작가",
                    "설설명명",
                    "ISBN-aaa",
                    List.of()
            );

            BookCreateRequest nullCategoryRequest = new BookCreateRequest(
                    "책1",
                    "일작가",
                    "설설명명",
                    "ISBN-aaa",
                    null
            );

            // when & then
            assertThrows(IllegalArgumentException.class, () -> bookService.createBook(emptyCategoryRequest));
            assertThrows(IllegalArgumentException.class, () -> bookService.createBook(nullCategoryRequest));
        }

        @Test
        @DisplayName("카테고리 전체 변경 서비스 로직 테스트: isbn 식별")
        void changeCategories() {
            // given
            // book
            Isbn isbn = new Isbn("aaa");
            Book book1 =
                    Book.rebuild(new BookId("b1"), "책1", "일작가", "설설명명", BookStatus.AVAILABLE, isbn);
            Book book2 =
                    Book.rebuild(new BookId("b2"), "책1", "일작가", "설설명명", BookStatus.AVAILABLE, isbn);

            bookRepository.save(book1);
            bookRepository.save(book2);

            // category
            Category category1 = new Category(new CategoryId("c1"), "카테고리1");
            Category category2 = new Category(new CategoryId("c2"), "카테고리2");
            Category newCategory1 = new Category(new CategoryId("c3"), "NEW-CATEGORY-1");
            Category newCategory2 = new Category(new CategoryId("c4"), "NEW-CATEGORY-2");

            List<Category> oldCategories = List.of(category1, category2);
            List<Category> newCategories = List.of(newCategory1, newCategory2);

            categoryRepository.saveAll(oldCategories);
            categoryRepository.saveAll(newCategories);

            List<String> categoryIds = newCategories.stream().map(c -> c.getId().toString()).toList();

            // book-category: oldCategories 저자
            List<BookCategory> bookCategories = List.of(
                    BookCategory.create(book1.getId(), category1.getId()),
                    BookCategory.create(book1.getId(), category2.getId()),
                    BookCategory.create(book2.getId(), category1.getId()),
                    BookCategory.create(book2.getId(), category2.getId())
            );
            List<BookCategory> savedBookCategories = bookCategoryRepository.saveAll(bookCategories);

            log.debug("### Before change ###");
            savedBookCategories.forEach(sbc -> log.debug("saved old bookId={} categoryId={}", sbc.getBookId().toString(), sbc.getCategoryId().toString()));

            // request 생성
            CategoryUpdateRequest request = new CategoryUpdateRequest(categoryIds);

            // when
            bookService.changeCategories(book1.getId().toString(), request);

            List<BookCategory> foundBookCategory1 = bookCategoryRepository.findByBookId(book1.getId());
            List<BookCategory> foundBookCategory2 = bookCategoryRepository.findByBookId(book2.getId());

            log.debug("### After change ###");
            foundBookCategory1.forEach(bc -> log.debug("Book1 categoryId={}", bc.getCategoryId().toString()));
            foundBookCategory2.forEach(bc -> log.debug("Book2 categoryId={}", bc.getCategoryId().toString()));

            List<Category> foundNewCategories1 = categoryRepository.findAllByIds(foundBookCategory1.stream()
                    .map(BookCategory::getCategoryId)
                    .collect(Collectors.toSet())); // request 로 변경 요청한 book
            List<Category> foundNewCategories2 = categoryRepository.findAllByIds(
                    foundBookCategory2.stream()
                            .map(BookCategory::getCategoryId)
                            .collect(Collectors.toSet())
            ); // 요청된 book 과 ISBN 같은 book

            // id 비교
            List<CategoryId> foundNewCategories1Ids = foundNewCategories1.stream()
                    .map(Category::getId)
                    .toList();
            List<CategoryId> foundNewCategories2Ids = foundNewCategories2.stream()
                    .map(Category::getId)
                    .toList();

            // then
            assertEquals(foundBookCategory1.size(), 2);
            assertEquals(foundBookCategory2.size(), 2);
            assertTrue(foundNewCategories1Ids.contains(newCategory1.getId()));
            assertTrue(foundNewCategories1Ids.contains(newCategory2.getId()));
            assertTrue(foundNewCategories2Ids.contains(newCategory1.getId()));
            assertTrue(foundNewCategories2Ids.contains(newCategory2.getId()));
        }
    }
}