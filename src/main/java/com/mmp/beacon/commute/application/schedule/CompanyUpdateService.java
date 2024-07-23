package com.mmp.beacon.commute.application.schedule;

import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.company.domain.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회사 정보를 업데이트하고, 스케줄 작업을 재등록하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class CompanyUpdateService {

    private final CompanyScheduleService companyScheduleService;
    private final CompanyRepository companyRepository;

    @Transactional
    public void updateCompany(Company company) {
        companyRepository.save(company);
        companyScheduleService.rescheduleCompanyTasks(company);
    }
}
