package com.hms.service.impl;

import com.hms.entity.Doctor;
import com.hms.entity.MedicalReport;
import com.hms.entity.Patient;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.DoctorRepository;
import com.hms.repository.MedicalReportRepository;
import com.hms.repository.PatientRepository;
import com.hms.service.MedicalReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MedicalReportServiceImpl implements MedicalReportService {

    private final MedicalReportRepository medicalReportRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Value("${app.file-storage-path:uploads/medical-reports}")
    private String storagePath;

    @Override
    @Transactional
    public Map<String, Object> uploadReport(
            Long patientId,
            Long uploadedByDoctorUserId,
            String title,
            String description,
            MultipartFile file) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Doctor doctor = null;

        if (uploadedByDoctorUserId != null) {
            doctor = doctorRepository
                    .findByUser_Id(uploadedByDoctorUserId)
                    .orElse(null);
        }

        try {
            // Create folder if it doesn't exist
            Path folder = Paths.get(storagePath);
            Files.createDirectories(folder);

            // Generate unique file name
            String originalFileName = file.getOriginalFilename();

            if (originalFileName == null || originalFileName.isBlank()) {
                originalFileName = "report";
            }

            String fileName = UUID.randomUUID() + "-" + originalFileName;

            // Save file locally
            Path filePath = folder.resolve(fileName);

            Files.copy(file.getInputStream(), filePath);

            // Store local file path in the existing s3Key column
            MedicalReport report = MedicalReport.builder()
                    .patient(patient)
                    .uploadedByDoctor(doctor)
                    .title(title)
                    .description(description)
                    .s3Key(filePath.toString())
                    .fileName(originalFileName)
                    .fileSizeBytes(file.getSize())
                    .build();

            report = medicalReportRepository.save(report);

            Map<String, Object> result = new HashMap<>();

            result.put("id", report.getId());
            result.put("title", report.getTitle());
            result.put("fileName", report.getFileName());
            result.put("fileSizeBytes", report.getFileSizeBytes());

            return result;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save medical report locally", e);
        }
    }

    @Override
    public List<Map<String, Object>> getReportsForPatient(Long patientId) {

        return medicalReportRepository.findByPatient_Id(patientId)
                .stream()
                .map(r -> {

                    Map<String, Object> m = new HashMap<>();

                    m.put("id", r.getId());
                    m.put("title", r.getTitle());
                    m.put("description", r.getDescription());
                    m.put("fileName", r.getFileName());
                    m.put("uploadedAt", r.getCreatedAt());

                    return m;

                })
                .toList();
    }

    @Override
    public String getDownloadUrl(Long reportId) {

        MedicalReport report = medicalReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        return report.getS3Key();
    }

    @Override
    @Transactional
    public void deleteReport(Long reportId) {

        MedicalReport report = medicalReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        try {

            // Delete local file
            Path filePath = Paths.get(report.getS3Key());

            Files.deleteIfExists(filePath);

            // Delete database record
            medicalReportRepository.delete(report);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to delete medical report file",
                    e
            );
        }
    }
}