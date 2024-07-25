package com.mmp.beacon.commute.application;

import com.mmp.beacon.commute.domain.Commute;
import com.mmp.beacon.commute.domain.repository.CommuteRepository;
import com.mmp.beacon.commute.query.response.CommuteRecordResponse;
import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.user.domain.AbstractUser;
import com.mmp.beacon.user.domain.Admin;
import com.mmp.beacon.user.domain.User;
import com.mmp.beacon.user.domain.repository.AbstractUserRepository;
import com.mmp.beacon.user.exception.SuperAdminAccessException;
import com.mmp.beacon.user.exception.UserNotFoundException;
import com.mmp.beacon.user.exception.UserWithoutCompanyException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommuteQueryService {

    private final CommuteRepository commuteRepository;
    private final AbstractUserRepository abstractUserRepository;
    private final TimeService timeService;

    @Transactional(readOnly = true)
    public Page<CommuteRecordResponse> findTodayCommuteRecords(Long userId, Pageable pageable) {
        LocalDate today = timeService.nowDate();
        AbstractUser user = abstractUserRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Long companyId = getCompanyId(user);

        Page<Commute> commutes = commuteRepository.findAllByDateAndUser_Company_Id(today, companyId, pageable);
        return commutes.map(this::mapToCommuteRecordResponse);
    }

    private Long getCompanyId(AbstractUser abstractUser) {
        if (abstractUser instanceof Admin admin) {
            return Optional.ofNullable(admin.getCompany())
                    .map(Company::getId)
                    .orElseThrow(UserWithoutCompanyException::new);
        } else if (abstractUser instanceof User user) {
            return Optional.ofNullable(user.getCompany())
                    .map(Company::getId)
                    .orElseThrow(UserWithoutCompanyException::new);
        } else {
            throw new SuperAdminAccessException("슈퍼 관리자는 별도의 API를 사용해야 합니다.");
        }
    }

    private CommuteRecordResponse mapToCommuteRecordResponse(Commute commute) {
        CommuteRecordResponse.UserInfo userInfo = new CommuteRecordResponse.UserInfo(
                commute.getUser().getId(),
                commute.getUser().getUserId(),
                commute.getUser().getName()
        );

        CommuteRecordResponse.CommuteInfo commuteInfo = new CommuteRecordResponse.CommuteInfo(
                commute.getId(),
                commute.getDate(),
                commute.getStartedAt(),
                commute.getEndedAt(),
                commute.getAttendanceStatus(),
                commute.getWorkStatus()
        );

        return new CommuteRecordResponse(userInfo, commuteInfo);
    }
}
