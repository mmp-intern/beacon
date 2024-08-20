package com.mmp.beacon.commute.domain;

import com.mmp.beacon.global.domain.BaseEntity;
import com.mmp.beacon.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

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

    @Column(name = "commute_date", nullable = false)
    private LocalDate date;

    @Column(name = "commute_started_at")
    private LocalTime startedAt;

    @Column(name = "commute_ended_at")
    private LocalTime endedAt;

    @Column(name = "attendance_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendanceStatus;

    @Column(name = "work_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkStatus workStatus;

    @Builder
    public Commute(
            User user,
            LocalDate date,
            LocalTime startedAt,
            LocalTime endedAt,
            AttendanceStatus attendanceStatus,
            WorkStatus workStatus
    ) {
        this.user = user;
        this.date = date;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.attendanceStatus = attendanceStatus;
        this.workStatus = workStatus;
    }

    public void updateWorkStatus(WorkStatus workStatus) {
        this.workStatus = workStatus;
    }

    public void updateAttendanceStatus(AttendanceStatus attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    /**
     * Commute 엔티티의 출근 시간과 퇴근 시간을 갱신합니다.
     * 주어진 출근 시간(earlyTimestamp)이 현재 출근 시간보다 이른 경우, 출근 시간을 갱신합니다.
     * 주어진 퇴근 시간(latestTimestamp)이 현재 퇴근 시간보다 늦은 경우, 퇴근 시간을 갱신합니다.
     *
     * @param earlyTimestamp  새로운 출근 시간
     * @param latestTimestamp 새로운 퇴근 시간
     */
    public void updateTimestamps(LocalTime earlyTimestamp, LocalTime latestTimestamp) {
        if (this.startedAt == null || earlyTimestamp.isBefore(this.startedAt)) {
            this.startedAt = earlyTimestamp;
        }
        if (this.endedAt == null || latestTimestamp.isAfter(this.endedAt)) {
            this.endedAt = latestTimestamp;
        }
    }

    public void updateTimestampByAdmin(LocalTime startedAt, LocalTime endedAt) {
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }
}
