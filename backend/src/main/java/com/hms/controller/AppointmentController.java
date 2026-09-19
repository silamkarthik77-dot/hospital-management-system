package com.hms.controller;

import com.hms.dto.request.BookAppointmentRequest;
import com.hms.dto.request.UpdateDiagnosisRequest;
import com.hms.dto.response.ApiResponse;
import com.hms.dto.response.AppointmentResponse;
import com.hms.entity.User;
import com.hms.service.AppointmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Appointments", description = "Booking, cancellation and schedules")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<AppointmentResponse> book(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BookAppointmentRequest request) {

        return ApiResponse.ok(
                "Appointment booked",
                appointmentService.bookAppointment(user.getId(), request)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<Void> cancel(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {

        appointmentService.cancelAppointment(user.getId(), id, reason);
        return ApiResponse.ok("Appointment cancelled", null);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR')")
    public ApiResponse<List<AppointmentResponse>> myAppointments(
            @AuthenticationPrincipal User user) {

        List<AppointmentResponse> result =
                user.getRole().getName().name().equals("DOCTOR")
                        ? appointmentService.getAppointmentsForDoctor(user.getId())
                        : appointmentService.getAppointmentsForPatient(user.getId());

        return ApiResponse.ok(result);
    }

    @GetMapping("/doctor/schedule")
    @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<List<AppointmentResponse>> doctorSchedule(
            @AuthenticationPrincipal User user,
            @RequestParam LocalDate date) {

        return ApiResponse.ok(
                appointmentService.getDoctorScheduleForDate(user.getId(), date)
        );
    }

    @PutMapping("/{id}/diagnosis")
    @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<AppointmentResponse> updateDiagnosis(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody UpdateDiagnosisRequest request) {

        return ApiResponse.ok(
                "Diagnosis updated",
                appointmentService.updateDiagnosis(user.getId(), id, request)
        );
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR')")
    public ApiResponse<AppointmentResponse> confirm(@PathVariable Long id) {

        return ApiResponse.ok(
                "Appointment confirmed",
                appointmentService.confirmAppointment(id)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ApiResponse<List<AppointmentResponse>> all() {

        return ApiResponse.ok(
                appointmentService.getAllAppointments()
        );
    }
}