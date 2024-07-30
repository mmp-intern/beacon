package com.mmp.beacon.commute.domain.repository;

import com.mmp.beacon.commute.domain.Commute;
import com.mmp.beacon.commute.domain.QCommute;
import com.mmp.beacon.user.domain.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
                .and(buildSearchPredicate(user, searchTerm, searchBy));

        List<Commute> commutes = queryFactory.selectFrom(commute)
                .leftJoin(commute.user, user).fetchJoin()
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(queryFactory.select(commute.count())
                        .from(commute)
                        .where(predicate)
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(commutes, pageable, total);
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
}
