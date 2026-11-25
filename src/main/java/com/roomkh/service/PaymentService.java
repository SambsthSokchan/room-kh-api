package com.roomkh.service;

import com.roomkh.dto.request.PaymentMarkPaidRequestDTO;
import com.roomkh.dto.request.PaymentRequestDTO;
import com.roomkh.dto.response.PaymentResponseDTO;

import java.util.List;

public interface PaymentService {
    PaymentResponseDTO createMonthlyBill(Long landlordUserId, PaymentRequestDTO request);
    PaymentResponseDTO markAsPaid(Long paymentId, Long landlordUserId, PaymentMarkPaidRequestDTO request);
    List<PaymentResponseDTO> getMyPayments(Long renterUserId);
    List<PaymentResponseDTO> getLandlordPayments(Long landlordUserId);
    List<PaymentResponseDTO> getContractPayments(Long contractId, Long requesterUserId);
}
