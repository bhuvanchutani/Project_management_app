package com.pm.backend.service;

import com.pm.backend.dto.notification.NotificationResponse;
import com.pm.backend.entity.Notification;
import com.pm.backend.entity.Role;
import com.pm.backend.entity.User;
import com.pm.backend.exception.ForbiddenException;
import com.pm.backend.exception.ResourceNotFoundException;
import com.pm.backend.repository.NotificationRepository;
import com.pm.backend.repository.UserRepository;
import com.pm.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public List<NotificationResponse> forCurrentUser() {
        User me = userRepository.findByEmail(SecurityUtil.currentUserEmail())
                .orElseThrow();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(me.getId()).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void markRead(Long id) {
        User me = userRepository.findByEmail(SecurityUtil.currentUserEmail())
                .orElseThrow();
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!n.getUser().getId().equals(me.getId())) {
            throw new ForbiddenException("Not your notification");
        }
        n.setRead(true);
    }

    @Transactional
    public void notifyAdminsExcept(User actor, String title, String message) {
        for (User admin : userRepository.findByRole(Role.ADMIN)) {
            if (admin.getId().equals(actor.getId())) {
                continue;
            }
            save(admin, title, message);
        }
        // If actor is the only admin, still no duplicate self-notification
    }

    @Transactional
    public void notifyAllAdmins(String title, String message) {
        for (User admin : userRepository.findByRole(Role.ADMIN)) {
            save(admin, title, message);
        }
    }

    @Transactional
    public void save(User recipient, String title, String message) {
        Notification n = new Notification();
        n.setUser(recipient);
        n.setTitle(title);
        n.setMessage(message);
        n.setRead(false);
        notificationRepository.save(n);
    }

    private NotificationResponse toDto(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getTitle(),
                n.getMessage(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}
