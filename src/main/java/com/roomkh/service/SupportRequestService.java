package com.roomkh.service;

import com.roomkh.dto.request.ReportReviewRequestDTO;
import com.roomkh.dto.request.SupportRequestDTO;
import com.roomkh.dto.response.SupportRequestResponseDTO;

import java.util.List;

public interface SupportRequestService {
    SupportRequestResponseDTO submitSupportRequest(Long userId, SupportRequestDTO request);
    List<SupportRequestResponseDTO> getMySupportRequests(Long userId);
    List<SupportRequestResponseDTO> getPendingSupportRequests();
    SupportRequestResponseDTO reviewSupportRequest(Long supportId, Long adminUserId, ReportReviewRequestDTO review);
}
