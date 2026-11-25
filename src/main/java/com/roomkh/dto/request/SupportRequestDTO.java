package com.roomkh.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SupportRequestDTO {

    @NotBlank(message = "Support subject is required")
    @Size(max = 150)
    private String supportSubject;

    @NotBlank(message = "Support description is required")
    private String supportDescription;
}
