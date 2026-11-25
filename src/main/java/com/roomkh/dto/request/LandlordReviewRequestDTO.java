package com.roomkh.dto.request;

import com.roomkh.enums.LandlordStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LandlordReviewRequestDTO {

    @NotNull(message = "Decision is required (APPROVED or REJECTED)")
    private LandlordStatus decision;

    private String reviewNote;
}
