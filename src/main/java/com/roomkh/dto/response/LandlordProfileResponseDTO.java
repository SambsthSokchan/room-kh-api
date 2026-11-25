package com.roomkh.dto.response;

import com.roomkh.enums.BusinessType;
import com.roomkh.enums.IdCardType;
import com.roomkh.enums.LandlordStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class LandlordProfileResponseDTO {
    private Long landlordProfileId;
    private Long userId;
    private String businessName;
    private BusinessType businessType;
    private String contactNumber;
    private String businessEmail;
    private String businessAddress;
    private String district;
    private String city;
    private IdCardType idType;
    private String idNumber;
    private String idPhotoUrl;
    private String businessPhotoUrl;
    private String businessDescription;
    private LandlordStatus landlordStatus;
    private OffsetDateTime createdAt;
}
