package com.msa_delivery.order.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    @CreatedBy
    @Column(name = "created_by", length = 255, updatable = false)
    private String created_by;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @LastModifiedBy
    @Column(name = "updated_by", length = 255)
    private String updated_by;

    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;

    @Column(name = "deleted_by", length = 255)
    private String deleted_by;

    @Column(name = "is_deleted", length = 255)
    private boolean is_deleted = false;

    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated_at= LocalDateTime.now();
    }

    // 소프트 삭제 메서드
    public void delete(String deletedBy) {
        this.deleted_at = LocalDateTime.now();
        this.deleted_by = deletedBy;
        this.is_deleted = true;
    }
}
