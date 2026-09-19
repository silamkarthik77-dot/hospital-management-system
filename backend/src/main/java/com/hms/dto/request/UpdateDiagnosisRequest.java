package com.hms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDiagnosisRequest {
    @NotBlank
    private String diagnosisNotes;
}
