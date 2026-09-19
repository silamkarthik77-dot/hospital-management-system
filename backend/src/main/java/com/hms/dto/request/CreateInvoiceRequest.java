package com.hms.dto.request;

import com.hms.entity.BillType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateInvoiceRequest {
    @NotNull
    private Long patientId;

    private Long appointmentId;

    @NotNull
    private BillType billType;

    @NotNull @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    private BigDecimal taxAmount;
}
