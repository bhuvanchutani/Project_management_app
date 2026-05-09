package com.pm.backend.service;

import com.pm.backend.dto.project.ProjectRequest;
import com.pm.backend.dto.project.ProjectResponse;
import com.pm.backend.entity.Project;
import com.pm.backend.entity.ProjectStatus;
import com.pm.backend.entity.Role;
import com.pm.backend.entity.User;
import com.pm.backend.exception.ForbiddenException;
import com.pm.backend.exception.ResourceNotFoundException;
import com.pm.backend.repository.ProjectRepository;
import com.pm.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ActivityLogService activityLogService;

    public List<ProjectResponse> all() {
        return projectRepository.findAll().stream().map(this::toDto).toList();
    }

    public ProjectResponse byId(Long id) {
        return toDto(getEntity(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProjectResponse create(ProjectRequest request) {
        User creator = userService.getByEmail(SecurityUtil.currentUserEmail());
        Project project = new Project();
        mapRequest(project, request);
        project.setCreatedBy(creator);
        project.getMembers().add(creator);
        project = projectRepository.save(project);
        activityLogService.log(creator.getId(), "CREATE_PROJECT", "PROJECT", project.getId().toString());
        return toDto(project);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProjectResponse update(Long id, ProjectRequest request) {
        User actor = userService.getByEmail(SecurityUtil.currentUserEmail());
        Project project = getEntity(id);
        mapRequest(project, request);
        project = projectRepository.save(project);
        activityLogService.log(actor.getId(), "UPDATE_PROJECT", "PROJECT", project.getId().toString());
        return toDto(project);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        User actor = userService.getByEmail(SecurityUtil.currentUserEmail());
        Project project = getEntity(id);
        projectRepository.delete(project);
        activityLogService.log(actor.getId(), "DELETE_PROJECT", "PROJECT", id.toString());
    }

    public Project getEntity(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User user = userService.getByEmail(SecurityUtil.currentUserEmail());
        boolean member = project.getMembers().stream().anyMatch(m -> m.getId().equals(user.getId()));
        if (!member && user.getRole() != Role.ADMIN) {
            throw new ForbiddenException("You don't have access to this project");
        }
        return project;
    }

    private void mapRequest(Project project, ProjectRequest request) {
        project.setTitle(request.title());
        project.setDescription(request.description());
        project.setDeadline(request.deadline());
        project.setStatus(request.status() == null ? ProjectStatus.PLANNING : request.status());

        if (request.memberIds() != null) {
            Set<User> members = new HashSet<>();
            for (Long id : request.memberIds()) {
                members.add(userService.getUserEntity(id));
            }
            project.setMembers(members);
        }
    }

    private ProjectResponse toDto(Project project) {
        Set<Long> members = project.getMembers().stream().map(User::getId).collect(java.util.stream.Collectors.toSet());
        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getStatus(),
                project.getDeadline(),
                project.getCreatedBy().getId(),
                members,
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
