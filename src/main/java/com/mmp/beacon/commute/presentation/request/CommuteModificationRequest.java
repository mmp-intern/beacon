package com.mmp.beacon.commute.presentation.request;

import com.mmp.beacon.commute.domain.AttendanceStatus;
import com.mmp.beacon.commute.domain.WorkStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record CommuteModificationRequest(
        LocalTime startedAt,
        LocalTime endedAt,
        @NotNull AttendanceStatus attendanceStatus,
        @NotNull WorkStatus workStatus
) {
}
