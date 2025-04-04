package com.solve.bookstore.application;

import com.solve.bookstore.application.dto.*;
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
import com.solve.bookstore.domain.rental.repository.RentalRepository;
import com.solve.bookstore.infrastructure.repository.RentalJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@SpringBootTest(properties = {
        "logging.level.com.solve.bookstore.application=DEBUG",
        "logging.level.com.solve.bookstore.infrastructure=DEBUG"
})
class BookServiceTest {

    @Autowired
    BookService bookService;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    BookCategoryRepository bookCategoryRepository;
    @Autowired
    RentalJpaRepository rentalJpaRepository;
    @Autowired
    TransactionTemplate transactionTemplate;

    @BeforeEach
    void beforeEach() {
        rentalJpaRepository.deleteAll();
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        bookCategoryRepository.deleteAll();

        log.debug("### success rentalJpaRepository.deleteAll()");
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
        BookCreatedResponse response = bookService.createBook(request);
        List<BookCategory> foundBookCategories = bookCategoryRepository.findByBookId(new BookId(response.bookId()));
        BookCategory foundBookCategory = foundBookCategories.get(0);

        // then
        assertEquals(foundBookCategories.size(), 1);
        assertEquals(foundBookCategory.getBookId(), new BookId(response.bookId()));
        assertEquals(foundBookCategory.getCategoryId(), savedCategory.getId());
    }

    @Test
    @DisplayName("책 생성 - 동일 ISBN 있는 도서")
    void createBook_success_existBook() {
        // given
        Category existCategory = new Category(new CategoryId("c1"), "Exist-CATEGORY-1");
        Category savedExistcategory = categoryRepository.save(existCategory);

        Category newCategory = new Category(new CategoryId("c2"), "NEW-CATEGORY-1");
        Category savedNewCategory = categoryRepository.save(newCategory);

        Isbn isbn = new Isbn("ISBN-same");
        Book book = Book.create(
                "존재하던 책",
                "존재하던 작가",
                "책설명",
                isbn);
        Book existBook = bookRepository.save(book);

        BookCategory existBookCategory = BookCategory.create(existBook.getId(), savedExistcategory.getId());
        bookCategoryRepository.save(existBookCategory);

        List<BookCategory> existBookCategories = bookCategoryRepository.findByBookId(existBook.getId());
        BookCategory savedExistBookCategory = existBookCategories.get(0);

        // request
        BookCreateRequest request = new BookCreateRequest(
                "새로운 책",
                "새로운 작가",
                "설설명명",
                isbn.toString(),
                List.of(savedNewCategory.getId().toString())
        );

        // when
        BookCreatedResponse response = bookService.createBook(request);

        Book savedBook = bookRepository.findById(new BookId(response.bookId()));
        List<BookCategory> foundBookCategories = bookCategoryRepository.findByBookId(new BookId(response.bookId()));
        BookCategory foundBookCategory = foundBookCategories.get(0);

        // then
        assertEquals(existBook.getTitle(), savedBook.getTitle());
        assertEquals(existBook.getAuthor(), savedBook.getAuthor());
        assertEquals(existBook.getIsbn(), savedBook.getIsbn());
        assertEquals(foundBookCategory.getCategoryId(), existBookCategory.getCategoryId());
    }

    @Test
    @DisplayName("도서 create - 카테고리 null, emptyList exception")
    void createBook_nullCategory() {
        // given
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


    @Test
    @DisplayName("도서 상태 업데이트 - 성공")
    void updateBookStatus_success() {
        // given
        Book book = Book.create(
                "책1",
                "일작가",
                "설설명명",
                new Isbn("ISBN-aaa"));
        Book savedBook = bookRepository.save(book);

        BookStatusChangeRequest request = new BookStatusChangeRequest("NOT_available");
        // when
        BookStatusChangeResponse updatedBook = bookService.updateAndSaveBookStatus(savedBook.getId().toString(), request);
        // then
        assertEquals(updatedBook.bookStatus(), BookStatus.NOT_AVAILABLE.name());
        assertEquals("도서상태 변경 완료", updatedBook.message());
    }

    @Test
    @DisplayName("도서 상태 업데이트 - 오류: 없는 이름")
    void updateBookStatus_faultBookStatus() {
        // given
        Book book = Book.create(
                "책1",
                "일작가",
                "설설명명",
                new Isbn("ISBN-aaa"));
        Book savedBook = bookRepository.save(book);

        BookStatusChangeRequest request = new BookStatusChangeRequest("Wrong_BOOK");
        // when & then
        assertThrows(IllegalArgumentException.class, () -> bookService.updateAndSaveBookStatus(savedBook.getId().toString(), request));
    }
}