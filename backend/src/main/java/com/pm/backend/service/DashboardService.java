package com.pm.backend.service;

import com.pm.backend.dto.dashboard.DashboardResponse;
import com.pm.backend.entity.Task;
import com.pm.backend.entity.TaskStatus;
import com.pm.backend.repository.ProjectRepository;
import com.pm.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public DashboardResponse metrics() {
        List<Task> tasks = taskRepository.findAll();
        long completed = tasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        long pending = tasks.stream().filter(t -> t.getStatus() != TaskStatus.COMPLETED).count();
        long overdue = tasks.stream()
                .filter(t -> t.getStatus() != TaskStatus.COMPLETED)
                .filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(LocalDate.now()))
                .count();

        return new DashboardResponse(
                projectRepository.count(),
                tasks.size(),
                completed,
                pending,
                overdue
        );
    }
}
