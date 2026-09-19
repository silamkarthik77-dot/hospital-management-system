package com.hms.service;

import com.hms.dto.response.DoctorResponse;

import java.util.List;

public interface DoctorService {
    List<DoctorResponse> getAllDoctors();
    List<DoctorResponse> getDoctorsByDepartment(Long departmentId);
    DoctorResponse getDoctorById(Long id);
}
