package hhplus.tdd.domain.lecture;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성된 시각

    private String createdBy; // 생성한 사용자 ID

    private LocalDateTime updatedAt; // 수정된 시각

    private String updatedBy; // 수정한 사용자 ID

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.createdBy = getCurrentUserId(); // 현재 사용자 ID
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = getCurrentUserId(); // 현재 사용자 ID
    }

    /**
     * 현재 사용자 ID를 가져오는 메서드 (수정중!!!)
     */
    private String getCurrentUserId() {
        return "admin";
    }
}
