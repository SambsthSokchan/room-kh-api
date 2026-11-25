package com.roomkh.dto.request;

import com.roomkh.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentMarkPaidRequestDTO {

    @NotNull(message = "Payment method is required (CASH, BANK_TRANSFER, ABA_PAY, WING, or OTHER)")
    private PaymentMethod paymentMethod;

    // Optional: defaults to today's date if omitted
    private LocalDate paymentDate;

    @Size(max = 255)
    private String receiptUrl;

    private String paymentNote;
}
