package com.roomkh.controller;

import com.roomkh.dto.request.ReportReviewRequestDTO;
import com.roomkh.dto.request.SupportRequestDTO;
import com.roomkh.dto.response.ApiResponseDTO;
import com.roomkh.dto.response.SupportRequestResponseDTO;
import com.roomkh.security.CustomUserDetails;
import com.roomkh.service.SupportRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support-requests")
@RequiredArgsConstructor
public class SupportRequestController {

    private final SupportRequestService supportRequestService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<SupportRequestResponseDTO>> submitSupportRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SupportRequestDTO request
    ) {
        SupportRequestResponseDTO created = supportRequestService.submitSupportRequest(
                userDetails.getUser().getUserId(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Support request submitted", created));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<List<SupportRequestResponseDTO>>> getMySupportRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Your support requests loaded",
                supportRequestService.getMySupportRequests(userDetails.getUser().getUserId())
        ));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<SupportRequestResponseDTO>>> getPendingSupportRequests() {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Pending support requests loaded",
                supportRequestService.getPendingSupportRequests()
        ));
    }

    @PatchMapping("/{supportId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<SupportRequestResponseDTO>> reviewSupportRequest(
            @PathVariable Long supportId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReportReviewRequestDTO review
    ) {
        SupportRequestResponseDTO updated = supportRequestService.reviewSupportRequest(
                supportId,
                userDetails.getUser().getUserId(),
                review
        );
        return ResponseEntity.ok(ApiResponseDTO.ok("Support request reviewed", updated));
    }
}
