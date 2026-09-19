package com.hms.controller;

import com.hms.dto.response.ApiResponse;
import com.hms.dto.response.DashboardStatsResponse;
import com.hms.entity.Doctor;
import com.hms.entity.Patient;
import com.hms.service.AdminService;
import com.hms.service.PatientService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin", description = "Dashboard analytics and management")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final PatientService patientService;

    @GetMapping("/dashboard")
    public ApiResponse<DashboardStatsResponse> dashboard() {
        return ApiResponse.ok(adminService.getDashboardStats());
    }

    @GetMapping("/patients")
    public ApiResponse<List<Patient>> patients() {
        return ApiResponse.ok(patientService.getAllPatients());
    }
}
