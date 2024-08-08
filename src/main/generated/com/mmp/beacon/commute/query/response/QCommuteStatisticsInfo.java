package com.mmp.beacon.commute.query.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.mmp.beacon.commute.query.response.QCommuteStatisticsInfo is a Querydsl Projection type for CommuteStatisticsInfo
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QCommuteStatisticsInfo extends ConstructorExpression<CommuteStatisticsInfo> {

    private static final long serialVersionUID = 492359735L;

    public QCommuteStatisticsInfo(com.querydsl.core.types.Expression<Long> userId, com.querydsl.core.types.Expression<String> userLoginId, com.querydsl.core.types.Expression<String> userName, com.querydsl.core.types.Expression<Long> presentDays, com.querydsl.core.types.Expression<Long> lateDays, com.querydsl.core.types.Expression<Long> absentDays, com.querydsl.core.types.Expression<Long> totalDays) {
        super(CommuteStatisticsInfo.class, new Class<?>[]{long.class, String.class, String.class, long.class, long.class, long.class, long.class}, userId, userLoginId, userName, presentDays, lateDays, absentDays, totalDays);
    }

}

