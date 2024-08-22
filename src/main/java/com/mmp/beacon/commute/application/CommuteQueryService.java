package com.mmp.beacon.commute.application;

import com.mmp.beacon.commute.application.command.CommuteDailyCommand;
import com.mmp.beacon.commute.application.command.CommutePeriodCommand;
import com.mmp.beacon.commute.domain.Commute;
import com.mmp.beacon.commute.domain.repository.CommuteRepository;
import com.mmp.beacon.commute.exception.CommuteNotFoundException;
import com.mmp.beacon.commute.query.response.CommuteRecordInfo;
import com.mmp.beacon.commute.query.response.CommuteRecordResponse;
import com.mmp.beacon.commute.query.response.CommuteStatisticsResponse;
import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.company.domain.repository.CompanyRepository;
import com.mmp.beacon.company.exception.CompanyNotFoundException;
import com.mmp.beacon.user.domain.AbstractUser;
import com.mmp.beacon.user.domain.Admin;
import com.mmp.beacon.user.domain.User;
import com.mmp.beacon.user.domain.repository.AbstractUserRepository;
import com.mmp.beacon.user.exception.UserNotFoundException;
import com.mmp.beacon.user.exception.UserWithoutCompanyException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommuteQueryService {

    private static final Long FIXED_COMPANY_ID = 1L;
    private final CommuteRepository commuteRepository;
    private final CompanyRepository companyRepository;
    private final AbstractUserRepository abstractUserRepository;
    private final TimeService timeService;

    @Transactional(readOnly = true)
    public Page<CommuteRecordResponse> findCommuteRecordsByDate(CommuteDailyCommand command) {
        AbstractUser user = abstractUserRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);
        Long companyId = getCompanyId(user);
        LocalDate date = Optional.ofNullable(command.date()).orElse(timeService.nowDate());

        Page<CommuteRecordInfo> commuteInfos = commuteRepository.findByCompanyIdAndDateAndSearchTerm(
                companyId, date, command.searchTerm(), command.searchBy(), command.pageable()
        );

        return commuteInfos.map(this::mapToCommuteRecordResponse);
    }

    @Transactional(readOnly = true)
    public Page<CommuteStatisticsResponse> findCommuteStatistics(CommutePeriodCommand command) {
        AbstractUser user = abstractUserRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);
        Long companyId = getCompanyId(user);

        LocalDate currentDate = timeService.nowDate();
        LocalDate startDate = Optional.ofNullable(command.startDate()).orElse(currentDate.withDayOfMonth(1));
        LocalDate endDate = Optional.ofNullable(command.endDate()).orElse(YearMonth.from(currentDate).atEndOfMonth());

        return commuteRepository.findCommuteStatistics(companyId, startDate, endDate,
                command.searchTerm(), command.searchBy(), command.pageable());
    }

    @Transactional(readOnly = true)
    public CommuteRecordResponse findCommuteRecord(Long userId, Long commuteId) {
        AbstractUser user = abstractUserRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return commuteRepository.findByIdAndIsDeletedFalse(commuteId)
                .map(this::mapToCommuteRecordResponse)
                .orElseThrow(CommuteNotFoundException::new);
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
            return companyRepository.findByIdAndIsDeletedFalse(FIXED_COMPANY_ID)
                    .map(Company::getId)
                    .orElseThrow(CompanyNotFoundException::new);
        }
    }

    private CommuteRecordResponse mapToCommuteRecordResponse(CommuteRecordInfo info) {
        CommuteRecordResponse.UserInfo userInfo = new CommuteRecordResponse.UserInfo(
                info.getUserId(),
                info.getUserLoginId(),
                info.getUserName()
        );

        CommuteRecordResponse.CommuteInfo commuteInfo = (info.getCommuteId() != null) ? new CommuteRecordResponse.CommuteInfo(
                info.getCommuteId(),
                info.getDate(),
                info.getStartedAt(),
                info.getEndedAt(),
                info.getAttendanceStatus(),
                info.getWorkStatus()
        ) : null;

        return new CommuteRecordResponse(userInfo, commuteInfo);
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

    /*    @Transactional(readOnly = true)
    public Page<CommuteRecordResponse> findCommuteRecords(CommutePeriodCommand command) {
        AbstractUser user = abstractUserRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);
        Long companyId = getCompanyId(user);

        LocalDate currentDate = timeService.nowDate();
        LocalDate startDate = Optional.ofNullable(command.startDate()).orElse(currentDate.withDayOfMonth(1));
        LocalDate endDate = Optional.ofNullable(command.endDate()).orElse(YearMonth.from(currentDate).atEndOfMonth());

        Page<Commute> commutes = commuteRepository.findByCompanyIdAndPeriodAndSearchTerm(
                companyId, startDate, endDate, command.searchTerm(), command.searchBy(), command.pageable());

        return commutes.map(commute -> mapToCommuteRecordResponse(commute.getUser(), commute));
    }*/

    /*    @Transactional(readOnly = true)
    public Page<CommuteRecordResponse> findTodayCommuteRecords(Long userId, Pageable pageable) {
        LocalDate today = timeService.nowDate();
        AbstractUser user = abstractUserRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Long companyId = getCompanyId(user);

        return userRepository.findByCompanyId(companyId, pageable).map(userPage -> {
            Optional<Commute> commute = commuteRepository.findByUserAndDate(userPage, today);
            return mapToCommuteRecordResponse(userPage, commute.orElse(null));
        });
    }*/

    /*private CommuteStatisticsResponse.CommuteStatistics calculateStatistics(User user, LocalDate startDate, LocalDate endDate) {
        int presentDays = (int) commuteRepository.countByUserAndDateBetweenAndAttendanceStatus(
                user, startDate, endDate, AttendanceStatus.PRESENT);
        int lateDays = (int) commuteRepository.countByUserAndDateBetweenAndAttendanceStatus(
                user, startDate, endDate, AttendanceStatus.LATE);
        int absentDays = (int) commuteRepository.countByUserAndDateBetweenAndAttendanceStatus(
                user, startDate, endDate, AttendanceStatus.ABSENT);
        int totalDays = presentDays + lateDays + absentDays;
        return new CommuteStatisticsResponse.CommuteStatistics(presentDays, lateDays, absentDays, totalDays);
    }*/

    /*private CommuteRecordResponse mapToCommuteRecordResponse(User user, Commute commute) {
        CommuteRecordResponse.UserInfo userInfo = new CommuteRecordResponse.UserInfo(
                user.getId(),
                user.getUserId(),
                user.getName()
        );

        CommuteRecordResponse.CommuteInfo commuteInfo = (commute != null) ? new CommuteRecordResponse.CommuteInfo(
                commute.getId(),
                commute.getDate(),
                commute.getStartedAt(),
                commute.getEndedAt(),
                commute.getAttendanceStatus(),
                commute.getWorkStatus()
        ) : null;

        return new CommuteRecordResponse(userInfo, commuteInfo);
    }*/
}
