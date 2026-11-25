package com.roomkh.controller;

import com.roomkh.dto.request.BroadcastMessageRequestDTO;
import com.roomkh.dto.request.RoomModerationRequestDTO;
import com.roomkh.dto.request.UserManagementRequestDTO;
import com.roomkh.dto.response.*;
import com.roomkh.security.CustomUserDetails;
import com.roomkh.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/rooms/pending")
    public ResponseEntity<ApiResponseDTO<List<RoomResponseDTO>>> getPendingRooms() {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Pending rooms loaded",
                adminService.getPendingRooms()
        ));
    }

    @PostMapping("/rooms/{roomId}/moderate")
    public ResponseEntity<ApiResponseDTO<RoomModerationResponseDTO>> moderateRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RoomModerationRequestDTO request
    ) {
        RoomModerationResponseDTO result = adminService.moderateRoom(
                roomId,
                userDetails.getUser().getUserId(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Room moderation recorded", result));
    }

    @PostMapping("/users/{targetUserId}/actions")
    public ResponseEntity<ApiResponseDTO<UserManagementResponseDTO>> manageUser(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserManagementRequestDTO request
    ) {
        UserManagementResponseDTO result = adminService.manageUser(
                targetUserId,
                userDetails.getUser().getUserId(),
                request
        );
        return ResponseEntity.ok(ApiResponseDTO.ok("User action applied", result));
    }

    @GetMapping("/users/{targetUserId}/audit")
    public ResponseEntity<ApiResponseDTO<List<UserManagementResponseDTO>>> getUserAuditHistory(
            @PathVariable Long targetUserId
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "User audit history loaded",
                adminService.getUserAuditHistory(targetUserId)
        ));
    }

    @PostMapping("/broadcasts")
    public ResponseEntity<ApiResponseDTO<BroadcastMessageResponseDTO>> sendBroadcast(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BroadcastMessageRequestDTO request
    ) {
        BroadcastMessageResponseDTO result = adminService.sendBroadcast(
                userDetails.getUser().getUserId(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Broadcast sent", result));
    }

    @GetMapping("/broadcasts")
    public ResponseEntity<ApiResponseDTO<List<BroadcastMessageResponseDTO>>> getBroadcastHistory() {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Broadcast history loaded",
                adminService.getBroadcastHistory()
        ));
    }
}
