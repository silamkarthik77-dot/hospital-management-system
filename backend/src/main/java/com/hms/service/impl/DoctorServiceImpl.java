package com.hms.service.impl;

import com.hms.dto.response.DoctorResponse;
import com.hms.entity.Doctor;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.DoctorRepository;
import com.hms.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    @Override
    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<DoctorResponse> getDoctorsByDepartment(Long departmentId) {
        return doctorRepository.findByDepartment_Id(departmentId).stream().map(this::toResponse).toList();
    }

    @Override
    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
        return toResponse(doctor);
    }

    private DoctorResponse toResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .fullName(doctor.getUser().getFullName())
                .email(doctor.getUser().getEmail())
                .phone(doctor.getUser().getPhone())
                .departmentName(doctor.getDepartment().getName())
                .specialization(doctor.getSpecialization())
                .experienceYears(doctor.getExperienceYears())
                .consultationFee(doctor.getConsultationFee())
                .availableDays(doctor.getAvailableDays())
                .slotStartTime(doctor.getSlotStartTime())
                .slotEndTime(doctor.getSlotEndTime())
                .build();
    }
}
