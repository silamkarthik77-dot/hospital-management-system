package com.hms.service;

import com.hms.dto.request.BookAppointmentRequest;
import com.hms.dto.response.AppointmentResponse;
import com.hms.entity.*;
import com.hms.exception.BadRequestException;
import com.hms.repository.AppointmentRepository;
import com.hms.repository.DoctorRepository;
import com.hms.repository.PatientRepository;
import com.hms.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    void bookAppointment_throwsWhenSlotAlreadyTaken() {
        User patientUser = User.builder().fullName("P").email("p@x.com").build();
        patientUser.setId(1L);
        Patient patient = Patient.builder().user(patientUser).build();
        patient.setId(10L);

        User doctorUser = User.builder().fullName("Dr D").email("d@x.com").build();
        Doctor doctor = Doctor.builder().user(doctorUser).build();
        doctor.setId(20L);

        BookAppointmentRequest request = new BookAppointmentRequest();
        request.setDoctorId(20L);
        request.setAppointmentDate(LocalDate.now().plusDays(1));
        request.setAppointmentTime(LocalTime.of(10, 0));

        when(patientRepository.findByUser_Id(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(20L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctor_IdAndAppointmentDateAndAppointmentTime(
                20L, request.getAppointmentDate(), request.getAppointmentTime())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> appointmentService.bookAppointment(1L, request));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void bookAppointment_succeedsAndSendsConfirmationEmail() {
        User patientUser = User.builder().fullName("P").email("p@x.com").build();
        patientUser.setId(1L);
        Patient patient = Patient.builder().user(patientUser).build();
        patient.setId(10L);

        User doctorUser = User.builder().fullName("Dr D").email("d@x.com").build();
        Doctor doctor = Doctor.builder().user(doctorUser).build();
        doctor.setId(20L);

        BookAppointmentRequest request = new BookAppointmentRequest();
        request.setDoctorId(20L);
        request.setAppointmentDate(LocalDate.now().plusDays(1));
        request.setAppointmentTime(LocalTime.of(10, 0));

        when(patientRepository.findByUser_Id(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(20L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctor_IdAndAppointmentDateAndAppointmentTime(
                anyLong(), any(), any())).thenReturn(false);

        Appointment saved = Appointment.builder()
                .patient(patient).doctor(doctor)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .status(AppointmentStatus.CONFIRMED)
                .build();
        saved.setId(100L);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(saved);

        AppointmentResponse response = appointmentService.bookAppointment(1L, request);

        assertEquals(AppointmentStatus.CONFIRMED, response.getStatus());
        verify(emailService).sendAppointmentConfirmationEmail(eq("p@x.com"), eq("P"), eq("Dr D"), any(), any());
    }
}
