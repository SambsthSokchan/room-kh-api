package com.roomkh.controller;

import com.roomkh.dto.request.BookingRequestDTO;
import com.roomkh.dto.request.BookingReviewRequestDTO;
import com.roomkh.dto.response.ApiResponseDTO;
import com.roomkh.dto.response.BookingResponseDTO;
import com.roomkh.security.CustomUserDetails;
import com.roomkh.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<ApiResponseDTO<BookingResponseDTO>> createBooking(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BookingRequestDTO request
    ) {
        BookingResponseDTO created = bookingService.createBooking(
                userDetails.getUser().getUserId(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Booking request submitted", created));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<ApiResponseDTO<List<BookingResponseDTO>>> getMyBookings(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Your bookings loaded",
                bookingService.getMyBookings(userDetails.getUser().getUserId())
        ));
    }

    @PatchMapping("/{bookingId}/cancel")
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<ApiResponseDTO<BookingResponseDTO>> cancelBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        BookingResponseDTO cancelled = bookingService.cancelBooking(
                bookingId,
                userDetails.getUser().getUserId()
        );
        return ResponseEntity.ok(ApiResponseDTO.ok("Booking cancelled", cancelled));
    }

    @GetMapping("/landlord")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponseDTO<List<BookingResponseDTO>>> getLandlordBookings(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Incoming bookings loaded",
                bookingService.getLandlordBookings(userDetails.getUser().getUserId())
        ));
    }

    @PatchMapping("/{bookingId}/review")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponseDTO<BookingResponseDTO>> reviewBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BookingReviewRequestDTO review
    ) {
        BookingResponseDTO updated = bookingService.reviewBooking(
                bookingId,
                userDetails.getUser().getUserId(),
                review
        );
        return ResponseEntity.ok(ApiResponseDTO.ok("Booking reviewed", updated));
    }
}
