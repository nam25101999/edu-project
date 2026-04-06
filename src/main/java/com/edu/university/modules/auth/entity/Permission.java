package com.edu.university.modules.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "permissions",
        indexes = {
                @Index(name = "idx_permission_name", columnList = "name", unique = true),
                @Index(name = "idx_permission_resource", columnList = "resource")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"resource", "action"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100, updatable = false)
    private String name;

    @Column(nullable = false, length = 50)
    private String resource;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(length = 255)
    private String description;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void generateName() {
        if (this.resource != null && this.action != null) {
            this.resource = this.resource.toUpperCase().trim();
            this.action = this.action.toUpperCase().trim();
            this.name = this.resource + "_" + this.action;
        }
    }
}
