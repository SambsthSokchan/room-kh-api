package com.roomkh.controller;

import com.roomkh.dto.request.PaymentMarkPaidRequestDTO;
import com.roomkh.dto.request.PaymentRequestDTO;
import com.roomkh.dto.response.ApiResponseDTO;
import com.roomkh.dto.response.PaymentResponseDTO;
import com.roomkh.security.CustomUserDetails;
import com.roomkh.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponseDTO<PaymentResponseDTO>> createMonthlyBill(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PaymentRequestDTO request
    ) {
        PaymentResponseDTO created = paymentService.createMonthlyBill(
                userDetails.getUser().getUserId(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Monthly bill created", created));
    }

    @PatchMapping("/{paymentId}/mark-paid")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponseDTO<PaymentResponseDTO>> markAsPaid(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PaymentMarkPaidRequestDTO request
    ) {
        PaymentResponseDTO updated = paymentService.markAsPaid(
                paymentId,
                userDetails.getUser().getUserId(),
                request
        );
        return ResponseEntity.ok(ApiResponseDTO.ok("Payment marked as PAID", updated));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<ApiResponseDTO<List<PaymentResponseDTO>>> getMyPayments(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Your payment history loaded",
                paymentService.getMyPayments(userDetails.getUser().getUserId())
        ));
    }

    @GetMapping("/landlord")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponseDTO<List<PaymentResponseDTO>>> getLandlordPayments(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Landlord payments loaded",
                paymentService.getLandlordPayments(userDetails.getUser().getUserId())
        ));
    }

    @GetMapping("/contract/{contractId}")
    public ResponseEntity<ApiResponseDTO<List<PaymentResponseDTO>>> getContractPayments(
            @PathVariable Long contractId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Contract payments loaded",
                paymentService.getContractPayments(contractId, userDetails.getUser().getUserId())
        ));
    }
}
