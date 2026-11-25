package com.roomkh.mapper;

import com.roomkh.dto.response.RoomResponseDTO;
import com.roomkh.entity.Room;
import com.roomkh.entity.RoomFacility;
import com.roomkh.entity.RoomPhoto;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public RoomResponseDTO toResponse(Room room) {
        return RoomResponseDTO.builder()
                .roomId(room.getRoomId())
                .landlordId(room.getLandlord().getLandlordProfileId())
                .businessName(room.getLandlord().getBusinessName())
                .roomTitle(room.getRoomTitle())
                .roomType(room.getRoomType())
                .monthlyRent(room.getMonthlyRent())
                .roomAddress(room.getRoomAddress())
                .district(room.getDistrict())
                .city(room.getCity())
                .nearbyUniversity(room.getNearbyUniversity())
                .roomDescription(room.getRoomDescription())
                .roomStatus(room.getRoomStatus())
                .moderationStatus(room.getModerationStatus())
                .averageRating(room.getAverageRating())
                .ratingCount(room.getRatingCount())
                .photoUrls(room.getPhotos().stream().map(RoomPhoto::getPhotoUrl).toList())
                .facilities(room.getFacilities().stream().map(RoomFacility::getFacilityName).toList())
                .createdAt(room.getCreatedAt())
                .build();
    }
}
