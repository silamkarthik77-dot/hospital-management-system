package com.hms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private Long userId;
    private String fullName;
    private String email;
    private String role;
}
