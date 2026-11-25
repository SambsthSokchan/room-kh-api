package com.roomkh.service;

import com.roomkh.dto.request.BookingRequestDTO;
import com.roomkh.dto.request.BookingReviewRequestDTO;
import com.roomkh.dto.response.BookingResponseDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDTO createBooking(Long renterUserId, BookingRequestDTO request);
    List<BookingResponseDTO> getMyBookings(Long renterUserId);
    BookingResponseDTO cancelBooking(Long bookingId, Long renterUserId);
    List<BookingResponseDTO> getLandlordBookings(Long landlordUserId);
    BookingResponseDTO reviewBooking(Long bookingId, Long landlordUserId, BookingReviewRequestDTO review);
}
