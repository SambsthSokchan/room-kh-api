package com.roomkh.repository;

import com.roomkh.entity.Room;
import com.roomkh.enums.ModerationStatus;
import com.roomkh.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    List<Room> findByLandlord_LandlordProfileIdOrderByCreatedAtDesc(Long landlordProfileId);
    List<Room> findByModerationStatusOrderByCreatedAtAsc(ModerationStatus moderationStatus);
    long countByModerationStatus(ModerationStatus moderationStatus);
    long countByModerationStatusAndRoomStatus(ModerationStatus moderationStatus, RoomStatus roomStatus);
    long countByLandlord_LandlordProfileId(Long landlordProfileId);
    long countByLandlord_LandlordProfileIdAndRoomStatus(Long landlordProfileId, RoomStatus roomStatus);
    long countByLandlord_LandlordProfileIdAndModerationStatus(Long landlordProfileId, ModerationStatus moderationStatus);
}
