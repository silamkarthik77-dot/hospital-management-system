package com.hms.service;

import com.hms.dto.request.BookAppointmentRequest;
import com.hms.dto.request.UpdateDiagnosisRequest;
import com.hms.dto.response.AppointmentResponse;

import java.util.List;

public interface AppointmentService {
    AppointmentResponse bookAppointment(Long patientUserId, BookAppointmentRequest request);
    void cancelAppointment(Long requesterUserId, Long appointmentId, String reason);
    List<AppointmentResponse> getAppointmentsForPatient(Long patientUserId);
    List<AppointmentResponse> getAppointmentsForDoctor(Long doctorUserId);
    List<AppointmentResponse> getDoctorScheduleForDate(Long doctorUserId, java.time.LocalDate date);
    AppointmentResponse updateDiagnosis(Long doctorUserId, Long appointmentId, UpdateDiagnosisRequest request);
    AppointmentResponse confirmAppointment(Long appointmentId);
    List<AppointmentResponse> getAllAppointments();
}
