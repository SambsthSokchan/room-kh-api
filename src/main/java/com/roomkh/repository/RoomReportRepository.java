package com.roomkh.repository;

import com.roomkh.entity.RoomReport;
import com.roomkh.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomReportRepository extends JpaRepository<RoomReport, Long> {
    List<RoomReport> findByReportStatusOrderByCreatedAtAsc(ReportStatus reportStatus);
    List<RoomReport> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    long countByReportStatus(ReportStatus reportStatus);
}
