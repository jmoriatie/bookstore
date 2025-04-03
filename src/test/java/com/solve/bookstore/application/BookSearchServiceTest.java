package com.solve.bookstore.application;

import com.solve.bookstore.application.dto.BookSearchResponse;
import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookSearchServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookCategoryRepository bookCategoryRepository;

    @InjectMocks
    private BookSearchService bookSearchService;

    private Book book1;
    private Book book2;
    private Category category;
    private CategoryId categoryId;
    private BookCategory bookCategory1;
    private BookCategory bookCategory2;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        book1 = Book.create("이것이 개발이다", "김개발", "개발 방법론", new Isbn("ISBN-111"));
        book2 = Book.create("자바를 잡아", "이자바", "자바기초", new Isbn("ISBN-222"));

        // 카테고리 준비
        categoryId = new CategoryId("cid-1");
        category = new Category(categoryId, "프로그래밍");
        
        // 도서-카테고리 관계 준비
        bookCategory1 = BookCategory.create(book1.getId(), categoryId);
        bookCategory2 = BookCategory.create(book2.getId(), categoryId);
    }

    @Test
    @DisplayName("ISBN으로 동일한 도서 찾기")
    void getSameIsbnBooksTest() {
        // given
        when(bookRepository.findByIsbn(any(Isbn.class))).thenReturn(List.of(book1, book2));

        // when
        BookSearchResponse response = bookSearchService.getSameIsbnBooks(book1);
        Set<BookId> result = response.books().stream().
                map(BookSearchResponse.BookInfo::bookId)
                .map(BookId::new)
                .collect(Collectors.toSet());

        // then
        assertThat(result).hasSize(2);
        assertThat(result).contains(book1.getId(), book2.getId());
    }

    @Test
    @DisplayName("카테고리별 도서 검색")
    void findBooksByCategoryTest() {
        // given
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(bookCategoryRepository.findByCategoryId(categoryId)).thenReturn(List.of(bookCategory1, bookCategory2));
        when(bookRepository.findByIds(any())).thenReturn(List.of(book1, book2));

        // when
        BookSearchResponse response = bookSearchService.findBooksByCategory(categoryId);

        BookSearchResponse.BookInfo resultBook1 = BookSearchResponse.BookInfo.from(book1);
        BookSearchResponse.BookInfo resultBook2 = BookSearchResponse.BookInfo.from(book2);
        // then
        assertThat(response.books()).hasSize(2);
        assertThat(response.books()).containsExactly(resultBook1, resultBook2);
        verify(categoryRepository, times(1)).existsById(categoryId);
        verify(bookCategoryRepository, times(1)).findByCategoryId(categoryId);
        verify(bookRepository, times(1)).findByIds(any());
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 검색 - empty list 반환")
    void findBooksByNonExistingCategoryTest() {
        // given
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        // when
        BookSearchResponse response = bookSearchService.findBooksByCategory(categoryId);
        List<BookSearchResponse.BookInfo> result = response.books();
        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("제목으로 도서 검색")
    void findBooksByTitleTest() {
        // given
        String title = "이것이";
        when(bookRepository.findByTitleContaining(title)).thenReturn(List.of(book1));

        // when
        BookSearchResponse response = bookSearchService.findBooksByTitle(title);
        List<BookSearchResponse.BookInfo> result = response.books();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("이것이 개발이다");
    }

    @Test
    @DisplayName("저자로 도서 검색 테스트")
    void findBooksByAuthorTest() {
        // given
        String author = "자바";
        when(bookRepository.findByAuthorContaining(author)).thenReturn(List.of(book2));

        // when
        BookSearchResponse response = bookSearchService.findBooksByAuthor(author);
        List<BookSearchResponse.BookInfo> result = response.books();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).author()).isEqualTo("이자바");
    }

    @Test
    @DisplayName("제목과 지은이로 도서 검색 테스트")
    void findBooksByTitleAndAuthorTest() {
        // given
        String title = "프로그래밍";
        String author = "이자바";
        when(bookRepository.findByTitleContainingAndAuthorContaining(title, author)).thenReturn(List.of(book2));

        // when
        BookSearchResponse response = bookSearchService.findBooksByTitleAndAuthor(title, author);
        List<BookSearchResponse.BookInfo> result = response.books();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("자바를 잡아");
        assertThat(result.get(0).author()).isEqualTo("이자바");
    }

    @Test
    @DisplayName("빈 제목으로 검색 - 예외")
    void findBooksByEmptyTitleTest() {
        // given
        String emptyTitle = "";

        // when & then
        assertThatThrownBy(() -> bookSearchService.findBooksByTitle(emptyTitle))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("검색할 제목은 필수입니다");
    }

    @Test
    @DisplayName("빈 지은이 검색 - 예외")
    void findBooksByEmptyAuthorTest() {
        // given
        String emptyAuthor = "";

        // when & then
        assertThatThrownBy(() -> bookSearchService.findBooksByAuthor(emptyAuthor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("검색할 지은이는 필수입니다");
    }

    @Test
    @DisplayName("빈 제목과 지은이 검색 - 예외")
    void findBooksByEmptyTitleAndAuthorTest() {
        // given
        String emptyTitle = "";
        String emptyAuthor = "";

        // when & then
        assertThatThrownBy(() -> bookSearchService.findBooksByTitleAndAuthor(emptyTitle, emptyAuthor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("검색할 제목 또는 지은이 중 하나는 필수입니다.");
    }
} 