package com.roomkh.service;

import com.roomkh.dto.request.RoomRatingRequestDTO;
import com.roomkh.dto.response.RoomRatingResponseDTO;

import java.util.List;

public interface RoomRatingService {
    RoomRatingResponseDTO rateRoom(Long userId, RoomRatingRequestDTO request);
    List<RoomRatingResponseDTO> getRatingsByRoom(Long roomId);
    void deleteRating(Long ratingId, Long requesterUserId, boolean isAdmin);
}
