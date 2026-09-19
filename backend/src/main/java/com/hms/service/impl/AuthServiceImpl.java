package com.hms.service.impl;

import com.hms.dto.request.*;
import com.hms.dto.response.AuthResponse;
import com.hms.entity.*;
import com.hms.exception.BadRequestException;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.PatientRepository;
import com.hms.repository.RoleRepository;
import com.hms.repository.UserRepository;
import com.hms.security.JwtService;
import com.hms.service.AuthService;
import com.hms.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Override
    @Transactional
    public AuthResponse registerPatient(RegisterPatientRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with this email already exists");
        }

        Role patientRole = roleRepository.findByName(RoleName.PATIENT)
                .orElseThrow(() -> new ResourceNotFoundException("PATIENT role not seeded"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(patientRole)
                .enabled(true)
                .build();
        user = userRepository.save(user);

        Patient patient = Patient.builder()
                .user(user)
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .bloodGroup(request.getBloodGroup())
                .address(request.getAddress())
                .build();
        patientRepository.save(patient);

        emailService.sendRegistrationEmail(user.getEmail(), user.getFullName());

        String token = jwtService.generateToken(user, user.getId(), patientRole.getName().name());
        return buildAuthResponse(user, token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtService.generateToken(user, user.getId(), user.getRole().getName().name());
        return buildAuthResponse(user, token);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with this email"));

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // In production this would be a frontend reset link, e.g. https://app/reset-password?token=...
        emailService.sendSimpleEmail(user.getEmail(), "Password Reset Request",
                "Use this token to reset your password (valid for 1 hour): " + token);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        if (user.getResetPasswordTokenExpiry() == null || user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Reset token has expired, please request a new one");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getName().name())
                .build();
    }
}
