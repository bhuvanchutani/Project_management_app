package com.pm.backend.controller;

import com.pm.backend.dto.user.UserResponse;
import com.pm.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> users(@RequestParam(required = false) String q) {
        return userService.searchUsers(q);
    }

    @GetMapping("/{id}")
    public UserResponse byId(@PathVariable Long id) {
        return userService.byId(id);
    }
}
