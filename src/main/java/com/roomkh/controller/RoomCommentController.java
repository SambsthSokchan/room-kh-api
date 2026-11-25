package com.roomkh.controller;

import com.roomkh.dto.request.RoomCommentRequestDTO;
import com.roomkh.dto.response.ApiResponseDTO;
import com.roomkh.dto.response.RoomCommentResponseDTO;
import com.roomkh.enums.UserRole;
import com.roomkh.security.CustomUserDetails;
import com.roomkh.service.RoomCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-comments")
@RequiredArgsConstructor
public class RoomCommentController {

    private final RoomCommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<RoomCommentResponseDTO>> addComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RoomCommentRequestDTO request
    ) {
        RoomCommentResponseDTO created = commentService.addComment(
                userDetails.getUser().getUserId(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Comment posted", created));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiResponseDTO<List<RoomCommentResponseDTO>>> getCommentsByRoom(
            @PathVariable Long roomId
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Room comments loaded",
                commentService.getActiveCommentsByRoom(roomId)
        ));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponseDTO<RoomCommentResponseDTO>> removeComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        boolean isAdmin = userDetails.getUser().getRole() == UserRole.ADMIN;
        RoomCommentResponseDTO updated = commentService.removeComment(
                commentId,
                userDetails.getUser().getUserId(),
                isAdmin
        );
        return ResponseEntity.ok(ApiResponseDTO.ok("Comment removed", updated));
    }
}
