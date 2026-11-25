package com.roomkh.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDTO {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    private LocalDate moveInDate;

    private LocalDate moveOutDate;

    @NotNull(message = "Tenant headcount is required")
    @Min(value = 1, message = "Tenant headcount must be at least 1")
    private Short tenantHeadcount = 1;

    @Size(max = 20)
    private String contactNumber;

    private String bookingNote;
}
