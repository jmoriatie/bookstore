package com.solve.bookstore.application;

import com.solve.bookstore.application.dto.CategoryChangedResponse;
import com.solve.bookstore.application.dto.CategoryUpdateRequest;
import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.repository.BookRepository;
import com.solve.bookstore.domain.category.model.Category;
import com.solve.bookstore.domain.category.model.CategoryId;
import com.solve.bookstore.domain.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    // 요구사항
    // --- 도메인 기능 ---
    // TODO 도서 카테고리 변경 가능 updateCategorys
    @Transactional
    public CategoryChangedResponse changeCategorys(CategoryUpdateRequest request){
        Book book = bookRepository.findById(new BookId(request.bookId()))
                .orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다. 도서 ID:"+request.bookId()));

        List<Book> sameTitleBooks = bookRepository.findByIsbn(book.getIsbn().toString()); // isbn 으로 전체 찾기

        // 카테고리 찾기
        Set<CategoryId> newCategoryIds = request.categoryIds().stream()
                .map(CategoryId::new)
                .collect(Collectors.toSet());
        List<Category> newCategorys = categoryRepository.findAllByIds(newCategoryIds);

        sameTitleBooks.forEach(b -> b.updateCategorys(newCategorys)); // 카테고리 바꾸기

        List<BookId> bookIds = bookRepository.saveAll(sameTitleBooks);

        return new CategoryChangedResponse(bookIds);
    }

//    @Transactional
//    public void createBook(BookCreateRequest request){
//
//    }

    // TODO 훼손, 분실 대여 중단
    //  - 대여가능 여부 확인 isAvailableForRental
    //  - 훼손, 분실로 변경 updateCategory

    // --- 서칭 관련 ---
    // TODO 카테고리별 도서 검색
    // TODO 지은이, 제목 도서 검색

    // --- 필요 ---
    // TODO CRUD
    //  - 도서 등록 - 요구사항: 신규도서는 항상 카테고리가 필요하다
    //  - 도서 전체 업데이트?
    // TODO Exception 커스텀
}
