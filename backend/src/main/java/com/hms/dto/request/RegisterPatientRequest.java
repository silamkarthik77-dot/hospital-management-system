package com.hms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterPatientRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank @Email(message = "A valid email is required")
    private String email;

    @NotBlank @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank
    private String phone;

    private LocalDate dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String address;
}
