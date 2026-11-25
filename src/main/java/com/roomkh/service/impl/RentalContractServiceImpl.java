package com.roomkh.service.impl;

import com.roomkh.dto.request.ContractEndRequestDTO;
import com.roomkh.dto.request.ContractRenewalRequestDTO;
import com.roomkh.dto.request.RentalContractRequestDTO;
import com.roomkh.dto.response.RentalContractResponseDTO;
import com.roomkh.entity.Booking;
import com.roomkh.entity.LandlordProfile;
import com.roomkh.entity.RentalContract;
import com.roomkh.entity.Room;
import com.roomkh.enums.BookingStatus;
import com.roomkh.enums.ContractStatus;
import com.roomkh.exception.BadRequestException;
import com.roomkh.exception.ConflictException;
import com.roomkh.exception.ForbiddenException;
import com.roomkh.exception.ResourceNotFoundException;
import com.roomkh.mapper.RentalContractMapper;
import com.roomkh.repository.BookingRepository;
import com.roomkh.repository.LandlordProfileRepository;
import com.roomkh.repository.RentalContractRepository;
import com.roomkh.service.RentalContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalContractServiceImpl implements RentalContractService {

    private final RentalContractRepository contractRepository;
    private final BookingRepository bookingRepository;
    private final LandlordProfileRepository landlordProfileRepository;
    private final RentalContractMapper contractMapper;

    @Override
    @Transactional
    public RentalContractResponseDTO createContract(Long landlordUserId, RentalContractRequestDTO dto) {
        if (!dto.getEndDate().isAfter(dto.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getLandlord().getUser().getUserId().equals(landlordUserId)) {
            throw new ForbiddenException("You do not own the room for this booking");
        }

        if (booking.getBookingStatus() != BookingStatus.PENDING
                && booking.getBookingStatus() != BookingStatus.APPROVED) {
            throw new ConflictException(
                    "Cannot create a contract from a " + booking.getBookingStatus().name() + " booking"
            );
        }

        if (contractRepository.existsByBooking_BookingId(booking.getBookingId())) {
            throw new ConflictException("A rental contract already exists for this booking");
        }

        Room room = booking.getRoom();
        if (contractRepository.existsByRoom_RoomIdAndContractStatus(room.getRoomId(), ContractStatus.ACTIVE)) {
            throw new ConflictException("This room already has an ACTIVE rental contract");
        }

        BigDecimal rent = dto.getMonthlyRent() != null ? dto.getMonthlyRent() : room.getMonthlyRent();
        BigDecimal deposit = dto.getDepositAmount() != null ? dto.getDepositAmount() : BigDecimal.ZERO;
        Short dueDay = dto.getPaymentDueDay() != null ? dto.getPaymentDueDay() : 5;

        RentalContract contract = RentalContract.builder()
                .booking(booking)
                .user(booking.getUser())
                .room(room)
                .landlord(booking.getLandlord())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .monthlyRent(rent)
                .depositAmount(deposit)
                .paymentDueDay(dueDay)
                .contractStatus(ContractStatus.ACTIVE)
                .contractTerms(dto.getContractTerms())
                .contractPdfUrl(dto.getContractPdfUrl())
                .build();

        // Flush immediately so PostgreSQL trigger trg_contract_created fires inside this transaction
        RentalContract saved = contractRepository.saveAndFlush(contract);
        return contractMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContractResponseDTO> getMyContracts(Long renterUserId) {
        return contractRepository.findByUser_UserIdOrderByCreatedAtDesc(renterUserId)
                .stream()
                .map(contractMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalContractResponseDTO> getLandlordContracts(Long landlordUserId) {
        LandlordProfile landlord = landlordProfileRepository.findByUser_UserId(landlordUserId)
                .orElseThrow(() -> new ForbiddenException("Landlord profile not found"));

        return contractRepository.findByLandlord_LandlordProfileIdOrderByCreatedAtDesc(
                        landlord.getLandlordProfileId()
                )
                .stream()
                .map(contractMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public RentalContractResponseDTO closeContract(
            Long contractId,
            Long landlordUserId,
            ContractEndRequestDTO dto
    ) {
        if (dto.getStatus() != ContractStatus.ENDED && dto.getStatus() != ContractStatus.CANCELLED) {
            throw new BadRequestException("Closing status must be ENDED or CANCELLED");
        }

        RentalContract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental contract not found"));

        if (!contract.getLandlord().getUser().getUserId().equals(landlordUserId)) {
            throw new ForbiddenException("You do not own this rental contract");
        }

        if (contract.getContractStatus() != ContractStatus.ACTIVE) {
            throw new ConflictException("Only ACTIVE contracts can be closed");
        }

        contract.setContractStatus(dto.getStatus());

        // Flush immediately so PostgreSQL trigger trg_contract_closed returns room_status to VACANT
        RentalContract updated = contractRepository.saveAndFlush(contract);
        return contractMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public RentalContractResponseDTO renewContract(
            Long contractId,
            Long landlordUserId,
            ContractRenewalRequestDTO dto
    ) {
        RentalContract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental contract not found"));

        if (!contract.getLandlord().getUser().getUserId().equals(landlordUserId)) {
            throw new ForbiddenException("You do not own this rental contract");
        }

        if (contract.getContractStatus() != ContractStatus.ACTIVE) {
            throw new ConflictException("Only ACTIVE contracts can be renewed");
        }

        if (!dto.getNewEndDate().isAfter(contract.getEndDate())) {
            throw new BadRequestException("New end date must be after the current end date");
        }

        contract.setEndDate(dto.getNewEndDate());
        if (dto.getNewMonthlyRent() != null) {
            contract.setMonthlyRent(dto.getNewMonthlyRent());
        }
        if (dto.getUpdatedTerms() != null && !dto.getUpdatedTerms().isBlank()) {
            contract.setContractTerms(dto.getUpdatedTerms().trim());
        }

        // Keep status ACTIVE so uq_active_contract_per_room and trg_prevent_user_soft_delete remain enforced
        return contractMapper.toResponse(contractRepository.saveAndFlush(contract));
    }
}
