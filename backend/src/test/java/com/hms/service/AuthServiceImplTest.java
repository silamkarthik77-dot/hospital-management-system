package com.hms.service;

import com.hms.dto.request.RegisterPatientRequest;
import com.hms.dto.response.AuthResponse;
import com.hms.entity.Role;
import com.hms.entity.RoleName;
import com.hms.entity.User;
import com.hms.exception.BadRequestException;
import com.hms.repository.PatientRepository;
import com.hms.repository.RoleRepository;
import com.hms.repository.UserRepository;
import com.hms.security.JwtService;
import com.hms.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterPatientRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterPatientRequest();
        request.setFullName("Test Patient");
        request.setEmail("test.patient@example.com");
        request.setPassword("StrongPass1!");
        request.setPhone("9999999999");
    }

    @Test
    void registerPatient_throwsWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.registerPatient(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerPatient_createsUserAndReturnsToken() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        Role patientRole = Role.builder().name(RoleName.PATIENT).build();
        when(roleRepository.findByName(RoleName.PATIENT)).thenReturn(Optional.of(patientRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed-password");

        User savedUser = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password("hashed-password")
                .role(patientRole)
                .build();
        savedUser.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(), eq(1L), eq("PATIENT"))).thenReturn("mock-jwt-token");

        AuthResponse response = authService.registerPatient(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getAccessToken());
        assertEquals("PATIENT", response.getRole());
        verify(patientRepository).save(any());
        verify(emailService).sendRegistrationEmail(request.getEmail(), request.getFullName());
    }
}
