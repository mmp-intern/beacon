package com.mmp.beacon.commute.application;

import com.mmp.beacon.commute.domain.Commute;
import com.mmp.beacon.commute.domain.repository.CommuteRepository;
import com.mmp.beacon.commute.exception.CommuteNotFoundException;
import com.mmp.beacon.commute.presentation.request.CommuteModificationRequest;
import com.mmp.beacon.user.domain.AbstractUser;
import com.mmp.beacon.user.domain.Admin;
import com.mmp.beacon.user.domain.SuperAdmin;
import com.mmp.beacon.user.domain.repository.AbstractUserRepository;
import com.mmp.beacon.user.exception.UserNotFoundException;
import com.mmp.beacon.user.exception.UserWithoutPermissionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommuteModificationService {

    private final CommuteRepository commuteRepository;
    private final AbstractUserRepository abstractUserRepository;

    /**
     * 출퇴근 기록을 수정합니다.
     *
     * @param commuteId 수정할 출퇴근 기록의 ID
     * @param request   수정할 출퇴근 기록 정보
     */
    @Transactional
    public void modifyCommute(Long userId, Long commuteId, CommuteModificationRequest request) {
        AbstractUser user = abstractUserRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!isAdminOrSuperAdmin(user)) {
            throw new UserWithoutPermissionException("직원은 근태 기록을 수정할 권한이 없습니다.");
        }

        Commute commute = commuteRepository.findByIdAndIsDeletedFalse(commuteId)
                .orElseThrow(CommuteNotFoundException::new);

        commute.updateTimestampByAdmin(request.startedAt(), request.endedAt());
        commute.updateAttendanceStatus(request.attendanceStatus());
        commute.updateWorkStatus(request.workStatus());

        commuteRepository.save(commute);
    }

    private boolean isAdminOrSuperAdmin(AbstractUser user) {
        return user instanceof Admin || user instanceof SuperAdmin;
    }
}
