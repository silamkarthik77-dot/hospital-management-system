package com.hms.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookAppointmentRequest {

    @NotNull
    private Long doctorId;

    @NotNull
    @FutureOrPresent(message = "Appointment date cannot be in the past")
    private LocalDate appointmentDate;

    @NotNull
    private LocalTime appointmentTime;

    private String reasonForVisit;
}