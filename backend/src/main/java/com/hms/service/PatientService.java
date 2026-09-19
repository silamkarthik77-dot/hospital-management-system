package com.hms.service;

import com.hms.entity.Patient;

import java.util.List;

public interface PatientService {
    Patient getProfile(Long userId);
    Patient updateProfile(Long userId, Patient updates);
    List<Patient> searchPatients(String query);
    List<Patient> getAllPatients();
}
