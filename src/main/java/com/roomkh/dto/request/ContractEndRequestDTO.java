package com.roomkh.dto.request;

import com.roomkh.enums.ContractStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContractEndRequestDTO {

    @NotNull(message = "Closing status is required (ENDED or CANCELLED)")
    private ContractStatus status;
}
