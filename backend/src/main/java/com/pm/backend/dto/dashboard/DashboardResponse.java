package com.pm.backend.dto.dashboard;

public record DashboardResponse(
        long totalProjects,
        long totalTasks,
        long completedTasks,
        long pendingTasks,
        long overdueTasks
) {
}
