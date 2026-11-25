package com.roomkh.repository;

import com.roomkh.entity.RentalContract;
import com.roomkh.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalContractRepository extends JpaRepository<RentalContract, Long> {
    List<RentalContract> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    List<RentalContract> findByLandlord_LandlordProfileIdOrderByCreatedAtDesc(Long landlordProfileId);
    boolean existsByBooking_BookingId(Long bookingId);
    boolean existsByRoom_RoomIdAndContractStatus(Long roomId, ContractStatus contractStatus);
    boolean existsByUser_UserIdAndContractStatus(Long userId, ContractStatus contractStatus);
    long countByContractStatus(ContractStatus contractStatus);
    long countByLandlord_LandlordProfileIdAndContractStatus(Long landlordProfileId, ContractStatus contractStatus);
    long countByUser_UserIdAndContractStatus(Long userId, ContractStatus contractStatus);
}
