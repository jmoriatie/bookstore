package com.solve.bookstore.application;

import com.solve.bookstore.application.dto.CategoryUpdateRequest;
import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.model.Isbn;
import com.solve.bookstore.domain.book.repository.BookRepository;
import com.solve.bookstore.domain.bookcategory.repository.BookCategoryRepository;
import com.solve.bookstore.domain.category.model.Category;
import com.solve.bookstore.domain.category.model.CategoryId;
import com.solve.bookstore.domain.category.repository.CategoryRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


class BookServiceTest {



    @Nested
    @ExtendWith(MockitoExtension.class)
    class MockitoTest{
        @InjectMocks
        BookService bookService;
        @Mock
        BookRepository bookRepository;
        @Mock
        CategoryRepository categoryRepository;
    }

    @Nested
    @SpringBootTest
    class ClassicTest{
        @Autowired
        BookService bookService;
        @Autowired
        BookRepository bookRepository;
        @Autowired
        CategoryRepository categoryRepository;
        @Autowired
        BookCategoryRepository bookCategoryRepository;

//        @BeforeEach
//        void beforeEach(){
//            bookRepository.deleteAll();
//            categoryRepository.deleteAll();
//            bookCategoryRepository.deleteAll();
//        }

        @Test
        void saveBookCategory(){

        }

//        @Test
//        @DisplayName("카테고리 전체 변경 서비스 로직 테스트: isbn 식별")
//        void changeCategorys() {
//            // given
//            Category category1 = new Category(new CategoryId("c1"), "카테고리1");
//            Category category2 = new Category(new CategoryId("c2"), "카테고리1");
//            Category category3 = new Category(new CategoryId("c3"), "new카테고리1");
//            Category category4 = new Category(new CategoryId("c4"), "new카테고리1");
//
//            List<Category> oldCategorys = List.of(category1, category2);
//            List<Category> newCategorys = List.of(category3, category4);
//
//            Isbn isbn = new Isbn("aaa");
//            Book book1 = Book.builder()
//                    .id(new BookId("b1"))
//                    .title("책1")
//                    .isbn(isbn)
//                    .categorys(oldCategorys)
//                    .build();
//
//            Book book2 = Book.builder()
//                    .id(new BookId("b2"))
//                    .title("책2")
//                    .isbn(isbn)
//                    .categorys(oldCategorys)
//                    .build();
//
//            List<String> categoryIds = newCategorys.stream().map(c -> c.getId().toString()).toList();
//            CategoryUpdateRequest request = new CategoryUpdateRequest(categoryIds, book1.getId().toString());
//            // when
//            List<BookId> bookIds = bookRepository.saveAll(List.of(book1, book2));
//            assertTrue(book1.getCategorys().containsAll(oldCategorys));
//            assertTrue(book2.getCategorys().containsAll(oldCategorys));
//
//            bookService.changeCategorys(request);
//
//            List<Book> changedBooks = bookRepository.findAllByIds(bookIds);
//
//            // then
//            assertEquals(changedBooks.size(), 2); // Book 총 2개 확인
//            assertTrue(changedBooks.get(0).getCategorys().containsAll(newCategorys)); // 변경됐나 확인
//            assertTrue(changedBooks.get(1).getCategorys().containsAll(newCategorys));
//            assertEquals(changedBooks.get(0).getCategorys().size(), 2); // 추가X 확인
//            assertEquals(changedBooks.get(1).getCategorys().size(), 2);
//        }
    }





}