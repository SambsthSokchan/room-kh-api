package com.roomkh.dto.response;

import com.roomkh.enums.ModerationStatus;
import com.roomkh.enums.RoomStatus;
import com.roomkh.enums.RoomType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class RoomResponseDTO {
    private Long roomId;
    private Long landlordId;
    private String businessName;
    private String roomTitle;
    private RoomType roomType;
    private BigDecimal monthlyRent;
    private String roomAddress;
    private String district;
    private String city;
    private String nearbyUniversity;
    private String roomDescription;
    private RoomStatus roomStatus;
    private ModerationStatus moderationStatus;
    private BigDecimal averageRating;
    private Integer ratingCount;
    private List<String> photoUrls;
    private List<String> facilities;
    private OffsetDateTime createdAt;
}
