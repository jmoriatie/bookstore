package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.infrastructure.entity.BookCategoryEntity;
import com.solve.bookstore.infrastructure.entity.BookCategoryEntityId;
import com.solve.bookstore.infrastructure.repository.dto.BookCategoryQueryResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookCategoryJpaRepository extends JpaRepository<BookCategoryEntity, BookCategoryEntityId> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM BookCategoryEntity bc WHERE bc.book.id IN :bookIds")
    int deleteByBook_IdIn(Set<String> bookIds);

    @Query("SELECT bc.category.id FROM BookCategoryEntity bc WHERE bc.book.id = :bookId")
    Set<String> findCategory_IdByBook_IdIn(String bookId);

    @Query("SELECT bc.book.id FROM BookCategoryEntity bc WHERE bc.category.id = :categoryId")
    Set<String> findBook_IdByCategory_IdIn(String categoryId);

    List<BookCategoryEntity> findByBook_Id(String bookId);
    List<BookCategoryEntity> findByCategory_Id(String categoryId);

    @Query("""
            SELECT new com.solve.bookstore.infrastructure.repository.dto.BookCategoryQueryResult(
                bc.book.id,
                bc.category
            )
            FROM BookCategoryEntity bc
            WHERE bc.book.id IN :bookIds
            """)
    List<BookCategoryQueryResult> findCategoryWithBookIds(@Param("bookIds") Set<String> bookIds);
}
