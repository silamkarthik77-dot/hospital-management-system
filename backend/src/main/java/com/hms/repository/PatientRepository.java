package com.hms.repository;

import com.hms.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUser_Id(Long userId);
    java.util.List<Patient> findByUser_FullNameContainingIgnoreCaseOrUser_EmailContainingIgnoreCase(String name, String email);
}
