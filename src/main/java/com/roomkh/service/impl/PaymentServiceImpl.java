package com.roomkh.service.impl;

import com.roomkh.dto.request.PaymentMarkPaidRequestDTO;
import com.roomkh.dto.request.PaymentRequestDTO;
import com.roomkh.dto.response.PaymentResponseDTO;
import com.roomkh.entity.LandlordProfile;
import com.roomkh.entity.Payment;
import com.roomkh.entity.RentalContract;
import com.roomkh.enums.ContractStatus;
import com.roomkh.enums.PaymentStatus;
import com.roomkh.exception.BadRequestException;
import com.roomkh.exception.ConflictException;
import com.roomkh.exception.ForbiddenException;
import com.roomkh.exception.ResourceNotFoundException;
import com.roomkh.mapper.PaymentMapper;
import com.roomkh.repository.LandlordProfileRepository;
import com.roomkh.repository.PaymentRepository;
import com.roomkh.repository.RentalContractRepository;
import com.roomkh.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RentalContractRepository contractRepository;
    private final LandlordProfileRepository landlordProfileRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponseDTO createMonthlyBill(Long landlordUserId, PaymentRequestDTO dto) {
        RentalContract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Rental contract not found"));

        if (!contract.getLandlord().getUser().getUserId().equals(landlordUserId)) {
            throw new ForbiddenException("You do not own this rental contract");
        }

        if (contract.getContractStatus() != ContractStatus.ACTIVE) {
            throw new ConflictException("Monthly bills can only be issued for ACTIVE contracts");
        }

        // Normalize to the 1st of the month to satisfy PostgreSQL CHECK (EXTRACT(DAY FROM payment_month) = 1)
        LocalDate billingMonth = dto.getPaymentMonth().withDayOfMonth(1);

        if (paymentRepository.existsByContract_ContractIdAndPaymentMonth(contract.getContractId(), billingMonth)) {
            throw new ConflictException("A bill already exists for contract "
                    + contract.getContractId() + " in " + billingMonth.getMonth() + " " + billingMonth.getYear());
        }

        BigDecimal baseRent = dto.getBaseRentAmount() != null ? dto.getBaseRentAmount() : contract.getMonthlyRent();
        BigDecimal water = dto.getWaterFee() != null ? dto.getWaterFee() : BigDecimal.ZERO;
        BigDecimal electricity = dto.getElectricityFee() != null ? dto.getElectricityFee() : BigDecimal.ZERO;
        BigDecimal other = dto.getOtherFees() != null ? dto.getOtherFees() : BigDecimal.ZERO;
        BigDecimal total = baseRent.add(water).add(electricity).add(other);

        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Total payment amount must be greater than 0");
        }

        Payment payment = Payment.builder()
                .contract(contract)
                .user(contract.getUser())
                .landlord(contract.getLandlord())
                .paymentMonth(billingMonth)
                .baseRentAmount(baseRent)
                .waterFee(water)
                .electricityFee(electricity)
                .otherFees(other)
                .amount(total)
                .paymentStatus(PaymentStatus.UNPAID)
                .paymentNote(dto.getPaymentNote())
                .build();

        return paymentMapper.toResponse(paymentRepository.saveAndFlush(payment));
    }

    @Override
    @Transactional
    public PaymentResponseDTO markAsPaid(Long paymentId, Long landlordUserId, PaymentMarkPaidRequestDTO dto) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found"));

        if (!payment.getLandlord().getUser().getUserId().equals(landlordUserId)) {
            throw new ForbiddenException("You do not own this payment record");
        }

        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            throw new ConflictException("This payment is already marked as PAID");
        }

        if (payment.getPaymentStatus() == PaymentStatus.CANCELLED) {
            throw new ConflictException("Cancelled payments cannot be marked as PAID");
        }

        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setPaymentDate(dto.getPaymentDate() != null ? dto.getPaymentDate() : LocalDate.now());

        if (dto.getReceiptUrl() != null && !dto.getReceiptUrl().isBlank()) {
            payment.setReceiptUrl(dto.getReceiptUrl().trim());
        }
        if (dto.getPaymentNote() != null && !dto.getPaymentNote().isBlank()) {
            payment.setPaymentNote(dto.getPaymentNote().trim());
        }

        return paymentMapper.toResponse(paymentRepository.saveAndFlush(payment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getMyPayments(Long renterUserId) {
        return paymentRepository.findByUser_UserIdOrderByPaymentMonthDesc(renterUserId)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getLandlordPayments(Long landlordUserId) {
        LandlordProfile landlord = landlordProfileRepository.findByUser_UserId(landlordUserId)
                .orElseThrow(() -> new ForbiddenException("Landlord profile not found"));

        return paymentRepository.findByLandlord_LandlordProfileIdOrderByPaymentMonthDesc(
                        landlord.getLandlordProfileId()
                )
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getContractPayments(Long contractId, Long requesterUserId) {
        RentalContract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental contract not found"));

        boolean isTenant = contract.getUser().getUserId().equals(requesterUserId);
        boolean isLandlord = contract.getLandlord().getUser().getUserId().equals(requesterUserId);

        if (!isTenant && !isLandlord) {
            throw new ForbiddenException("You do not have access to this contract's payments");
        }

        return paymentRepository.findByContract_ContractIdOrderByPaymentMonthDesc(contractId)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }
}
