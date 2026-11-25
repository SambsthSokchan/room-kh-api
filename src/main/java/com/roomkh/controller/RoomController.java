package com.roomkh.controller;

import com.roomkh.dto.request.RoomRequestDTO;
import com.roomkh.dto.request.RoomStatusRequestDTO;
import com.roomkh.dto.response.ApiResponseDTO;
import com.roomkh.dto.response.RoomResponseDTO;
import com.roomkh.enums.RoomType;
import com.roomkh.security.CustomUserDetails;
import com.roomkh.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponseDTO<RoomResponseDTO>> createRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RoomRequestDTO request
    ) {
        RoomResponseDTO created = roomService.createRoom(userDetails.getUser().getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Room submitted for admin review", created));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponseDTO<List<RoomResponseDTO>>> getMyRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<RoomResponseDTO> rooms = roomService.getMyRooms(userDetails.getUser().getUserId());
        return ResponseEntity.ok(ApiResponseDTO.ok("Your rooms loaded", rooms));
    }

    @PatchMapping("/{roomId}/status")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponseDTO<RoomResponseDTO>> updateRoomStatus(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RoomStatusRequestDTO request
    ) {
        RoomResponseDTO updated = roomService.updateRoomStatus(
                roomId,
                userDetails.getUser().getUserId(),
                request
        );
        return ResponseEntity.ok(ApiResponseDTO.ok("Room status updated", updated));
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponseDTO<Page<RoomResponseDTO>>> searchPublicRooms(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String nearbyUniversity,
            @RequestParam(required = false) RoomType roomType,
            @RequestParam(required = false) BigDecimal minRent,
            @RequestParam(required = false) BigDecimal maxRent,
            @RequestParam(required = false) String facility,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<RoomResponseDTO> results = roomService.searchPublicRooms(
                city, district, nearbyUniversity, roomType, minRent, maxRent, facility, keyword, pageable
        );
        return ResponseEntity.ok(ApiResponseDTO.ok("Rooms loaded", results));
    }

    @GetMapping("/public/{roomId}")
    public ResponseEntity<ApiResponseDTO<RoomResponseDTO>> getPublicRoomById(@PathVariable Long roomId) {
        return ResponseEntity.ok(ApiResponseDTO.ok("Room details loaded", roomService.getPublicRoomById(roomId)));
    }
}
