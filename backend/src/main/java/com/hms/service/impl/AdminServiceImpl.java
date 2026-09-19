package com.hms.service.impl;

import com.hms.dto.response.DashboardStatsResponse;
import com.hms.entity.AppointmentStatus;
import com.hms.entity.PaymentStatus;
import com.hms.repository.*;
import com.hms.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = LocalDateTime.now();

        return DashboardStatsResponse.builder()
                .totalPatients(patientRepository.count())
                .totalDoctors(doctorRepository.count())
                .todaysAppointments(appointmentRepository.findByAppointmentDate(today).size())
                .monthlyRevenue(invoiceRepository.sumRevenueBetween(monthStart, monthEnd))
                .pendingBills(invoiceRepository.findByStatus(PaymentStatus.PENDING).size())
                .build();
    }
}
