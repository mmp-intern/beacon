package com.mmp.beacon.commute.query.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.mmp.beacon.commute.query.response.QCommuteRecordInfo is a Querydsl Projection type for CommuteRecordInfo
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QCommuteRecordInfo extends ConstructorExpression<CommuteRecordInfo> {

    private static final long serialVersionUID = 672143205L;

    public QCommuteRecordInfo(com.querydsl.core.types.Expression<Long> userId, com.querydsl.core.types.Expression<String> userLoginId, com.querydsl.core.types.Expression<String> userName, com.querydsl.core.types.Expression<Long> commuteId, com.querydsl.core.types.Expression<java.time.LocalDate> date, com.querydsl.core.types.Expression<java.time.LocalTime> startTime, com.querydsl.core.types.Expression<java.time.LocalTime> endTime, com.querydsl.core.types.Expression<com.mmp.beacon.commute.domain.AttendanceStatus> attendanceStatus, com.querydsl.core.types.Expression<com.mmp.beacon.commute.domain.WorkStatus> workStatus) {
        super(CommuteRecordInfo.class, new Class<?>[]{long.class, String.class, String.class, long.class, java.time.LocalDate.class, java.time.LocalTime.class, java.time.LocalTime.class, com.mmp.beacon.commute.domain.AttendanceStatus.class, com.mmp.beacon.commute.domain.WorkStatus.class}, userId, userLoginId, userName, commuteId, date, startTime, endTime, attendanceStatus, workStatus);
    }

}

