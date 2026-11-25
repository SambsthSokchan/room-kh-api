package com.roomkh.controller;

import com.roomkh.dto.response.ApiResponseDTO;
import com.roomkh.dto.response.DashboardResponseDTO;
import com.roomkh.dto.response.LandlordDashboardResponseDTO;
import com.roomkh.dto.response.UserDashboardResponseDTO;
import com.roomkh.security.CustomUserDetails;
import com.roomkh.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<DashboardResponseDTO>> getAdminDashboard() {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Admin dashboard metrics loaded",
                dashboardService.getAdminDashboard()
        ));
    }

    @GetMapping("/landlord")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponseDTO<LandlordDashboardResponseDTO>> getLandlordDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Landlord dashboard metrics loaded",
                dashboardService.getLandlordDashboard(userDetails.getUser().getUserId())
        ));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponseDTO<UserDashboardResponseDTO>> getUserDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "User dashboard metrics loaded",
                dashboardService.getUserDashboard(userDetails.getUser().getUserId())
        ));
    }
}
