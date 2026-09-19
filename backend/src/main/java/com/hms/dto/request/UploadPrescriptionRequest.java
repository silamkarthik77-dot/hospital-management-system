package com.hms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UploadPrescriptionRequest {
    @NotNull
    private Long appointmentId;

    @NotBlank
    private String medicines;

    private String instructions;
}
