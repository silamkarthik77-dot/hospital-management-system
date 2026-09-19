package com.hms.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface MedicalReportService {
    Map<String, Object> uploadReport(Long patientId, Long uploadedByDoctorUserId, String title, String description, MultipartFile file);
    List<Map<String, Object>> getReportsForPatient(Long patientId);
    String getDownloadUrl(Long reportId);
    void deleteReport(Long reportId);
}
