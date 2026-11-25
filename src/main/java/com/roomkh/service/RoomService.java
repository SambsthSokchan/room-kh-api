package com.roomkh.service;

import com.roomkh.dto.request.RoomRequestDTO;
import com.roomkh.dto.request.RoomStatusRequestDTO;
import com.roomkh.dto.response.RoomResponseDTO;
import com.roomkh.enums.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface RoomService {
    RoomResponseDTO createRoom(Long landlordUserId, RoomRequestDTO request);
    List<RoomResponseDTO> getMyRooms(Long landlordUserId);
    RoomResponseDTO updateRoomStatus(Long roomId, Long landlordUserId, RoomStatusRequestDTO request);
    Page<RoomResponseDTO> searchPublicRooms(
            String city,
            String district,
            String nearbyUniversity,
            RoomType roomType,
            BigDecimal minRent,
            BigDecimal maxRent,
            String facility,
            String keyword,
            Pageable pageable
    );
    RoomResponseDTO getPublicRoomById(Long roomId);
}
