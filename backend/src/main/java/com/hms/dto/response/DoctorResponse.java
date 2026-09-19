package com.hms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class DoctorResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String departmentName;
    private String specialization;
    private Integer experienceYears;
    private BigDecimal consultationFee;
    private String availableDays;
    private String slotStartTime;
    private String slotEndTime;
}
