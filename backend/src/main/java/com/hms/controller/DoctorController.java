package com.hms.controller;

import com.hms.dto.response.ApiResponse;
import com.hms.dto.response.DoctorResponse;
import com.hms.service.DoctorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctors", description = "Public doctor directory")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ApiResponse<List<DoctorResponse>> getAllDoctors() {
        return ApiResponse.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/department/{departmentId}")
    public ApiResponse<List<DoctorResponse>> getByDepartment(@PathVariable Long departmentId) {
        return ApiResponse.ok(doctorService.getDoctorsByDepartment(departmentId));
    }

    @GetMapping("/{id}")
    public ApiResponse<DoctorResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(doctorService.getDoctorById(id));
    }
}
