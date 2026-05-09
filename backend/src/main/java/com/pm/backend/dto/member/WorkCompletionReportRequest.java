package com.pm.backend.dto.member;

import jakarta.validation.constraints.Size;

public record WorkCompletionReportRequest(
        @Size(max = 500) String note
) {
}
