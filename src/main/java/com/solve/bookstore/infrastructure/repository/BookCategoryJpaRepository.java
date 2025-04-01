package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.infrastructure.entity.BookCategoryEntity;
import com.solve.bookstore.infrastructure.entity.BookCategoryEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookCategoryJpaRepository extends JpaRepository<BookCategoryEntity, BookCategoryEntityId> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM BookCategoryEntity bc WHERE bc.book.id IN :bookIds")
    int deleteByBook_IdIn(Set<String> bookIds);

    List<BookCategoryEntity> findByBook_Id(String bookId);
}
