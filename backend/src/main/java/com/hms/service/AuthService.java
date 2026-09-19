package com.hms.service;

import com.hms.dto.request.*;
import com.hms.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse registerPatient(RegisterPatientRequest request);
    AuthResponse login(LoginRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
