package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.repository.BookRepository;
import com.solve.bookstore.domain.category.model.Category;
import com.solve.bookstore.infrastructure.entity.BookEntity;
import com.solve.bookstore.infrastructure.repository.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {

    private final BookJpaRepository bookJpaRepository; // BookRepository jpa 활용
    private final BookMapper bookMapper;

    @Override
    public Book save(Book book) {
        BookEntity savedEntity = bookJpaRepository.save(bookMapper.toEntity(book));
        log.info("saved Book id={} title={} author={} ISBN={}",savedEntity.getId(), savedEntity.getTitle(), savedEntity.getAuthor(), savedEntity.getIsbn());
        return bookMapper.toDomain(savedEntity);
    }

    @Override
    public List<BookId> saveAll(List<Book> books) {
        return null;
    }

    @Override
    public List<BookId> saveAll(Set<Book> books) {
        return null;
    }

    @Override
    public List<Book> findAllByIds(List<BookId> ids) {
        return null;
    }

    @Override
    public List<Book> findAllByIds(Set<BookId> ids) {
        return null;
    }

    @Override
    public Book findById(BookId id) {
        BookEntity entity = bookJpaRepository.findById(id.toString())
                .orElseThrow(() -> new IllegalArgumentException("없는 도서 ID 입니다. ID: " + id));
        return bookMapper.toDomain(entity);
    }

    @Override
    public List<Book> findByIsbn(String isbn) {
        List<BookEntity> entities = bookJpaRepository.findByIsbn(isbn);
        entities.forEach(b -> log.debug("### found book with ISBN id={} isbn={}", b.getId(), b.getIsbn()));
        return entities.stream()
                .map(bookMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteAll() {
        bookJpaRepository.deleteAll();
    }
}
