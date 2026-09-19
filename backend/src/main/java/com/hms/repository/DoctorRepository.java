package com.hms.repository;

import com.hms.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUser_Id(Long userId);
    List<Doctor> findByDepartment_Id(Long departmentId);
    List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);
}
