package com.hms.controller;

import com.hms.dto.response.ApiResponse;
import com.hms.entity.Patient;
import com.hms.service.PatientService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receptionist")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Receptionist", description = "Front-desk operations: patient search, scheduling assistance")
@PreAuthorize("hasAnyRole('RECEPTIONIST','ADMIN')")
public class ReceptionistController {

    private final PatientService patientService;

    @GetMapping("/patients/search")
    public ApiResponse<List<Patient>> search(@RequestParam String query) {
        return ApiResponse.ok(patientService.searchPatients(query));
    }

    @GetMapping("/patients")
    public ApiResponse<List<Patient>> allPatients() {
        return ApiResponse.ok(patientService.getAllPatients());
    }
}
