package com.roomkh.controller;

import com.roomkh.dto.response.ApiResponseDTO;
import com.roomkh.dto.response.NotificationResponseDTO;
import com.roomkh.security.CustomUserDetails;
import com.roomkh.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<List<NotificationResponseDTO>>> getMyNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Notifications loaded",
                notificationService.getMyNotifications(userDetails.getUser().getUserId())
        ));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponseDTO<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        long count = notificationService.getUnreadCount(userDetails.getUser().getUserId());
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Unread count loaded",
                Map.of("unreadCount", count)
        ));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponseDTO<NotificationResponseDTO>> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        NotificationResponseDTO updated = notificationService.markAsRead(
                notificationId,
                userDetails.getUser().getUserId()
        );
        return ResponseEntity.ok(ApiResponseDTO.ok("Notification marked as read", updated));
    }
}
