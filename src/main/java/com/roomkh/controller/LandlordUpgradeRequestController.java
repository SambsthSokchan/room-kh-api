package com.roomkh.controller;

import com.roomkh.dto.request.LandlordReviewRequestDTO;
import com.roomkh.dto.request.LandlordUpgradeRequestDTO;
import com.roomkh.dto.response.ApiResponseDTO;
import com.roomkh.dto.response.LandlordUpgradeRequestResponseDTO;
import com.roomkh.security.CustomUserDetails;
import com.roomkh.service.LandlordUpgradeRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/landlord-upgrade-requests")
@RequiredArgsConstructor
public class LandlordUpgradeRequestController {

    private final LandlordUpgradeRequestService upgradeRequestService;

    @PostMapping
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<ApiResponseDTO<LandlordUpgradeRequestResponseDTO>> submitRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody LandlordUpgradeRequestDTO request
    ) {
        LandlordUpgradeRequestResponseDTO created =
                upgradeRequestService.submitRequest(userDetails.getUser().getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Landlord upgrade request submitted", created));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<List<LandlordUpgradeRequestResponseDTO>>> getMyRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<LandlordUpgradeRequestResponseDTO> requests =
                upgradeRequestService.getMyRequests(userDetails.getUser().getUserId());
        return ResponseEntity.ok(ApiResponseDTO.ok("Upgrade requests loaded", requests));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<LandlordUpgradeRequestResponseDTO>>> getPendingRequests() {
        List<LandlordUpgradeRequestResponseDTO> requests = upgradeRequestService.getPendingRequests();
        return ResponseEntity.ok(ApiResponseDTO.ok("Pending upgrade requests loaded", requests));
    }

    @PatchMapping("/{requestId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<LandlordUpgradeRequestResponseDTO>> reviewRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody LandlordReviewRequestDTO review
    ) {
        LandlordUpgradeRequestResponseDTO updated = upgradeRequestService.reviewRequest(
                requestId,
                userDetails.getUser().getUserId(),
                review
        );
        return ResponseEntity.ok(ApiResponseDTO.ok("Upgrade request reviewed", updated));
    }
}
