package com.hms.service.impl;

import com.hms.entity.Patient;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.PatientRepository;
import com.hms.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public Patient getProfile(Long userId) {
        return patientRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));
    }

    @Override
    @Transactional
    public Patient updateProfile(Long userId, Patient updates) {
        Patient patient = getProfile(userId);
        if (updates.getAddress() != null) patient.setAddress(updates.getAddress());
        if (updates.getBloodGroup() != null) patient.setBloodGroup(updates.getBloodGroup());
        if (updates.getEmergencyContactName() != null) patient.setEmergencyContactName(updates.getEmergencyContactName());
        if (updates.getEmergencyContactPhone() != null) patient.setEmergencyContactPhone(updates.getEmergencyContactPhone());
        if (updates.getMedicalHistory() != null) patient.setMedicalHistory(updates.getMedicalHistory());
        return patientRepository.save(patient);
    }

    @Override
    public List<Patient> searchPatients(String query) {
        return patientRepository.findByUser_FullNameContainingIgnoreCaseOrUser_EmailContainingIgnoreCase(query, query);
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
}
