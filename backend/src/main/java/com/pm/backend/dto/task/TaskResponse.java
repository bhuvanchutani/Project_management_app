package com.pm.backend.dto.task;

import com.pm.backend.entity.TaskPriority;
import com.pm.backend.entity.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(
        Long id,
        String title,
        String description,
        Long assignedToId,
        Long projectId,
        Long createdById,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        Instant createdAt,
        Instant updatedAt
) {
}
