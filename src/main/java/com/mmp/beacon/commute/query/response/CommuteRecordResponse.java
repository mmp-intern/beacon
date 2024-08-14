package com.mmp.beacon.commute.query.response;

import com.mmp.beacon.commute.domain.AttendanceStatus;
import com.mmp.beacon.commute.domain.WorkStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record CommuteRecordResponse(
        UserInfo user,
        CommuteInfo commute
) {
    public record UserInfo(
            Long id,
            String userId,
            String name
    ) {
    }

    public record CommuteInfo(
            Long id,
            LocalDate date,
            LocalTime startedAt,
            LocalTime endedAt,
            AttendanceStatus attendanceStatus,
            WorkStatus workStatus
    ) {
    }
}
