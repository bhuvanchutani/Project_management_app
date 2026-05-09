package com.pm.backend.controller;

import com.pm.backend.dto.member.WorkCompletionReportRequest;
import com.pm.backend.entity.User;
import com.pm.backend.service.NotificationService;
import com.pm.backend.service.UserService;
import com.pm.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberWorkCompletionController {

    private final NotificationService notificationService;
    private final UserService userService;

    @PostMapping("/work-completion")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MEMBER')")
    public void reportWorkCompletion(@RequestBody(required = false) WorkCompletionReportRequest request) {
        User member = userService.getByEmail(SecurityUtil.currentUserEmail());
        String note = request != null && request.note() != null && !request.note().isBlank()
                ? request.note().trim()
                : "No additional details.";
        String title = "Work completion: " + member.getName();
        String message = member.getName() + " (" + member.getEmail() + ") reported that their assigned work is complete. Note: " + note;
        notificationService.notifyAllAdmins(title, message);
    }
}
