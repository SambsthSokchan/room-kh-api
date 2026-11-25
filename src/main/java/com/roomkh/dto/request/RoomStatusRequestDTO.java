package com.roomkh.dto.request;

import com.roomkh.enums.RoomStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomStatusRequestDTO {

    @NotNull(message = "Room status is required")
    private RoomStatus roomStatus;
}
