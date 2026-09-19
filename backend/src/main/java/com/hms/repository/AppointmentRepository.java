package com.hms.repository;

import com.hms.entity.Appointment;
import com.hms.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient_Id(Long patientId);
    List<Appointment> findByDoctor_Id(Long doctorId);
    List<Appointment> findByDoctor_IdAndAppointmentDate(Long doctorId, LocalDate date);
    List<Appointment> findByAppointmentDate(LocalDate date);
    long countByAppointmentDateAndStatus(LocalDate date, AppointmentStatus status);
    boolean existsByDoctor_IdAndAppointmentDateAndAppointmentTime(Long doctorId, LocalDate date, java.time.LocalTime time);
}
