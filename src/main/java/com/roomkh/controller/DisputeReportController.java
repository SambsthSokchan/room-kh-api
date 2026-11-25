package com.roomkh.controller;

import com.roomkh.dto.request.DisputeReportRequestDTO;
import com.roomkh.dto.request.ReportReviewRequestDTO;
import com.roomkh.dto.response.ApiResponseDTO;
import com.roomkh.dto.response.DisputeReportResponseDTO;
import com.roomkh.security.CustomUserDetails;
import com.roomkh.service.DisputeReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispute-reports")
@RequiredArgsConstructor
public class DisputeReportController {

    private final DisputeReportService disputeReportService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<DisputeReportResponseDTO>> submitDispute(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody DisputeReportRequestDTO request
    ) {
        DisputeReportResponseDTO created = disputeReportService.submitDispute(
                userDetails.getUser().getUserId(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Dispute report submitted", created));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<List<DisputeReportResponseDTO>>> getMyDisputes(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Your disputes loaded",
                disputeReportService.getMyDisputes(userDetails.getUser().getUserId())
        ));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<DisputeReportResponseDTO>>> getPendingDisputes() {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Pending disputes loaded",
                disputeReportService.getPendingDisputes()
        ));
    }

    @PatchMapping("/{disputeId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<DisputeReportResponseDTO>> reviewDispute(
            @PathVariable Long disputeId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReportReviewRequestDTO review
    ) {
        DisputeReportResponseDTO updated = disputeReportService.reviewDispute(
                disputeId,
                userDetails.getUser().getUserId(),
                review
        );
        return ResponseEntity.ok(ApiResponseDTO.ok("Dispute reviewed", updated));
    }
}
