package com.pm.backend.service;

import com.pm.backend.dto.user.UserResponse;
import com.pm.backend.entity.User;
import com.pm.backend.exception.ResourceNotFoundException;
import com.pm.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> allUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<UserResponse> searchUsers(String query) {
        if (!StringUtils.hasText(query)) {
            return allUsers();
        }
        String q = query.trim();
        if (q.length() < 2) {
            return allUsers();
        }
        return userRepository.searchByNameOrEmail(q).stream().map(this::toDto).toList();
    }

    public UserResponse byId(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toDto(user);
    }

    public User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserResponse toDto(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }
}
