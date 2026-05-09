package com.pm.backend.service;

import com.pm.backend.dto.task.TaskRequest;
import com.pm.backend.dto.task.TaskResponse;
import com.pm.backend.entity.*;
import com.pm.backend.exception.ForbiddenException;
import com.pm.backend.exception.ResourceNotFoundException;
import com.pm.backend.repository.TaskRepository;
import com.pm.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ProjectService projectService;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;

    public Page<TaskResponse> all(int page, int size) {
        User current = userService.getByEmail(SecurityUtil.currentUserEmail());
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks = current.getRole() == Role.ADMIN
                ? taskRepository.findAll(pageable)
                : taskRepository.findByAssignedTo(current, pageable);
        return tasks.map(this::toDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse create(TaskRequest request) {
        User creator = userService.getByEmail(SecurityUtil.currentUserEmail());
        Project project = projectService.getEntity(request.projectId());
        User assigned = userService.getUserEntity(request.assignedToId());

        Task task = new Task();
        mapRequest(task, request);
        task.setCreatedBy(creator);
        task.setAssignedTo(assigned);
        task.setProject(project);
        task = taskRepository.save(task);
        activityLogService.log(creator.getId(), "CREATE_TASK", "TASK", task.getId().toString());
        return toDto(task);
    }

    public TaskResponse update(Long id, TaskRequest request) {
        User actor = userService.getByEmail(SecurityUtil.currentUserEmail());
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        boolean isAdmin = actor.getRole() == Role.ADMIN;
        boolean isAssignee = task.getAssignedTo().getId().equals(actor.getId());
        if (!isAdmin && !isAssignee) {
            throw new ForbiddenException("You can update only your own tasks");
        }

        TaskStatus previousStatus = task.getStatus();
        mapRequest(task, request);
        task.setAssignedTo(userService.getUserEntity(request.assignedToId()));
        task.setProject(projectService.getEntity(request.projectId()));
        task = taskRepository.save(task);
        activityLogService.log(actor.getId(), "UPDATE_TASK", "TASK", task.getId().toString());
        maybeNotifyAdminsTaskCompleted(actor, task, previousStatus);
        return toDto(task);
    }

    public TaskResponse markComplete(Long id) {
        User actor = userService.getByEmail(SecurityUtil.currentUserEmail());
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        boolean isAdmin = actor.getRole() == Role.ADMIN;
        boolean isAssignee = task.getAssignedTo().getId().equals(actor.getId());
        if (!isAdmin && !isAssignee) {
            throw new ForbiddenException("You can complete only your own tasks");
        }

        TaskStatus previousStatus = task.getStatus();
        task.setStatus(TaskStatus.COMPLETED);
        task = taskRepository.save(task);
        activityLogService.log(actor.getId(), "COMPLETE_TASK", "TASK", task.getId().toString());
        maybeNotifyAdminsTaskCompleted(actor, task, previousStatus);
        return toDto(task);
    }

    private void maybeNotifyAdminsTaskCompleted(User actor, Task task, TaskStatus previousStatus) {
        if (task.getStatus() != TaskStatus.COMPLETED || previousStatus == TaskStatus.COMPLETED) {
            return;
        }
        String title = "Task completed: " + task.getTitle();
        String message = actor.getName() + " (" + actor.getEmail() + ") marked task \"" + task.getTitle()
                + "\" (ID " + task.getId() + ") as completed.";
        if (actor.getRole() == Role.ADMIN) {
            notificationService.notifyAdminsExcept(actor, title, message);
        } else {
            notificationService.notifyAllAdmins(title, message);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        User actor = userService.getByEmail(SecurityUtil.currentUserEmail());
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        taskRepository.delete(task);
        activityLogService.log(actor.getId(), "DELETE_TASK", "TASK", id.toString());
    }

    private void mapRequest(Task task, TaskRequest request) {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setStatus(request.status() == null ? TaskStatus.TODO : request.status());
        task.setPriority(request.priority() == null ? TaskPriority.MEDIUM : request.priority());
    }

    private TaskResponse toDto(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getAssignedTo().getId(),
                task.getProject().getId(),
                task.getCreatedBy().getId(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
