package com.mmp.beacon.security.application;

import com.mmp.beacon.security.config.UserDetail;
import com.mmp.beacon.security.presentation.request.CreateUserRequest;
import com.mmp.beacon.security.presentation.request.LoginRequest;
import com.mmp.beacon.security.query.response.UserProfileResponse;
import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.company.domain.repository.CompanyRepository;
import com.mmp.beacon.user.domain.User;
import com.mmp.beacon.user.domain.UserRole;
import com.mmp.beacon.user.domain.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(UserApplicationService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CompanyRepository companyRepository;

    // 사용자 등록 서비스
    public void register(CreateUserRequest userDto, Company company) {
        logger.info("사용자 등록 중: {}", userDto.getUserId());
        try {
            String encPassword = bCryptPasswordEncoder.encode(userDto.getPassword());
            UserRole role = UserRole.valueOf(userDto.getRole().toUpperCase());

            // 현재 인증된 사용자와 역할 가져오기
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                throw new IllegalArgumentException("인증이 필요합니다.");
            }
            User currentUser = ((UserDetail) auth.getPrincipal()).getUser();
            UserRole currentUserRole = currentUser.getRole();

            // 현재 사용자의 회사가 대상 회사와 일치하는지 확인
            if (!currentUser.getCompany().getName().equals(company.getName())) {
                throw new IllegalArgumentException("접근 거부: 다른 회사의 계정을 생성할 수 없습니다.");
            }

            // 현재 사용자가 지정된 역할을 생성할 권한이 있는지 확인
            if (currentUserRole == UserRole.SUPER_ADMIN || (currentUserRole == UserRole.ADMIN && role == UserRole.USER)) {
                User user = new User(userDto.getUserId(), encPassword, userDto.getName(), userDto.getPhone(), userDto.getEmail(), userDto.getPosition(), company);
                user.setRole(role);

                // 데이터베이스에 사용자 저장
                userRepository.save(user);
                logger.info("사용자 등록 성공: {}", userDto.getUserId());
            } else {
                throw new IllegalArgumentException("잘못된 역할 또는 권한 부족");
            }
        } catch (IllegalArgumentException e) {
            logger.error("잘못된 역할 제공됨: {}", userDto.getRole(), e);
            throw new IllegalArgumentException("잘못된 역할 또는 권한 부족: " + userDto.getRole());
        } catch (DataIntegrityViolationException e) {
            logger.error("중복 항목 오류: ", e);
            throw new DataIntegrityViolationException("중복 항목: " + userDto.getUserId());
        } catch (Exception e) {
            logger.error("등록 실패: ", e);
            throw new RuntimeException("등록 실패: " + e.getMessage(), e);
        }
    }

    // 사용자 인증 서비스
    public boolean authenticate(LoginRequest userDto, HttpServletRequest request) {
        logger.info("사용자 인증 중: {}", userDto.getUserId());
        Optional<User> userOpt = userRepository.findByUserId(userDto.getUserId());

        if (userOpt.isPresent() && bCryptPasswordEncoder.matches(userDto.getPassword(), userOpt.get().getPassword())) {
            User user = userOpt.get();
            if (user.getRole() == null) {
                user.setRole(UserRole.USER); // 기본 Role 설정
            }

            Authentication auth = new UsernamePasswordAuthenticationToken(new UserDetail(user), null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            logger.info("사용자 인증 성공: {}", user.getUserId());
            return true;
        }
        logger.warn("사용자 인증 실패: {}", userDto.getUserId());
        return false;
    }

    // 현재 인증된 사용자 정보 반환
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetail) {
            UserDetail userDetail = (UserDetail) auth.getPrincipal();
            return userRepository.findByUserId(userDetail.getUsername())
                    .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
        }
        return null;
    }

    // 특정 사용자 프로필 정보 반환
    public UserProfileResponse getUserProfile(String userId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalArgumentException("인증이 필요합니다.");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 현재 사용자의 회사가 대상 사용자의 회사와 일치하는지 확인
        if (!currentUser.getCompany().getName().equals(user.getCompany().getName())) {
            throw new IllegalArgumentException("접근 거부: 다른 회사의 프로필을 볼 수 없습니다.");
        }

        return new UserProfileResponse(
                user.getUserId(),
                user.getEmail(),
                user.getPosition(),
                user.getName(),
                user.getPhone(),
                user.getCompany().getName(),
                user.getRole().name());
    }
}
