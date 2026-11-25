package com.roomkh.repository;

import com.roomkh.entity.RoomModeration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomModerationRepository extends JpaRepository<RoomModeration, Long> {
    List<RoomModeration> findByRoom_RoomIdOrderByCreatedAtDesc(Long roomId);
}
