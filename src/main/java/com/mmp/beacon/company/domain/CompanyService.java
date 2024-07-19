package com.mmp.beacon.company.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public Company findCompanyById(String companyName) {
        Optional<Company> optionalCompany = companyRepository.findByName(companyName); // 수정된 메소드 이름
        if (optionalCompany.isPresent()) {
            return optionalCompany.get();
        } else {
            // 회사가 존재하지 않으면 새로운 회사 생성
            Company newCompany = new Company();
            newCompany.setCompanyName(companyName);
            return companyRepository.save(newCompany);
        }
    }
}