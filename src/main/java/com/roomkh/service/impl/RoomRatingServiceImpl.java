package com.roomkh.service.impl;

import com.roomkh.dto.request.RoomRatingRequestDTO;
import com.roomkh.dto.response.RoomRatingResponseDTO;
import com.roomkh.entity.Room;
import com.roomkh.entity.RoomRating;
import com.roomkh.entity.User;
import com.roomkh.enums.ModerationStatus;
import com.roomkh.exception.BadRequestException;
import com.roomkh.exception.ForbiddenException;
import com.roomkh.exception.ResourceNotFoundException;
import com.roomkh.repository.RoomRatingRepository;
import com.roomkh.repository.RoomRepository;
import com.roomkh.repository.UserRepository;
import com.roomkh.service.RoomRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomRatingServiceImpl implements RoomRatingService {

    private final RoomRatingRepository ratingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RoomRatingResponseDTO rateRoom(Long userId, RoomRatingRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (room.getModerationStatus() != ModerationStatus.APPROVED) {
            throw new BadRequestException("Only approved rooms can be rated");
        }

        if (room.getLandlord().getUser().getUserId().equals(userId)) {
            throw new BadRequestException("You cannot rate your own room");
        }

        // Upsert: update existing rating or create a new one (UNIQUE user_id + room_id)
        RoomRating rating = ratingRepository.findByUser_UserIdAndRoom_RoomId(userId, room.getRoomId())
                .orElseGet(() -> RoomRating.builder()
                        .user(user)
                        .room(room)
                        .build());

        rating.setRatingValue(dto.getRatingValue());
        rating.setRatingComment(dto.getRatingComment());

        // Flush immediately so PostgreSQL rating trigger recalculates rooms.average_rating & rating_count
        return toResponse(ratingRepository.saveAndFlush(rating));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomRatingResponseDTO> getRatingsByRoom(Long roomId) {
        return ratingRepository.findByRoom_RoomIdOrderByCreatedAtDesc(roomId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteRating(Long ratingId, Long requesterUserId, boolean isAdmin) {
        RoomRating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));

        if (!isAdmin && !rating.getUser().getUserId().equals(requesterUserId)) {
            throw new ForbiddenException("You can only delete your own rating");
        }

        ratingRepository.delete(rating);
        ratingRepository.flush();
    }

    private RoomRatingResponseDTO toResponse(RoomRating r) {
        return RoomRatingResponseDTO.builder()
                .ratingId(r.getRatingId())
                .roomId(r.getRoom().getRoomId())
                .userId(r.getUser().getUserId())
                .reviewerName(r.getUser().getFullName())
                .ratingValue(r.getRatingValue())
                .ratingComment(r.getRatingComment())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
