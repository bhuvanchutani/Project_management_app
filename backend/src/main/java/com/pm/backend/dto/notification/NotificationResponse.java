package com.pm.backend.dto.notification;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        boolean read,
        Instant createdAt
) {
}
