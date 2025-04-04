package com.solve.bookstore.infrastructure.testdata;

import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.model.BookStatus;
import com.solve.bookstore.domain.book.model.Isbn;
import com.solve.bookstore.domain.book.repository.BookRepository;
import com.solve.bookstore.domain.bookcategory.model.BookCategory;
import com.solve.bookstore.domain.bookcategory.repository.BookCategoryRepository;
import com.solve.bookstore.domain.category.model.Category;
import com.solve.bookstore.domain.category.model.CategoryId;
import com.solve.bookstore.domain.category.repository.CategoryRepository;
import com.solve.bookstore.domain.rental.model.Rental;
import com.solve.bookstore.domain.rental.repository.RentalRepository;
import com.solve.bookstore.domain.user.model.User;
import com.solve.bookstore.domain.user.model.UserId;
import com.solve.bookstore.domain.user.model.UserRole;
import com.solve.bookstore.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("!test")
public class InitData implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Map<String, CategoryId> categories = createCategories(); // 카테고리 생성
        List<Book> books = createBooks(categories);// 도서 데이터 생성
        List<User> users = createUsers(); // 관리자&일반 사용자 계정 생성
        createRentals(books, users);
        showAvailableData();
    }

    private void showAvailableData() {
        List<Book> books = bookRepository.findByStatus(BookStatus.AVAILABLE);
        books.forEach(
                b -> log.info("대여가능 도서 목록: id=[{}] title=[{}], author=[{}]", b.getId(), b.getTitle(), b.getAuthor())
        );
    }

    private Map<String, CategoryId> createCategories() {
        Map<String, CategoryId> categoryMap = new HashMap<>();
        List<String> categoryNames = List.of(
                "문학", "경제경영", "인문학", "IT", "과학"
        );

        List<Category> testCategories = List.of(
                new Category(new CategoryId("cid-1"), "취미/실용/스포츠"),
                new Category(new CategoryId("cid-2"), "육아")
        );
        for(Category c : testCategories){ // 테스트 가능 카테고리
            categoryRepository.save(c);
            categoryMap.put(c.getName(), c.getId());
        }

        for (String name : categoryNames) {
            Category category = new Category(null, name);
            categoryRepository.save(category);
            categoryMap.put(name, category.getId());
        }
        return categoryMap;
    }

    private List<Book> createBooks(Map<String, CategoryId> categories) {
        List<Book> books = new ArrayList<>();
        List<BookData> bookDataList = List.of(
                new BookData("문학", "너에게 해주지 못한 말들", "권태영"),
                new BookData("문학", "단순하게 배부르게", "현영서"),
                new BookData("문학", "게으른 사랑", "권태영"),
                new BookData("경제경영", "트랜드 코리아 2322", "권태영"),
                new BookData("경제경영", "초격자 투자", "장동혁"),
                new BookData("경제경영", "파이어족 강환국의 하면 되지 않는다! 퀀트 투자", "홍길동"),
                new BookData("인문학", "진심보다 밥", "이서연"),
                new BookData("인문학", "실패에 대하여 생각하지 마라", "위성원"),
                new BookData("IT", "실리콘밸리 리더십 쉽다", "지승열"),
                new BookData("IT", "데이터분석을 위한 A 프로그래밍", "지승열"),
                new BookData("IT", "-1년차 게임 개발", "위성원"),
                new BookData("IT", "인공지능1-12", "장동혁"),
                new BookData("IT", "Skye가 알려주는 피부 채색의 비결", "권태영"),
                new BookData("과학", "자연의 발전", "장지명"),
                new BookData("과학", "코스모스 필 무렵", "이승열")
        );

        BookData bookData = new BookData("취미/실용/스포츠", "하루만에 5키로 빼기", "다이어트퀸");
        Book testingBook1 = Book.rebuild(
                new BookId("tbid-111"),
                bookData.title,
                bookData.author,
                generateDescription(bookData.title),
                BookStatus.AVAILABLE,
                new Isbn("ISBN-99999"));
        bookRepository.save(testingBook1);  // Rent 테스트 가능 도서
        bookCategoryRepository.save(BookCategory.create(testingBook1.getId(), new CategoryId("cid-1")));
        bookCategoryRepository.save(BookCategory.create(testingBook1.getId(), new CategoryId("cid-2")));

        BookData bookData2 = new BookData("육아", "육아왕으로 가는 길", "육아왕");
        Book testingBook2 = Book.rebuild(
                new BookId("tbid-222"),
                bookData2.title,
                bookData2.author,
                generateDescription(bookData2.title),
                BookStatus.AVAILABLE,
                new Isbn("ISBN-99999"));
        bookRepository.save(testingBook2);  // Book 상태변경 테스트 가능 도서
        createBookCategory(categories, bookData2, testingBook2);

        for (BookData data : bookDataList) { // 도서 생성
            Book book = Book.create(
                    data.title,
                    data.author,
                    generateDescription(data.title),  // 임의의 설명 생성
                    new Isbn(generateRandomIsbn())    // 임의의 ISBN 생성
            );
            books.add(bookRepository.save(book));

            createBookCategory(categories, data, book); // Book-Category 연관관계 생성
        }
        return books;
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();

        User adminUser = User.create(
                "관리자",
                "010-1111-2222",
                "admin@admin.com",
                passwordEncoder.encode("admin"),
                UserRole.ADMIN
        );
        users.add(userRepository.save(adminUser));

        User testUser = User.of(
                new UserId("tuid-999"),
                "나빌림",
                "010-0000-0000",
                "user@user.com",
                passwordEncoder.encode("user"),
                UserRole.USER
        );
        users.add(userRepository.save(testUser));
        return users;
    }

    private void createRentals(List<Book> books, List<User> users) {
        LocalDateTime now = LocalDateTime.now();
        List<RentalData> rentalDataList = List.of(
                // 현재 대여 중인 도서들 (반납일 X)
                new RentalData(1, 1, now.minusDays(5), null),
                new RentalData(1, 1, now.minusDays(3), null),
                new RentalData(2, 1, now.minusDays(2), null),
                new RentalData(3, 1, now.minusDays(1), null),

                // 반납완료 도서
                new RentalData(4, 1, now.minusDays(5), now.minusDays(3)),
                new RentalData(5, 1, now.minusDays(8), now.minusDays(2)),
                new RentalData(6, 1, now.minusDays(9), now.minusDays(5)),
                new RentalData(7, 1, now.minusDays(8), now.minusDays(3)),
                new RentalData(8, 1, now.minusDays(7), now.minusDays(2)),
                new RentalData(9, 1, now.minusDays(5), now.minusDays(1))
        );

        for (RentalData data : rentalDataList) {
            Book book = books.get(data.bookId);
            User user = users.get(data.userId);

            Rental rental = Rental.create(
                    book.getId(),
                    user.getId(),
                    data.rentalDate,
                    data.rentalDate.plusDays(10)  // 예상 반납일은 대여일로부터 10일 후
            );

            if (data.returnDate != null) {
                rental.returnCompleted();  // 반납 처리
            }

            rentalRepository.saveNew(rental);
        }
    }

    private void createBookCategory(Map<String, CategoryId> categories, BookData data, Book book) {
        CategoryId categoryId = categories.get(data.category);
        if (categoryId != null) {
            BookCategory bookCategory = BookCategory.create(book.getId(), categoryId);
            bookCategoryRepository.save(bookCategory);
        }
    }

    private String generateDescription(String title) {
        return String.format("'%s' 설명입니다.", title);
    }

    private String generateRandomIsbn() {
        return String.format("ISBN-%08d", new Random().nextInt(1000000000));
    }

    @Data
    @AllArgsConstructor
    private static class BookData {
        private String category;
        private String title;
        private String author;
    }

    @Data
    @AllArgsConstructor
    private static class RentalData {
        private int bookId;
        private int userId;
        private LocalDateTime rentalDate;
        private LocalDateTime returnDate;
    }
}