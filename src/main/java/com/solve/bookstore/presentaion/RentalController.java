package com.solve.bookstore.presentaion;

import com.solve.bookstore.application.RentalService;
import com.solve.bookstore.application.dto.BookStatusChangeRequest;
import com.solve.bookstore.application.dto.RentSuccessResponse;
import com.solve.bookstore.application.dto.ReturnBookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "도서 대여", description = "도서 대여 및 반납 관련 API")
@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @Operation(summary = "도서 대여", description = "도서를 대여합니다. 대여 가능한 상태의 도서만 대여할 수 있습니다.")
    @PostMapping("{bookId}/{rentalUserId}")
    public ResponseEntity<RentSuccessResponse> rent(
            @Parameter(description = "도서 ID", required = true)
            @PathVariable String bookId,
            @Parameter(description = "대여자 ID", required = true)
            @PathVariable String rentalUserId
    ){
        RentSuccessResponse response = rentalService.rent(bookId, rentalUserId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "도서 반납", description = "도서를 반납합니다. 반납 시 도서 상태를 변경할 수 있습니다.")
    @PatchMapping("{bookId}/{rentalUserId}")
    public ResponseEntity<ReturnBookResponse> returnBook(
            @Parameter(description = "도서 ID", required = true)
            @PathVariable String bookId,
            @RequestBody @Valid BookStatusChangeRequest request
    ){
        ReturnBookResponse response = rentalService.returnBook(bookId, request);
        return ResponseEntity.ok(response);
    }
}
