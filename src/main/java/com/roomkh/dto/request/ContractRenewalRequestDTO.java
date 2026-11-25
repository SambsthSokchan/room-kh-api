package com.roomkh.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractRenewalRequestDTO {

    @NotNull(message = "New end date is required")
    private LocalDate newEndDate;

    @DecimalMin(value = "0.01", message = "Monthly rent must be greater than 0")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal newMonthlyRent;

    private String updatedTerms;
}
