package com.roomkh.controller;

import com.roomkh.dto.request.ReportReviewRequestDTO;
import com.roomkh.dto.request.RoomReportRequestDTO;
import com.roomkh.dto.response.ApiResponseDTO;
import com.roomkh.dto.response.RoomReportResponseDTO;
import com.roomkh.security.CustomUserDetails;
import com.roomkh.service.RoomReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-reports")
@RequiredArgsConstructor
public class RoomReportController {

    private final RoomReportService roomReportService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<RoomReportResponseDTO>> submitReport(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RoomReportRequestDTO request
    ) {
        RoomReportResponseDTO created = roomReportService.submitReport(
                userDetails.getUser().getUserId(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Room report submitted", created));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<List<RoomReportResponseDTO>>> getMyReports(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Your room reports loaded",
                roomReportService.getMyReports(userDetails.getUser().getUserId())
        ));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<RoomReportResponseDTO>>> getPendingReports() {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Pending room reports loaded",
                roomReportService.getPendingReports()
        ));
    }

    @PatchMapping("/{reportId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<RoomReportResponseDTO>> reviewReport(
            @PathVariable Long reportId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReportReviewRequestDTO review
    ) {
        RoomReportResponseDTO updated = roomReportService.reviewReport(
                reportId,
                userDetails.getUser().getUserId(),
                review
        );
        return ResponseEntity.ok(ApiResponseDTO.ok("Room report reviewed", updated));
    }
}
