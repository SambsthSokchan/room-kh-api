package com.roomkh.repository;

import com.roomkh.entity.SupportRequest;
import com.roomkh.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {
    List<SupportRequest> findBySupportStatusOrderByCreatedAtAsc(ReportStatus supportStatus);
    List<SupportRequest> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    long countBySupportStatus(ReportStatus supportStatus);
    long countByUser_UserId(Long userId);
}
