package com.roomkh.repository;

import com.roomkh.entity.RoomRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRatingRepository extends JpaRepository<RoomRating, Long> {
    List<RoomRating> findByRoom_RoomIdOrderByCreatedAtDesc(Long roomId);
    Optional<RoomRating> findByUser_UserIdAndRoom_RoomId(Long userId, Long roomId);
    long countByUser_UserId(Long userId);
}
