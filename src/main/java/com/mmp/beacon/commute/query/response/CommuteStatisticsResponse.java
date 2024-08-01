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
            int presentDays,
            int lateDays,
            int absentDays,
            int totalDays
    ) {
    }
}
