package com.pm.backend.repository;

import com.pm.backend.entity.Task;
import com.pm.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByAssignedTo(User assignedTo, Pageable pageable);
}
