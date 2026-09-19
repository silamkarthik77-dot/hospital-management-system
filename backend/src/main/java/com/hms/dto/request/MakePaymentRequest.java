package com.hms.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MakePaymentRequest {
    @NotNull
    private Long invoiceId;

    @NotNull @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    @NotBlank
    private String paymentMethod;
}
