package com.hms.controller;

import com.hms.dto.response.ApiResponse;
import com.hms.entity.User;
import com.hms.service.MedicalReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medical-reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Medical Reports", description = "Upload, download and delete medical reports")
public class MedicalReportController {

    private final MedicalReportService medicalReportService;

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ApiResponse<Map<String, Object>> upload(@AuthenticationPrincipal User user,
                                                     @RequestParam Long patientId,
                                                     @RequestParam String title,
                                                     @RequestParam(required = false) String description,
                                                     @RequestParam MultipartFile file) {
        return ApiResponse.ok("Report uploaded", medicalReportService.uploadReport(
                patientId, user.getId(), title, description, file));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ApiResponse<List<Map<String, Object>>> forPatient(@PathVariable Long patientId) {
        return ApiResponse.ok(medicalReportService.getReportsForPatient(patientId));
    }

    @GetMapping("/{id}/download-url")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ApiResponse<String> downloadUrl(@PathVariable Long id) {
        return ApiResponse.ok(medicalReportService.getDownloadUrl(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        medicalReportService.deleteReport(id);
        return ApiResponse.ok("Report deleted", null);
    }
}
