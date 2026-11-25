package com.roomkh.repository;

import com.roomkh.entity.DisputeReport;
import com.roomkh.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisputeReportRepository extends JpaRepository<DisputeReport, Long> {
    List<DisputeReport> findByDisputeStatusOrderByCreatedAtAsc(ReportStatus disputeStatus);
    List<DisputeReport> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    long countByDisputeStatus(ReportStatus disputeStatus);
}
