package com.mmp.beacon.commute.query.response;

public record CommuteStatisticsResponse(
        UserInfo userInfo,
        CommuteStatistics statistics
) {
    public record UserInfo(
            Long id,
            String userId,
            String name
    ) {
    }

    public record CommuteStatistics(
            long presentDays,
            long lateDays,
            long absentDays,
            long totalDays
    ) {
    }
}
