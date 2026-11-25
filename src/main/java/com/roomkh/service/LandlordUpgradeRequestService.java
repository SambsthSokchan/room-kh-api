package com.roomkh.service;

import com.roomkh.dto.request.LandlordReviewRequestDTO;
import com.roomkh.dto.request.LandlordUpgradeRequestDTO;
import com.roomkh.dto.response.LandlordUpgradeRequestResponseDTO;

import java.util.List;

public interface LandlordUpgradeRequestService {
    LandlordUpgradeRequestResponseDTO submitRequest(Long userId, LandlordUpgradeRequestDTO request);
    List<LandlordUpgradeRequestResponseDTO> getMyRequests(Long userId);
    List<LandlordUpgradeRequestResponseDTO> getPendingRequests();
    LandlordUpgradeRequestResponseDTO reviewRequest(Long requestId, Long adminUserId, LandlordReviewRequestDTO review);
}
