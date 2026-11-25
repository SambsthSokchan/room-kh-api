package com.roomkh.service.impl;

import com.roomkh.dto.request.LandlordReviewRequestDTO;
import com.roomkh.dto.request.LandlordUpgradeRequestDTO;
import com.roomkh.dto.response.LandlordUpgradeRequestResponseDTO;
import com.roomkh.entity.Admin;
import com.roomkh.entity.LandlordProfile;
import com.roomkh.entity.LandlordUpgradeRequest;
import com.roomkh.entity.User;
import com.roomkh.enums.LandlordStatus;
import com.roomkh.enums.UserRole;
import com.roomkh.exception.BadRequestException;
import com.roomkh.exception.ConflictException;
import com.roomkh.exception.ForbiddenException;
import com.roomkh.exception.ResourceNotFoundException;
import com.roomkh.mapper.LandlordUpgradeRequestMapper;
import com.roomkh.repository.AdminRepository;
import com.roomkh.repository.LandlordProfileRepository;
import com.roomkh.repository.LandlordUpgradeRequestRepository;
import com.roomkh.repository.UserRepository;
import com.roomkh.service.LandlordUpgradeRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LandlordUpgradeRequestServiceImpl implements LandlordUpgradeRequestService {

    private final LandlordUpgradeRequestRepository upgradeRequestRepository;
    private final LandlordProfileRepository landlordProfileRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final LandlordUpgradeRequestMapper mapper;

    @Override
    @Transactional
    public LandlordUpgradeRequestResponseDTO submitRequest(Long userId, LandlordUpgradeRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != UserRole.RENTER) {
            throw new ConflictException("Only RENTER accounts can request a landlord upgrade");
        }

        if (upgradeRequestRepository.existsByUser_UserIdAndRequestStatus(userId, LandlordStatus.PENDING)) {
            throw new ConflictException("You already have a pending landlord upgrade request");
        }

        LandlordUpgradeRequest request = LandlordUpgradeRequest.builder()
                .user(user)
                .businessName(dto.getBusinessName().trim())
                .businessType(dto.getBusinessType())
                .contactNumber(dto.getContactNumber().trim())
                .businessEmail(dto.getBusinessEmail())
                .businessAddress(dto.getBusinessAddress().trim())
                .district(dto.getDistrict())
                .city(dto.getCity().trim())
                .idType(dto.getIdType())
                .idNumber(dto.getIdNumber().trim())
                .idPhotoUrl(dto.getIdPhotoUrl().trim())
                .businessPhotoUrl(dto.getBusinessPhotoUrl())
                .businessDescription(dto.getBusinessDescription())
                .requestStatus(LandlordStatus.PENDING)
                .build();

        return mapper.toResponse(upgradeRequestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LandlordUpgradeRequestResponseDTO> getMyRequests(Long userId) {
        return upgradeRequestRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LandlordUpgradeRequestResponseDTO> getPendingRequests() {
        return upgradeRequestRepository.findByRequestStatusOrderByCreatedAtAsc(LandlordStatus.PENDING)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public LandlordUpgradeRequestResponseDTO reviewRequest(
            Long requestId,
            Long adminUserId,
            LandlordReviewRequestDTO review
    ) {
        if (review.getDecision() != LandlordStatus.APPROVED
                && review.getDecision() != LandlordStatus.REJECTED) {
            throw new BadRequestException("Decision must be APPROVED or REJECTED");
        }

        if (review.getDecision() == LandlordStatus.REJECTED
                && (review.getReviewNote() == null || review.getReviewNote().isBlank())) {
            throw new BadRequestException("Review note is required when rejecting a request");
        }

        LandlordUpgradeRequest upgradeRequest = upgradeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Upgrade request not found"));

        if (upgradeRequest.getRequestStatus() != LandlordStatus.PENDING) {
            throw new ConflictException("Upgrade request has already been reviewed");
        }

        Admin admin = adminRepository.findByUser_UserId(adminUserId)
                .orElseThrow(() -> new ForbiddenException("Admin profile not found"));

        upgradeRequest.setRequestStatus(review.getDecision());
        upgradeRequest.setReviewNote(review.getReviewNote());
        upgradeRequest.setReviewedBy(admin);

        if (review.getDecision() == LandlordStatus.APPROVED) {
            User applicant = upgradeRequest.getUser();
            applicant.setRole(UserRole.LANDLORD);
            userRepository.save(applicant);

            LandlordProfile profile = landlordProfileRepository.findByUser_UserId(applicant.getUserId())
                    .orElseGet(() -> LandlordProfile.builder().user(applicant).build());

            profile.setBusinessName(upgradeRequest.getBusinessName());
            profile.setBusinessType(upgradeRequest.getBusinessType());
            profile.setContactNumber(upgradeRequest.getContactNumber());
            profile.setBusinessEmail(upgradeRequest.getBusinessEmail());
            profile.setBusinessAddress(upgradeRequest.getBusinessAddress());
            profile.setDistrict(upgradeRequest.getDistrict());
            profile.setCity(upgradeRequest.getCity());
            profile.setIdType(upgradeRequest.getIdType());
            profile.setIdNumber(upgradeRequest.getIdNumber());
            profile.setIdPhotoUrl(upgradeRequest.getIdPhotoUrl());
            profile.setBusinessPhotoUrl(upgradeRequest.getBusinessPhotoUrl());
            profile.setBusinessDescription(upgradeRequest.getBusinessDescription());
            profile.setLandlordStatus(LandlordStatus.APPROVED);

            landlordProfileRepository.save(profile);
        }

        return mapper.toResponse(upgradeRequestRepository.save(upgradeRequest));
    }
}
