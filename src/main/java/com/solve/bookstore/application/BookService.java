package com.solve.bookstore.application;

import com.solve.bookstore.application.dto.*;
import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.model.BookStatus;
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

    private final BookSearchService bookSearchService;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository, BookSearchService bookSearchService) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.bookSearchService = bookSearchService;
    }

    // TODO 구현 -> 메서드 분리 -> Class 분리 단계별 리팩토링 필요
    //  도메인서비스 -> categoryService : validation, id 확인 등 분리
    //  도메인서비스 -> bookCategoryService? domain 분리?

    /**
     * 도서 등록
     * 요구사항: 신규도서는 항상 카테고리가 필요하다
     */
    @Transactional
    public Book createBook(BookCreateRequest request){
        if(hasNoCategories(request.categoryIds()))
            throw new IllegalArgumentException("저장할 카테고리가 없습니다.");

        Set<CategoryId> categoryIds = request.categoryIds().stream()
                .map(CategoryId::new)
                .collect(Collectors.toSet());

        validateCategory(categoryIds);

        Book savedBook = bookRepository.save(request.toDomain());

        // BookCategory 연관관계 저장
        saveBookCategories(savedBook.getId(), categoryIds);
        return savedBook;
    }

    /**
     * 도서 카테고리 변경
     * 요구사항: 도서는 카테고리를 변경할 수 있음, 도서는 카테고리를 2개 이상 가질 수 있음
     */
    @Transactional
    public CategoryChangedResponse changeCategories(String bookId, CategoryUpdateRequest request) {
        if (hasNoCategories(request.categoryIds()))
            return new CategoryChangedResponse(Collections.emptySet());

        Set<BookId> sameIsbnBookIds = bookSearchService.getSameIsbnBooks(getBook(bookId));
        Set<CategoryId> newCategoryIds = getCategoryIds(request);
        validateCategory(newCategoryIds);

        bookCategoryRepository.deleteByBookIdIn(sameIsbnBookIds); // 연관관계 삭제
        saveBookCategoriesForAllBook(sameIsbnBookIds, newCategoryIds);

        return new CategoryChangedResponse(sameIsbnBookIds);
    }
    // TODO 훼손, 분실 대여 중단
    //  - 대여 불가로 변경 여부 확인 isAvailableForRental

    // --- 필요 ---
    // TODO CRUD 더?
    // TODO Exception 커스텀

    /**
     * 도서 상태 업데이트
     * 요구사항: 훼손, 분실로 도서 상태 변경
     */
    @Transactional
    public BookStatusChangeResponse updateAndSaveBookStatus(String bookId, BookStatusChangeRequest request) {
        Book book = bookRepository.findById(new BookId(bookId));
        if(book.getStatus().isNotAvailable())
            return BookStatusChangeResponse.notAvailable(book.getId().toString(), book.getStatus().name());

        Book savedBook = updateAndSaveBookStatus(request, book);

        return BookStatusChangeResponse.success(savedBook.getId().toString(), savedBook.getStatus().name());
    }

    private Book updateAndSaveBookStatus(BookStatusChangeRequest request, Book book) {
        BookStatus bookStatus = BookStatus.fromStr(request.bookStatus());
        book.updateStatus(bookStatus);
        return bookRepository.save(book);
    }

    /**
     * request에 category 유무 확인
     */
    private static boolean hasNoCategories(List<String> categoryIds) {
        return categoryIds == null || categoryIds.isEmpty();
    }

    /**
     * String CategoryId -> new CategoryId()
     */
    private static Set<CategoryId> getCategoryIds(CategoryUpdateRequest request) {
        return request.categoryIds().stream()
                .map(CategoryId::new)
                .collect(Collectors.toSet());
    }

    private Book getBook(String bookId) {
        return bookRepository.findById(new BookId(bookId));
    }


    /**
     * 존재하지 않는 카테고리 ID validation
     */
    private void validateCategory(Set<CategoryId> newCategoryIds) {
        Set<CategoryId> existingCategoryIds = categoryRepository.findAllIdsByIds(newCategoryIds);
        Set<CategoryId> nonExistingCategoryIds = new HashSet<>(newCategoryIds);
        nonExistingCategoryIds.removeAll(existingCategoryIds);

        if (!nonExistingCategoryIds.isEmpty()) {
            throw new IllegalArgumentException("카테고리를 찾을 수 없습니다. 카테고리 ID: " +
                    nonExistingCategoryIds.stream()
                            .map(CategoryId::toString)
                            .collect(Collectors.joining(", ")));
        }
    }

    private void saveBookCategories(BookId bookId, Set<CategoryId> categoryIds) {
        List<BookCategory> bookCategories = new ArrayList<>();
        categoryIds.forEach(categoryId ->
                bookCategories.add(BookCategory.create(bookId, categoryId)));
        bookCategoryRepository.saveAll(bookCategories);
    }

    private void saveBookCategoriesForAllBook(Set<BookId> sameIsbnBookIds, Set<CategoryId> newCategoryIds) {
        List<BookCategory> bookCategories = new ArrayList<>();
        for (BookId bId : sameIsbnBookIds) {
            for (CategoryId cId : newCategoryIds) {
                bookCategories.add(BookCategory.create(bId, cId));
            }
        }
        bookCategoryRepository.saveAll(bookCategories);
    }
}
