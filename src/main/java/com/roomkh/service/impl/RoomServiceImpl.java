package com.roomkh.service.impl;

import com.roomkh.dto.request.RoomRequestDTO;
import com.roomkh.dto.request.RoomStatusRequestDTO;
import com.roomkh.dto.response.RoomResponseDTO;
import com.roomkh.entity.LandlordProfile;
import com.roomkh.entity.Room;
import com.roomkh.entity.RoomFacility;
import com.roomkh.entity.RoomPhoto;
import com.roomkh.enums.LandlordStatus;
import com.roomkh.enums.ModerationStatus;
import com.roomkh.enums.RoomStatus;
import com.roomkh.enums.RoomType;
import com.roomkh.exception.ForbiddenException;
import com.roomkh.exception.ResourceNotFoundException;
import com.roomkh.mapper.RoomMapper;
import com.roomkh.repository.LandlordProfileRepository;
import com.roomkh.repository.RoomRepository;
import com.roomkh.service.RoomService;
import com.roomkh.specification.RoomSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final LandlordProfileRepository landlordProfileRepository;
    private final RoomMapper roomMapper;

    @Override
    @Transactional
    public RoomResponseDTO createRoom(Long landlordUserId, RoomRequestDTO dto) {
        LandlordProfile landlord = landlordProfileRepository.findByUser_UserId(landlordUserId)
                .orElseThrow(() -> new ForbiddenException("Landlord profile not found"));

        if (landlord.getLandlordStatus() != LandlordStatus.APPROVED) {
            throw new ForbiddenException("Your landlord profile is not approved yet");
        }

        Room room = Room.builder()
                .landlord(landlord)
                .roomTitle(dto.getRoomTitle().trim())
                .roomType(dto.getRoomType())
                .monthlyRent(dto.getMonthlyRent())
                .roomAddress(dto.getRoomAddress().trim())
                .district(dto.getDistrict())
                .city(dto.getCity().trim())
                .nearbyUniversity(dto.getNearbyUniversity())
                .roomDescription(dto.getRoomDescription())
                .roomStatus(RoomStatus.VACANT)
                .moderationStatus(ModerationStatus.PENDING)
                .build();

        if (dto.getPhotoUrls() != null) {
            for (int i = 0; i < dto.getPhotoUrls().size(); i++) {
                String url = dto.getPhotoUrls().get(i).trim();
                RoomPhoto photo = RoomPhoto.builder()
                        .room(room)
                        .photoUrl(url)
                        .photoType(i == 0 ? "COVER" : "GALLERY")
                        .build();
                room.getPhotos().add(photo);
            }
        }

        if (dto.getFacilities() != null) {
            dto.getFacilities().stream()
                    .map(String::trim)
                    .filter(name -> !name.isBlank())
                    .distinct()
                    .forEach(name -> room.getFacilities().add(
                            RoomFacility.builder()
                                    .room(room)
                                    .facilityName(name)
                                    .build()
                    ));
        }

        return roomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> getMyRooms(Long landlordUserId) {
        LandlordProfile landlord = landlordProfileRepository.findByUser_UserId(landlordUserId)
                .orElseThrow(() -> new ForbiddenException("Landlord profile not found"));

        return roomRepository.findByLandlord_LandlordProfileIdOrderByCreatedAtDesc(
                        landlord.getLandlordProfileId()
                )
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public RoomResponseDTO updateRoomStatus(Long roomId, Long landlordUserId, RoomStatusRequestDTO dto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (!room.getLandlord().getUser().getUserId().equals(landlordUserId)) {
            throw new ForbiddenException("You do not own this room listing");
        }

        room.setRoomStatus(dto.getRoomStatus());
        return roomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponseDTO> searchPublicRooms(
            String city,
            String district,
            String nearbyUniversity,
            RoomType roomType,
            BigDecimal minRent,
            BigDecimal maxRent,
            String facility,
            String keyword,
            Pageable pageable
    ) {
        Specification<Room> spec = RoomSpecification.filterPublicRooms(
                city, district, nearbyUniversity, roomType, minRent, maxRent, facility, keyword
        );
        return roomRepository.findAll(spec, pageable).map(roomMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponseDTO getPublicRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (room.getModerationStatus() != ModerationStatus.APPROVED) {
            throw new ResourceNotFoundException("Room not found");
        }

        return roomMapper.toResponse(room);
    }
}
