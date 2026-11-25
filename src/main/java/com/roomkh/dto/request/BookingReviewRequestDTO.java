package com.roomkh.dto.request;

import com.roomkh.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingReviewRequestDTO {

    @NotNull(message = "Decision is required (APPROVED or REJECTED)")
    private BookingStatus decision;

    private String bookingNote;
}
