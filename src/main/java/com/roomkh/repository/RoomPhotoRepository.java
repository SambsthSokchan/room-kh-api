package com.roomkh.repository;

import com.roomkh.entity.RoomPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomPhotoRepository extends JpaRepository<RoomPhoto, Long> {
    List<RoomPhoto> findByRoom_RoomId(Long roomId);
}
