package com.pm.backend.dto.project;

import com.pm.backend.entity.ProjectStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

public record ProjectResponse(
        Long id,
        String title,
        String description,
        ProjectStatus status,
        LocalDate deadline,
        Long createdBy,
        Set<Long> members,
        Instant createdAt,
        Instant updatedAt
) {
}
