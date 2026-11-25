package com.roomkh.mapper;

import com.roomkh.dto.response.LandlordUpgradeRequestResponseDTO;
import com.roomkh.entity.LandlordUpgradeRequest;
import org.springframework.stereotype.Component;

@Component
public class LandlordUpgradeRequestMapper {

    public LandlordUpgradeRequestResponseDTO toResponse(LandlordUpgradeRequest req) {
        return LandlordUpgradeRequestResponseDTO.builder()
                .requestId(req.getRequestId())
                .userId(req.getUser().getUserId())
                .applicantName(req.getUser().getFullName())
                .applicantEmail(req.getUser().getEmail())
                .businessName(req.getBusinessName())
                .businessType(req.getBusinessType())
                .contactNumber(req.getContactNumber())
                .businessEmail(req.getBusinessEmail())
                .businessAddress(req.getBusinessAddress())
                .district(req.getDistrict())
                .city(req.getCity())
                .idType(req.getIdType())
                .idNumber(req.getIdNumber())
                .idPhotoUrl(req.getIdPhotoUrl())
                .businessPhotoUrl(req.getBusinessPhotoUrl())
                .businessDescription(req.getBusinessDescription())
                .requestStatus(req.getRequestStatus())
                .reviewNote(req.getReviewNote())
                .reviewedByAdminId(req.getReviewedBy() != null ? req.getReviewedBy().getAdminId() : null)
                .createdAt(req.getCreatedAt())
                .updatedAt(req.getUpdatedAt())
                .build();
    }
}
