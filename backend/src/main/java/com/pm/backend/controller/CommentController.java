package com.pm.backend.controller;

import com.pm.backend.entity.Comment;
import com.pm.backend.entity.Task;
import com.pm.backend.entity.User;
import com.pm.backend.exception.ResourceNotFoundException;
import com.pm.backend.repository.CommentRepository;
import com.pm.backend.repository.TaskRepository;
import com.pm.backend.service.UserService;
import com.pm.backend.util.SecurityUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;

    @GetMapping("/task/{taskId}")
    public List<Comment> byTask(@PathVariable Long taskId) {
        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
    }

    @PostMapping("/task/{taskId}")
    public Comment create(@PathVariable Long taskId, @RequestBody @NotBlank String content) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User user = userService.getByEmail(SecurityUtil.currentUserEmail());
        Comment comment = new Comment();
        comment.setTask(task);
        comment.setContent(content);
        comment.setUser(user);
        return commentRepository.save(comment);
    }
}
