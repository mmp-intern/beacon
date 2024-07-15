package com.mmp.beacon.request.domain;

import com.mmp.beacon.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "password_reset_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordResetRequest {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "request_no")
    private Request request;

    @Column(name = "current_password", nullable = false)
    private String currentPassword;

    @Column(name = "new_password", nullable = false)
    private String newPassword;
}
