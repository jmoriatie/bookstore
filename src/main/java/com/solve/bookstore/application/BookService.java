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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.bookCategoryRepository = bookCategoryRepository;
    }

    // TODO 구현 -> 메서드 분리 -> Class 분리 단계별 리팩토링 필요
    // 요구사항
    // --- 도메인 기능 ---
    // 도서 카테고리 변경 가능 updateCategorys
    @Transactional
    public CategoryChangedResponse changeCategories(String bookId, CategoryUpdateRequest request) {
        if (hasUpdatableCategory(request))
            return new CategoryChangedResponse(Collections.emptySet());

        Set<BookId> sameIsbnBookIds = getSameIsbnBooks(getBook(bookId));
        Set<CategoryId> newCategoryIds = getCategoryIds(request);
        validateCategory(newCategoryIds);

        bookCategoryRepository.deleteByBookIdIn(sameIsbnBookIds); // 연관관계 삭제
        saveNewCategories(sameIsbnBookIds, newCategoryIds);

        return new CategoryChangedResponse(sameIsbnBookIds);
    }

    private void saveNewCategories(Set<BookId> sameIsbnBookIds, Set<CategoryId> newCategoryIds) {
        List<BookCategory> newBookCategories = new ArrayList<>();
        for (BookId bId : sameIsbnBookIds) {
            for (CategoryId cId : newCategoryIds) {
                newBookCategories.add(BookCategory.create(bId, cId));
            }
        }
        bookCategoryRepository.saveAll(newBookCategories);
    }

    /**
     * String CategoryId -> new CategoryId()
     */
    private static Set<CategoryId> getCategoryIds(CategoryUpdateRequest request) {
        return request.categoryIds().stream()
                .map(CategoryId::new)
                .collect(Collectors.toSet());
    }

    private static boolean hasUpdatableCategory(CategoryUpdateRequest request) {
        return request.categoryIds().isEmpty();
    }

    private Book getBook(String bookId) {
        return bookRepository.findById(new BookId(bookId));

    }

    /**
     * 동일 ISBN 을 가진 도서들 찾기
     */
    private Set<BookId> getSameIsbnBooks(Book book) {
        List<Book> sameIsbnBooks = bookRepository.findByIsbn(book.getIsbn().toString()); // isbn 으로 전체 찾기
        return sameIsbnBooks.stream()
                .map(Book::getId)
                .collect(Collectors.toSet());
    }

    /**
     * 존재하지 않는 카테고리 ID validation
     */
    private void validateCategory(Set<CategoryId> newCategoryIds) {
        Set<CategoryId> existingCategoryIds = categoryRepository.findAllIdsByIds(newCategoryIds);
        Set<CategoryId> nonExistingCategoryIds = new HashSet<>(newCategoryIds);
        if(!existingCategoryIds.isEmpty())
            nonExistingCategoryIds.removeAll(existingCategoryIds);

        if (!nonExistingCategoryIds.isEmpty()) {
            throw new IllegalArgumentException("카테고리를 찾을 수 없습니다. 카테고리 ID: " +
                    nonExistingCategoryIds.stream()
                            .map(CategoryId::toString)
                            .collect(Collectors.joining(", ")));
        }
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
