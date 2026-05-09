package com.pm.backend.dto.task;

import com.pm.backend.entity.TaskPriority;
import com.pm.backend.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank @Size(min = 2, max = 160) String title,
        String description,
        @NotNull Long assignedToId,
        @NotNull Long projectId,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate
) {
}
