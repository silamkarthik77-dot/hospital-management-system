package com.hms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class DashboardStatsResponse {
    private long totalPatients;
    private long totalDoctors;
    private long todaysAppointments;
    private BigDecimal monthlyRevenue;
    private long pendingBills;
}
