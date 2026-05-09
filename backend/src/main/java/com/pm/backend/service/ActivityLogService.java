package com.pm.backend.service;

import com.pm.backend.entity.ActivityLog;
import com.pm.backend.entity.User;
import com.pm.backend.repository.ActivityLogRepository;
import com.pm.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    public void log(Long userId, String action, String entityType, String entityId) {
        ActivityLog log = new ActivityLog();
        User user = userRepository.findById(userId).orElse(null);
        log.setUser(user);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        activityLogRepository.save(log);
    }
}
