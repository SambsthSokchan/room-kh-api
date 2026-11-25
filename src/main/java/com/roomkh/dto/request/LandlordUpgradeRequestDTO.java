package com.roomkh.dto.request;

import com.roomkh.enums.BusinessType;
import com.roomkh.enums.IdCardType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LandlordUpgradeRequestDTO {

    @NotBlank(message = "Business name is required")
    @Size(max = 150)
    private String businessName;

    @NotNull(message = "Business type is required")
    private BusinessType businessType = BusinessType.RENTAL_HOUSE;

    @NotBlank(message = "Contact number is required")
    @Size(max = 20)
    private String contactNumber;

    @Email(message = "Invalid business email format")
    @Size(max = 150)
    private String businessEmail;

    @NotBlank(message = "Business address is required")
    @Size(max = 255)
    private String businessAddress;

    @Size(max = 100)
    private String district;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @NotNull(message = "ID card type is required")
    private IdCardType idType = IdCardType.NATIONAL_ID;

    @NotBlank(message = "ID number is required")
    @Size(max = 50)
    private String idNumber;

    @NotBlank(message = "ID photo URL is required")
    @Size(max = 255)
    private String idPhotoUrl;

    @Size(max = 255)
    private String businessPhotoUrl;

    private String businessDescription;
}
