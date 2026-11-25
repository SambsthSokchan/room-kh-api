package com.roomkh.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RentalContractRequestDTO {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    // Optional: if omitted, defaults to the room's listed monthlyRent
    @DecimalMin(value = "0.01", message = "Monthly rent must be greater than 0")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal monthlyRent;

    @DecimalMin(value = "0.00", message = "Deposit amount cannot be negative")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal depositAmount = BigDecimal.ZERO;

    @Min(value = 1, message = "Payment due day must be between 1 and 28")
    @Max(value = 28, message = "Payment due day must be between 1 and 28")
    private Short paymentDueDay = 5;

    private String contractTerms;

    @Size(max = 255)
    private String contractPdfUrl;
}
