package com.roomkh.controller;

import com.roomkh.dto.request.RoomRatingRequestDTO;
import com.roomkh.dto.response.ApiResponseDTO;
import com.roomkh.dto.response.RoomRatingResponseDTO;
import com.roomkh.enums.UserRole;
import com.roomkh.security.CustomUserDetails;
import com.roomkh.service.RoomRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-ratings")
@RequiredArgsConstructor
public class RoomRatingController {

    private final RoomRatingService ratingService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<RoomRatingResponseDTO>> rateRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RoomRatingRequestDTO request
    ) {
        RoomRatingResponseDTO saved = ratingService.rateRoom(
                userDetails.getUser().getUserId(),
                request
        );
        return ResponseEntity.ok(ApiResponseDTO.ok("Room rating saved", saved));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiResponseDTO<List<RoomRatingResponseDTO>>> getRatingsByRoom(
            @PathVariable Long roomId
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Room ratings loaded",
                ratingService.getRatingsByRoom(roomId)
        ));
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteRating(
            @PathVariable Long ratingId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        boolean isAdmin = userDetails.getUser().getRole() == UserRole.ADMIN;
        ratingService.deleteRating(ratingId, userDetails.getUser().getUserId(), isAdmin);
        return ResponseEntity.ok(ApiResponseDTO.ok("Rating deleted", null));
    }
}
