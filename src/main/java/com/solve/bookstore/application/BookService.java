package com.solve.bookstore.application;

import com.solve.bookstore.application.dto.CategoryChangedResponse;
import com.solve.bookstore.application.dto.CategoryUpdateRequest;
import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.repository.BookRepository;
import com.solve.bookstore.domain.bookcategory.model.BookCategory;
import com.solve.bookstore.domain.bookcategory.repository.BookCategoryRepository;
import com.solve.bookstore.domain.category.model.CategoryId;
import com.solve.bookstore.domain.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.bookCategoryRepository = bookCategoryRepository;
    }

    // 요구사항
    // --- 도메인 기능 ---
    // TODO 도서 카테고리 변경 가능 updateCategorys
    @Transactional
    public CategoryChangedResponse changeCategorys(CategoryUpdateRequest request) {
        // TODO 기능 메서드화하기, bookCategoryRepository 서비스 분리?
        // 업데이트할 카테고리 없으면 pass
        if (request.categoryIds().isEmpty()) {
            return new CategoryChangedResponse(Collections.emptySet());
        }

        Book book = bookRepository.findById(new BookId(request.bookId()))
                .orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다. 도서 ID:" + request.bookId()));

        List<Book> sameTitleBooks = bookRepository.findByIsbn(book.getIsbn().toString()); // isbn 으로 전체 찾기
        Set<BookId> sameTitleBookIds = sameTitleBooks.stream()
                .map(Book::getId)
                .collect(Collectors.toSet());

        // 카테고리 찾기
        Set<CategoryId> newCategoryIds = request.categoryIds().stream()
                .map(CategoryId::new)
                .collect(Collectors.toSet());

        // 존재하지 않는 카테고리 ID 찾기
        Set<CategoryId> existingCategoryIds = categoryRepository.findAllIdsByIds(newCategoryIds);
        Set<CategoryId> nonExistingCategoryIds = new HashSet<>(newCategoryIds);
        nonExistingCategoryIds.removeAll(existingCategoryIds);

        // 존재하지 않는 카테고리가 있으면 예외 발생
        if (!nonExistingCategoryIds.isEmpty()) {
            throw new IllegalArgumentException("카테고리를 찾을 수 없습니다. 카테고리 ID: " +
                    nonExistingCategoryIds.stream()
                            .map(CategoryId::toString)
                            .collect(Collectors.joining(", ")));
        }

        // 북카테고리 삭제
        bookCategoryRepository.deleteBookIds(sameTitleBookIds);

        // 재등록
        List<BookCategory> newBookCategories = new ArrayList<>();
        for (BookId bId : sameTitleBookIds) {
            for (CategoryId cId : newCategoryIds) {
                newBookCategories.add(BookCategory.create(bId, cId));
            }
        }
        bookCategoryRepository.savaAll(newBookCategories);

        return new CategoryChangedResponse(sameTitleBookIds);
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
