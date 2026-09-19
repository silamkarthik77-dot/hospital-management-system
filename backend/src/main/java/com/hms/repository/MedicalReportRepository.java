package com.hms.repository;

import com.hms.entity.MedicalReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalReportRepository extends JpaRepository<MedicalReport, Long> {
    List<MedicalReport> findByPatient_Id(Long patientId);
}
