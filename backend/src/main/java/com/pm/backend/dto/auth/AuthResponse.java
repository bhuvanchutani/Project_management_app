package com.pm.backend.dto.auth;

import com.pm.backend.entity.Role;

public record AuthResponse(
        String token,
        Long userId,
        String name,
        String email,
        Role role
) {
}
