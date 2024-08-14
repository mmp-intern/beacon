package com.mmp.beacon.commute.query.response;

import com.mmp.beacon.commute.domain.AttendanceStatus;
import com.mmp.beacon.commute.domain.WorkStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class CommuteRecordInfo {

    private final Long userId;
    private final String userLoginId;
    private final String userName;
    private final Long commuteId;
    private final LocalDate date;
    private final LocalTime startedAt;
    private final LocalTime endedAt;
    private final AttendanceStatus attendanceStatus;
    private final WorkStatus workStatus;

    @QueryProjection
    public CommuteRecordInfo(
            Long userId,
            String userLoginId,
            String userName,
            Long commuteId,
            LocalDate date,
            LocalTime startedAt,
            LocalTime endedAt,
            AttendanceStatus attendanceStatus,
            WorkStatus workStatus
    ) {
        this.userId = userId;
        this.userLoginId = userLoginId;
        this.userName = userName;
        this.commuteId = commuteId;
        this.date = date;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.attendanceStatus = attendanceStatus;
        this.workStatus = workStatus;
    }
}
