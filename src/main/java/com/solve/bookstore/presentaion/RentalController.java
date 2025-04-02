package com.solve.bookstore.presentaion;

import com.solve.bookstore.application.RentalService;
import com.solve.bookstore.application.dto.BookStatusChangeRequest;
import com.solve.bookstore.application.dto.RentSuccessResponse;
import com.solve.bookstore.application.dto.ReturnBookResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PostMapping("{bookId}/{rentalUserId}")
    public ResponseEntity<RentSuccessResponse> rent(
            @PathVariable String bookId,
            @PathVariable String rentalUserId
    ){
        RentSuccessResponse response = rentalService.rent(bookId, rentalUserId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("{bookId}/{rentalUserId}")
    public ResponseEntity<ReturnBookResponse> returnBook(
            @PathVariable String bookId,
            @RequestBody @Valid BookStatusChangeRequest request
    ){
        ReturnBookResponse response = rentalService.returnBook(bookId, request);
        return ResponseEntity.ok(response);
    }
}
