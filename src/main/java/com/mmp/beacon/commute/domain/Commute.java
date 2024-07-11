package com.mmp.beacon.commute.domain;

import com.mmp.beacon.global.domain.BaseEntity;
import com.mmp.beacon.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "commute")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Commute extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commute_no")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User user;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "commute_started_at", updatable = false, nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "commute_ended_at", nullable = false)
    private LocalDateTime endedAt;

    @PrePersist
    public void onPrePersist() {
        this.startedAt = this.getCreateAt();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.endedAt = this.getUpdateAt();
    }
}
