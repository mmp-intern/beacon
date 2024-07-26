package com.mmp.beacon.user.query.repository;

import com.mmp.beacon.user.domain.QUser;
import com.mmp.beacon.user.domain.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<User> findByCompanyIdAndSearchTerm(Long companyId, String searchTerm, String searchBy, Pageable pageable) {
        QUser user = QUser.user;

        BooleanExpression predicate = user.company.id.eq(companyId);
        if ("name".equalsIgnoreCase(searchBy)) {
            predicate = predicate.and(user.name.containsIgnoreCase(searchTerm));
        } else if ("id".equalsIgnoreCase(searchBy)) {
            predicate = predicate.and(user.id.eq(Long.parseLong(searchTerm)));
        }

        List<User> users = queryFactory.selectFrom(user)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(queryFactory.select(user.count())
                        .from(user)
                        .where(predicate)
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(users, pageable, total);
    }
}
