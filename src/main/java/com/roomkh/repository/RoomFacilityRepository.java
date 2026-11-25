package com.roomkh.repository;

import com.roomkh.entity.RoomFacility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomFacilityRepository extends JpaRepository<RoomFacility, Long> {
    List<RoomFacility> findByRoom_RoomId(Long roomId);
    boolean existsByRoom_RoomIdAndFacilityNameIgnoreCase(Long roomId, String facilityName);
}
