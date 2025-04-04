package com.solve.bookstore.application.dto;

import com.solve.bookstore.domain.bookcategory.model.BookWithCategories;
import com.solve.bookstore.domain.category.model.Category;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record BookSearchResponse(
        List<BookInfo> books,
        int bookQuantity
) {

    public record CategoryInfo(String id, String name) {
        public static Set<CategoryInfo> from(Set<Category> category) {
            if(category == null || category.isEmpty())
                return Collections.emptySet();

            return category.stream()
                    .map(c -> new CategoryInfo(
                            c.getId().toString(),
                            c.getName()
                    )).collect(Collectors.toSet());
        }
    }

    public record BookInfo(
            String bookId,
            String title,
            String author,
            String description,
            String isbn,
            String bookStatus,
            Set<CategoryInfo> categories
    ) {
        public static BookInfo from(BookWithCategories bookWithCategories) {
            return new BookInfo(
                    bookWithCategories.book().getId().toString(),
                    bookWithCategories.book().getTitle(),
                    bookWithCategories.book().getAuthor(),
                    bookWithCategories.book().getDescription(),
                    bookWithCategories.book().getIsbn().toString(),
                    bookWithCategories.book().getStatus().name(),
                    CategoryInfo.from(bookWithCategories.categories())
            );
        }
    }

    public static BookSearchResponse from(List<BookInfo> bookInfos, int bookQuantity) {
        if(bookInfos == null || bookInfos.isEmpty())
            return new BookSearchResponse(Collections.emptyList(), 0);

        return new BookSearchResponse(bookInfos, bookQuantity);
    }
} 