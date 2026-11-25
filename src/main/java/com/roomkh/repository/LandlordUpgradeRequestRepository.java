package com.roomkh.repository;

import com.roomkh.entity.LandlordUpgradeRequest;
import com.roomkh.enums.LandlordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LandlordUpgradeRequestRepository extends JpaRepository<LandlordUpgradeRequest, Long> {
    List<LandlordUpgradeRequest> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    List<LandlordUpgradeRequest> findByRequestStatusOrderByCreatedAtAsc(LandlordStatus requestStatus);
    boolean existsByUser_UserIdAndRequestStatus(Long userId, LandlordStatus requestStatus);
    long countByRequestStatus(LandlordStatus requestStatus);
}
