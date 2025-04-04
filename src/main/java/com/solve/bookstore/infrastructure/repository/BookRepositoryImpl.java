package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.model.BookStatus;
import com.solve.bookstore.domain.book.model.Isbn;
import com.solve.bookstore.domain.book.repository.BookRepository;
import com.solve.bookstore.infrastructure.entity.BookEntity;
import com.solve.bookstore.infrastructure.repository.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {

    private final BookJpaRepository bookJpaRepository; // BookRepository jpa 활용
    private final BookMapper bookMapper;

    @Override
    public Book save(Book book) {
        BookEntity savedEntity = bookJpaRepository.save(bookMapper.toEntity(book));
        log.info("saved Book id={} title={} author={} status={} ISBN={}",savedEntity.getId(), savedEntity.getTitle(), savedEntity.getAuthor(), savedEntity.getStatus(), savedEntity.getIsbn());
        return bookMapper.toDomain(savedEntity);
    }

    @Override
    public Book findById(BookId id) {
        BookEntity entity = bookJpaRepository.findById(id.toString())
                .orElseThrow(() -> new IllegalArgumentException("없는 도서 ID 입니다. ID: " + id));
        return bookMapper.toDomain(entity);
    }

    @Override
    public List<Book> findByIsbn(Isbn isbn) {
        List<BookEntity> entities = bookJpaRepository.findByIsbn(isbn.toString());
        return entities.stream()
                .map(bookMapper::toDomain)
                .toList();
    }

    @Override
    public List<Book> findByIsbnAndStatus(Isbn isbn, BookStatus status) {
        List<BookEntity> entities = bookJpaRepository.findByIsbnAndStatus(isbn.toString(), status.name());
        return entities.stream()
                .map(bookMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteAll() {
        bookJpaRepository.deleteAll();
    }
    
    @Override
    public List<Book> findByIds(Set<BookId> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return List.of();
        }
        
        Set<String> stringIds = bookIds.stream()
                .map(BookId::toString)
                .collect(Collectors.toSet());
        
        return bookJpaRepository.findByIdIn(stringIds).stream()
                .map(bookMapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Book> findByTitleContaining(String title) {
        List<BookEntity> entities = bookJpaRepository.findByTitleContaining(title);
        return entities.stream()
                .map(bookMapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Book> findByAuthorContaining(String author) {
        List<BookEntity> entities = bookJpaRepository.findByAuthorContaining(author);
        return entities.stream()
                .map(bookMapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Book> findByTitleContainingAndAuthorContaining(String title, String author) {
        List<BookEntity> entities = bookJpaRepository.findByTitleContainingAndAuthorContaining(title, author);
        return entities.stream()
                .map(bookMapper::toDomain)
                .toList();
    }

    @Override
    public List<Book> findByStatus(BookStatus status) {
        List<BookEntity> entities = bookJpaRepository.findByStatus(status.name());
        return entities.stream()
                .map(bookMapper::toDomain)
                .toList();
    }
}
