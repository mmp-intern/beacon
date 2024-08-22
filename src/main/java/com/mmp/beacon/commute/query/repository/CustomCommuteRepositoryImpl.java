package com.mmp.beacon.commute.query.repository;

import com.mmp.beacon.commute.domain.AttendanceStatus;
import com.mmp.beacon.commute.domain.Commute;
import com.mmp.beacon.commute.domain.QCommute;
import com.mmp.beacon.commute.query.response.CommuteRecordInfo;
import com.mmp.beacon.commute.query.response.CommuteStatisticsInfo;
import com.mmp.beacon.commute.query.response.CommuteStatisticsResponse;
import com.mmp.beacon.commute.query.response.QCommuteRecordInfo;
import com.mmp.beacon.user.domain.QUser;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomCommuteRepositoryImpl implements CustomCommuteRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Commute> findByCompanyIdAndPeriodAndSearchTerm(
            Long companyId,
            LocalDate startDate,
            LocalDate endDate,
            String searchTerm,
            String searchBy,
            Pageable pageable
    ) {
        QCommute commute = QCommute.commute;
        QUser user = QUser.user;

        BooleanExpression predicate = commute.user.company.id.eq(companyId)
                .and(commute.date.between(startDate, endDate))
                .and(buildSearchPredicate(user, searchTerm, searchBy))
                .and(commute.isDeleted.isFalse())
                .and(user.isDeleted.isFalse());

        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(pageable, user, commute);

        List<Commute> commutes = queryFactory.selectFrom(commute)
                .leftJoin(commute.user, user).fetchJoin()
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .fetch();

        long total = Optional.ofNullable(queryFactory.select(commute.count())
                        .from(commute)
                        .where(predicate)
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(commutes, pageable, total);
    }

    @Override
    public Page<CommuteRecordInfo> findByCompanyIdAndDateAndSearchTerm(
            Long companyId,
            LocalDate date,
            String searchTerm,
            String searchBy,
            Pageable pageable
    ) {
        QCommute commute = QCommute.commute;
        QUser user = QUser.user;

        BooleanExpression companyPredicate = user.company.id.eq(companyId).and(user.isDeleted.isFalse());
        BooleanExpression searchPredicate = buildSearchPredicate(user, searchTerm, searchBy);
        BooleanExpression deletedPredicate = commute.isDeleted.isFalse();

        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(pageable, user, commute);

        List<CommuteRecordInfo> results = queryFactory
                .select(new QCommuteRecordInfo(
                        user.id,
                        user.userId,
                        user.name,
                        commute.id,
                        commute.date,
                        commute.startedAt,
                        commute.endedAt,
                        commute.attendanceStatus,
                        commute.workStatus
                ))
                .from(user)
                .leftJoin(commute).on(commute.user.id.eq(user.id).and(commute.date.eq(date)).and(deletedPredicate))
                .where(companyPredicate.and(searchPredicate))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .fetch();

        long total = Optional.ofNullable(queryFactory.select(user.count())
                        .from(user)
                        .where(companyPredicate.and(searchPredicate))
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<CommuteStatisticsResponse> findCommuteStatistics(
            Long companyId,
            LocalDate startDate,
            LocalDate endDate,
            String searchTerm,
            String searchBy,
            Pageable pageable
    ) {
        QCommute commute = QCommute.commute;
        QUser user = QUser.user;

        BooleanExpression companyPredicate = user.company.id.eq(companyId).and(user.isDeleted.isFalse());
        BooleanExpression datePredicate = commute.date.between(startDate, endDate);
        BooleanExpression searchPredicate = buildSearchPredicate(user, searchTerm, searchBy);
        BooleanExpression deletedPredicate = commute.isDeleted.isFalse();

        List<OrderSpecifier<?>> orderSpecifiers = getStatisticsOrderSpecifiers(pageable, user, commute);

        List<CommuteStatisticsInfo> results = queryFactory
                .select(Projections.constructor(CommuteStatisticsInfo.class,
                        user.id,
                        user.userId,
                        user.name,
                        new CaseBuilder()
                                .when(commute.attendanceStatus.eq(AttendanceStatus.PRESENT))
                                .then(1L)
                                .otherwise(0L)
                                .sum().as("presentDays"),
                        new CaseBuilder()
                                .when(commute.attendanceStatus.eq(AttendanceStatus.LATE))
                                .then(1L)
                                .otherwise(0L)
                                .sum().as("lateDays"),
                        new CaseBuilder()
                                .when(commute.attendanceStatus.eq(AttendanceStatus.ABSENT))
                                .then(1L)
                                .otherwise(0L)
                                .sum().as("absentDays"),
                        commute.count().as("totalDays")
                ))
                .from(user)
                .leftJoin(commute).on(commute.user.id.eq(user.id).and(datePredicate).and(deletedPredicate))
                .where(companyPredicate.and(searchPredicate))
                .groupBy(user.id)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(queryFactory.select(user.count())
                        .from(user)
                        .where(companyPredicate.and(searchPredicate))
                        .fetchOne())
                .orElse(0L);

        List<CommuteStatisticsResponse> responses = results.stream()
                .map(info -> new CommuteStatisticsResponse(
                        new CommuteStatisticsResponse.UserInfo(info.getUserId(), info.getUserLoginId(), info.getUserName()),
                        new CommuteStatisticsResponse.CommuteStatistics(
                                info.getPresentDays(),
                                info.getLateDays(),
                                info.getAbsentDays(),
                                info.getTotalDays()
                        )
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, total);
    }

    private BooleanExpression buildSearchPredicate(QUser user, String searchTerm, String searchBy) {
        if (searchTerm != null && !searchTerm.isBlank()) {
            if ("name".equalsIgnoreCase(searchBy)) {
                return user.name.containsIgnoreCase(searchTerm);
            } else if ("id".equalsIgnoreCase(searchBy)) {
                return user.userId.containsIgnoreCase(searchTerm);
            }
        }
        return null;
    }

    private List<OrderSpecifier<?>> getOrderSpecifiers(Pageable pageable, QUser user, QCommute commute) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        pageable.getSort().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String property = order.getProperty();
            switch (property) {
                case "id":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, user.id));
                    break;
                case "userId":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, user.userId));
                    break;
                case "name":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, user.name));
                    break;
                case "date":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, commute.date));
                    break;
                case "startedAt":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, commute.startedAt));
                    break;
                case "endedAt":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, commute.endedAt));
                    break;
                case "attendanceStatus":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, commute.attendanceStatus));
                    break;
                case "workStatus":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, commute.workStatus));
                    break;
                default:
                    break;
            }
        });
        return orderSpecifiers;
    }

    private List<OrderSpecifier<?>> getStatisticsOrderSpecifiers(Pageable pageable, QUser user, QCommute commute) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        pageable.getSort().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String property = order.getProperty();
            switch (property) {
                case "id":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, user.id));
                    break;
                case "userId":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, user.userId));
                    break;
                case "name":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, user.name));
                    break;
                case "presentDays":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, Expressions.numberPath(Long.class, "presentDays")));
                    break;
                case "lateDays":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, Expressions.numberPath(Long.class, "lateDays")));
                    break;
                case "absentDays":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, Expressions.numberPath(Long.class, "absentDays")));
                    break;
                case "totalDays":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, Expressions.numberPath(Long.class, "totalDays")));
                    break;
                default:
                    break;
            }
        });
        return orderSpecifiers;
    }
}
