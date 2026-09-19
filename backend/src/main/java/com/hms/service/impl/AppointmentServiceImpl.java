package com.hms.service.impl;

import com.hms.dto.request.BookAppointmentRequest;
import com.hms.dto.request.UpdateDiagnosisRequest;
import com.hms.dto.response.AppointmentResponse;
import com.hms.entity.*;
import com.hms.exception.BadRequestException;
import com.hms.exception.ResourceNotFoundException;
import com.hms.exception.UnauthorizedException;
import com.hms.repository.AppointmentRepository;
import com.hms.repository.DoctorRepository;
import com.hms.repository.PatientRepository;
import com.hms.service.AppointmentService;
import com.hms.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(Long patientUserId, BookAppointmentRequest request) {
        Patient patient = patientRepository.findByUser_Id(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        boolean slotTaken = appointmentRepository.existsByDoctor_IdAndAppointmentDateAndAppointmentTime(
                doctor.getId(), request.getAppointmentDate(), request.getAppointmentTime());
        if (slotTaken) {
            throw new BadRequestException("This time slot is already booked. Please choose another slot.");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .status(AppointmentStatus.CONFIRMED)
                .reasonForVisit(request.getReasonForVisit())
                .build();
        appointment = appointmentRepository.save(appointment);

        emailService.sendAppointmentConfirmationEmail(
                patient.getUser().getEmail(), patient.getUser().getFullName(),
                doctor.getUser().getFullName(),
                request.getAppointmentDate().format(DateTimeFormatter.ISO_DATE),
                request.getAppointmentTime().toString());

        return toResponse(appointment);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long requesterUserId, Long appointmentId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        boolean isOwner = appointment.getPatient().getUser().getId().equals(requesterUserId);
        if (!isOwner) {
            throw new UnauthorizedException("You can only cancel your own appointments");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        appointmentRepository.save(appointment);

        emailService.sendAppointmentCancellationEmail(
                appointment.getPatient().getUser().getEmail(),
                appointment.getPatient().getUser().getFullName(),
                appointment.getAppointmentDate().format(DateTimeFormatter.ISO_DATE),
                appointment.getAppointmentTime().toString());
    }

    @Override
    public List<AppointmentResponse> getAppointmentsForPatient(Long patientUserId) {
        Patient patient = patientRepository.findByUser_Id(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));
        return appointmentRepository.findByPatient_Id(patient.getId()).stream().map(this::toResponse).toList();
    }

    @Override
    public List<AppointmentResponse> getAppointmentsForDoctor(Long doctorUserId) {
        Doctor doctor = doctorRepository.findByUser_Id(doctorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
        return appointmentRepository.findByDoctor_Id(doctor.getId()).stream().map(this::toResponse).toList();
    }

    @Override
    public List<AppointmentResponse> getDoctorScheduleForDate(Long doctorUserId, LocalDate date) {
        Doctor doctor = doctorRepository.findByUser_Id(doctorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
        return appointmentRepository.findByDoctor_IdAndAppointmentDate(doctor.getId(), date)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public AppointmentResponse updateDiagnosis(Long doctorUserId, Long appointmentId, UpdateDiagnosisRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getDoctor().getUser().getId().equals(doctorUserId)) {
            throw new UnauthorizedException("You can only update diagnosis for your own appointments");
        }

        appointment.setDiagnosisNotes(request.getDiagnosisNotes());
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse confirmAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        return toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll().stream().map(this::toResponse).toList();
    }

    private AppointmentResponse toResponse(Appointment a) {
        return AppointmentResponse.builder()
                .id(a.getId())
                .patientId(a.getPatient().getId())
                .patientName(a.getPatient().getUser().getFullName())
                .doctorId(a.getDoctor().getId())
                .doctorName(a.getDoctor().getUser().getFullName())
                .appointmentDate(a.getAppointmentDate())
                .appointmentTime(a.getAppointmentTime())
                .status(a.getStatus())
                .reasonForVisit(a.getReasonForVisit())
                .diagnosisNotes(a.getDiagnosisNotes())
                .build();
    }
}
