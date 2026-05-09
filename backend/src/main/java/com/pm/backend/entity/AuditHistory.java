package com.pm.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "audit_history")
public class AuditHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 80)
    private String entityType;

    @Column(nullable = false, length = 80)
    private String entityId;

    @Column(nullable = false, length = 60)
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String details;
}
