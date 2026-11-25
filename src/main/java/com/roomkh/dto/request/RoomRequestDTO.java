package com.roomkh.dto.request;

import com.roomkh.enums.RoomType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class RoomRequestDTO {

    @NotBlank(message = "Room title is required")
    @Size(max = 150)
    private String roomTitle;

    @NotNull(message = "Room type is required")
    private RoomType roomType;

    @NotNull(message = "Monthly rent is required")
    @DecimalMin(value = "0.01", message = "Monthly rent must be greater than 0")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal monthlyRent;

    @NotBlank(message = "Room address is required")
    @Size(max = 255)
    private String roomAddress;

    @Size(max = 100)
    private String district;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @Size(max = 150)
    private String nearbyUniversity;

    private String roomDescription;

    private List<@NotBlank @Size(max = 255) String> photoUrls = new ArrayList<>();

    private List<@NotBlank @Size(max = 100) String> facilities = new ArrayList<>();
}
