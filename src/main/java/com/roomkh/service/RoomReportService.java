package com.roomkh.service;

import com.roomkh.dto.request.ReportReviewRequestDTO;
import com.roomkh.dto.request.RoomReportRequestDTO;
import com.roomkh.dto.response.RoomReportResponseDTO;

import java.util.List;

public interface RoomReportService {
    RoomReportResponseDTO submitReport(Long userId, RoomReportRequestDTO request);
    List<RoomReportResponseDTO> getMyReports(Long userId);
    List<RoomReportResponseDTO> getPendingReports();
    RoomReportResponseDTO reviewReport(Long reportId, Long adminUserId, ReportReviewRequestDTO review);
}
