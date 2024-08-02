package com.mmp.beacon.commute.query.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class CommuteStatisticsInfo {

    private final Long userId;
    private final String userLoginId;
    private final String userName;
    private final long presentDays;
    private final long lateDays;
    private final long absentDays;
    private final long totalDays;

    @QueryProjection
    public CommuteStatisticsInfo(
            Long userId,
            String userLoginId,
            String userName,
            long presentDays,
            long lateDays,
            long absentDays,
            long totalDays
    ) {
        this.userId = userId;
        this.userLoginId = userLoginId;
        this.userName = userName;
        this.presentDays = presentDays;
        this.lateDays = lateDays;
        this.absentDays = absentDays;
        this.totalDays = totalDays;
    }
}
