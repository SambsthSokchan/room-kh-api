package com.roomkh.service;

import com.roomkh.dto.request.DisputeReportRequestDTO;
import com.roomkh.dto.request.ReportReviewRequestDTO;
import com.roomkh.dto.response.DisputeReportResponseDTO;

import java.util.List;

public interface DisputeReportService {
    DisputeReportResponseDTO submitDispute(Long userId, DisputeReportRequestDTO request);
    List<DisputeReportResponseDTO> getMyDisputes(Long userId);
    List<DisputeReportResponseDTO> getPendingDisputes();
    DisputeReportResponseDTO reviewDispute(Long disputeId, Long adminUserId, ReportReviewRequestDTO review);
}
