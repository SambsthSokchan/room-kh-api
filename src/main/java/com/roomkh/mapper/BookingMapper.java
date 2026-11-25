package com.roomkh.mapper;

import com.roomkh.dto.response.BookingResponseDTO;
import com.roomkh.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponseDTO toResponse(Booking booking) {
        return BookingResponseDTO.builder()
                .bookingId(booking.getBookingId())
                .userId(booking.getUser().getUserId())
                .renterName(booking.getUser().getFullName())
                .roomId(booking.getRoom().getRoomId())
                .roomTitle(booking.getRoom().getRoomTitle())
                .landlordId(booking.getLandlord().getLandlordProfileId())
                .businessName(booking.getLandlord().getBusinessName())
                .bookingDate(booking.getBookingDate())
                .moveInDate(booking.getMoveInDate())
                .moveOutDate(booking.getMoveOutDate())
                .tenantHeadcount(booking.getTenantHeadcount())
                .contactNumber(booking.getContactNumber())
                .bookingStatus(booking.getBookingStatus())
                .bookingNote(booking.getBookingNote())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
