package com.pm.backend.dto.project;

import com.pm.backend.entity.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record ProjectRequest(
        @NotBlank @Size(min = 2, max = 140) String title,
        String description,
        ProjectStatus status,
        LocalDate deadline,
        Set<Long> memberIds
) {
}
