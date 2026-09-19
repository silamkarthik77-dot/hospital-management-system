package com.hms.controller;

import com.hms.dto.response.ApiResponse;
import com.hms.entity.Patient;
import com.hms.entity.User;
import com.hms.service.PatientService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Patients", description = "Patient self-service profile")
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<Patient> me(@AuthenticationPrincipal User user) {
        return ApiResponse.ok(patientService.getProfile(user.getId()));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<Patient> updateMe(@AuthenticationPrincipal User user, @RequestBody Patient updates) {
        return ApiResponse.ok("Profile updated", patientService.updateProfile(user.getId(), updates));
    }
}
