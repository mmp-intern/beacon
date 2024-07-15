package com.mmp.beacon.request.domain;

import com.mmp.beacon.commute.domain.AttendanceStatus;
import com.mmp.beacon.commute.domain.Commute;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "commute_edit_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommuteEditRequest {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "request_no")
    private Request request;

    @ManyToOne
    @JoinColumn(name = "commute_no", nullable = false)
    private Commute commute;

    @Column(name = "commute_started_at", updatable = false, nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "commute_ended_at", nullable = false)
    private LocalDateTime endedAt;

    @Column(name = "attendance_status")
    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendanceStatus;
}
