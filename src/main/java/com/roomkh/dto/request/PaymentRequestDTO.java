package com.roomkh.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentRequestDTO {

    @NotNull(message = "Contract ID is required")
    private Long contractId;

    // Any date in the billing month; normalized to the 1st of the month automatically
    @NotNull(message = "Payment month is required (e.g. 2026-12-01)")
    private LocalDate paymentMonth;

    // Optional: defaults to the contract's monthlyRent if omitted
    @DecimalMin(value = "0.00", message = "Base rent cannot be negative")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal baseRentAmount;

    @DecimalMin(value = "0.00", message = "Water fee cannot be negative")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal waterFee = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "Electricity fee cannot be negative")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal electricityFee = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "Other fees cannot be negative")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal otherFees = BigDecimal.ZERO;

    private String paymentNote;
}
