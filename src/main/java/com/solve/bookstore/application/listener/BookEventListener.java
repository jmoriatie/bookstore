package com.solve.bookstore.application.listener;

import com.solve.bookstore.application.BookSearchService;
import com.solve.bookstore.domain.book.event.UpdatedBookEvent;
import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.repository.BookRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@Transactional
public class BookEventListener {

    private final BookSearchService bookSearchService;
    private final BookRepository bookRepository;

    public BookEventListener(BookSearchService bookSearchService, BookRepository bookRepository) {
        this.bookSearchService = bookSearchService;
        this.bookRepository = bookRepository;
    }

    @Async
    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUpdatedBookEvent(UpdatedBookEvent event){
        if(event.bookIds() == null || event.bookIds().isEmpty()) return;

        List<Book> updatedBooks = bookRepository.findByIds(event.bookIds());
        updatedBooks.forEach(bookSearchService::clearBookCaches);
    }
}
