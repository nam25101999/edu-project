package com.edu.university.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base abstract class for all entities to enable standard JPA auditing.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
@NoArgsConstructor
public abstract class BaseEntity {

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    protected String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    protected String updatedBy;

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    protected String deletedBy;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    protected Boolean isActive = true;

    /**
     * Getter duy trì khả năng tương thích với các code cũ gọi .isActive()
     * Đảm bảo null-safe khi xử lý kiểu Boolean.
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }

    /**
     * Setter duy trì khả năng tương thích với các code cũ gọi .setActive()
     */
    public void setActive(Boolean active) {
        this.isActive = active;
    }

    /**
     * Soft delete method to set metadata for deletion.
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isActive = false;
    }

    /**
     * Soft delete method to set metadata for deletion.
     * @param deletedByActionUser Current user performing the deletion.
     */
    public void softDelete(String deletedByActionUser) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedByActionUser;
        this.isActive = false;
    }
}
