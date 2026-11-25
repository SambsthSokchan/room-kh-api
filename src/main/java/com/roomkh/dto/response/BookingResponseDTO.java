package com.roomkh.dto.response;

import com.roomkh.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
public class BookingResponseDTO {
    private Long bookingId;
    private Long userId;
    private String renterName;
    private Long roomId;
    private String roomTitle;
    private Long landlordId;
    private String businessName;
    private LocalDate bookingDate;
    private LocalDate moveInDate;
    private LocalDate moveOutDate;
    private Short tenantHeadcount;
    private String contactNumber;
    private BookingStatus bookingStatus;
    private String bookingNote;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
