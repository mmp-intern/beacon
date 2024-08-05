package com.mmp.beacon.security.application;

import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.company.domain.repository.CompanyRepository;
import com.mmp.beacon.security.presentation.request.AdminCreateRequest;
import com.mmp.beacon.security.presentation.request.CreateUserRequest;
import com.mmp.beacon.security.presentation.request.LoginRequest;
import com.mmp.beacon.security.provider.JwtTokenProvider;
import com.mmp.beacon.security.query.response.UserProfileResponse;
import com.mmp.beacon.user.domain.AbstractUser;
import com.mmp.beacon.user.domain.Admin;
import com.mmp.beacon.user.domain.User;
import com.mmp.beacon.user.domain.UserRole;
import com.mmp.beacon.user.domain.repository.AbstractUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final AbstractUserRepository abstractUserRepository; // 사용자 저장소에 접근하기 위한 객체
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // 비밀번호 암호화를 위한 객체
    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 생성을 위한 객체
    private final CompanyRepository companyRepository; // 회사 저장소에 접근하기 위한 객체

    private final Map<String, String> refreshTokenStore = new HashMap<>(); // 리프레시 토큰 저장소

    // 현재 인증된 사용자 정보를 반환하는 메서드
    public AbstractUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); // 현재 인증 정보를 가져옴
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetail = (CustomUserDetails) auth.getPrincipal(); // 사용자 세부 정보를 가져옴
            return abstractUserRepository.findByUserId(userDetail.getUsername())
                    .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다.")); // 사용자 ID로 사용자 정보를 조회
        }
        return null; // 인증되지 않은 경우 null 반환
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String userId) {
        AbstractUser currentUser = getCurrentUser(); // 현재 사용자 정보를 가져옴
        AbstractUser user = abstractUserRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다.")); // 사용자 ID로 사용자 정보를 조회

        // 슈퍼 어드민일 경우에도 USER 역할의 사용자만 조회할 수 있음
        if (currentUser.getRole() == UserRole.SUPER_ADMIN) {
            if (user.getRole() != UserRole.USER) {
                throw new IllegalArgumentException("사용자만 조회할 수 있습니다.");
            }
            return createUserProfileResponse(user);
        }

        // 권한 체크
        if (user.getUserId().equals(currentUser.getUserId()) &&
                (user.getRole() == UserRole.SUPER_ADMIN || user.getRole() == UserRole.ADMIN)) {
            throw new IllegalArgumentException("[권한 부족] 조회할 수 없습니다.");
        }

        // 같은 회사인지 체크
        Company currentUserCompany = getUserCompany(currentUser);
        Company userCompany = getUserCompany(user);

        if (currentUserCompany == null || userCompany == null || !currentUserCompany.equals(userCompany)) {
            throw new IllegalArgumentException("같은 회사의 사용자만 조회할 수 있습니다.");
        }

        // 역할 체크
        if (user.getRole() != UserRole.USER) {
            throw new IllegalArgumentException("USER 프로필만 조회할 수 있습니다.");
        }

        return createUserProfileResponse(user); // 사용자 프로필 응답 생성
    }


    // 사용자의 회사 정보를 반환하는 메서드
    private Company getUserCompany(AbstractUser user) {
        if (user instanceof Admin) {
            return ((Admin) user).getCompany(); // Admin 사용자의 회사 정보를 반환
        } else if (user instanceof User) {
            return ((User) user).getCompany(); // User 사용자의 회사 정보를 반환
        }
        return null; // 회사 정보가 없는 경우 null 반환
    }

    // 새로운 사용자를 등록하는 메서드
    @Transactional
    public void register(CreateUserRequest userDto) {
        try {
            String encPassword = bCryptPasswordEncoder.encode(userDto.getPassword()); // 비밀번호 암호화
            UserRole role = UserRole.USER; // 역할을 항상 USER으로 지정

            AbstractUser currentUser = getCurrentUser(); // 현재 사용자 정보를 가져옴
            if (currentUser == null) {
                throw new IllegalArgumentException("인증이 필요합니다.");
            }

            Company company = validateAndRetrieveCompany(userDto, currentUser, role); // 회사 정보를 검증 및 조회

            AbstractUser user = new User(userDto.getUserId(), encPassword, role, company, userDto.getName(), userDto.getEmail(), userDto.getPhone(), userDto.getPosition()); // 새로운 사용자 객체 생성
            abstractUserRepository.save(user); // 사용자 저장
            log.info("사용자 등록 성공: {}", userDto.getUserId());
        } catch (DataIntegrityViolationException e) {
            log.error("중복된 항목 오류: ", e);
            throw new DataIntegrityViolationException("중복된 항목 오류: " + userDto.getUserId());
        } catch (Exception e) {
            log.error("등록 실패: ", e);
            throw new RuntimeException("등록 실패: " + e.getMessage(), e);
        }
    }

    // 회사 정보를 검증 및 조회하는 메서드
    private Company validateAndRetrieveCompany(CreateUserRequest userDto, AbstractUser currentUser, UserRole role) {
        Company company;
        if (currentUser.getRole() == UserRole.SUPER_ADMIN) {
            if (role != UserRole.USER && role != UserRole.ADMIN) {
                throw new IllegalArgumentException("권한이 부족합니다.");
            }
            if (userDto.getCompany() == null || userDto.getCompany().isEmpty()) {
                throw new IllegalArgumentException("회사 이름을 비워둘 수 없습니다.");
            }
            company = companyRepository.findByName(userDto.getCompany())
                    .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));
        } else if (currentUser.getRole() == UserRole.ADMIN) {
            if (role != UserRole.USER) {
                throw new IllegalArgumentException("권한이 부족합니다.");
            }
            company = ((Admin) currentUser).getCompany();
            if (!company.getName().equals(userDto.getCompany())) {
                throw new IllegalArgumentException("같은 회사 사용자만 생성할 수 있습니다.");
            }
        } else {
            throw new IllegalArgumentException("권한 부족.");
        }
        return company;
    }

    // 새로운 관리자를 등록하는 메서드
    @Transactional
    public void registerAdmin(AdminCreateRequest adminDto) {
        try {
            String encPassword = bCryptPasswordEncoder.encode(adminDto.getPassword()); // 비밀번호 암호화
            UserRole role = UserRole.ADMIN; // 역할을 항상 ADMIN으로 지정

            AbstractUser currentUser = getCurrentUser(); // 현재 사용자 정보를 가져옴
            if (currentUser == null) {
                throw new IllegalArgumentException("인증이 필요합니다.");
            }

            Company company = companyRepository.findByName(adminDto.getCompany())
                    .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다.")); // 회사 정보 조회

            AbstractUser user = new Admin(adminDto.getUserId(), encPassword, role, company); // 새로운 관리자 객체 생성

            abstractUserRepository.save(user); // 관리자 저장
            log.info("관리자 등록 성공: {}", adminDto.getUserId());
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("회사를 찾을 수 없습니다.")) {
                log.error("회사가 존재하지 않습니다: {}", adminDto.getCompany(), e);
                throw new IllegalArgumentException("회사가 존재하지 않습니다: " + adminDto.getCompany());
            } else {
                log.error("유효하지 않은 권한 또는 권한 부족: {}", adminDto.getRole(), e);
                throw new IllegalArgumentException("유효하지 않은 권한 또는 권한 부족: " + adminDto.getRole());
            }
        } catch (DataIntegrityViolationException e) {
            log.error("중복된 항목 오류: ", e);
            throw new DataIntegrityViolationException("중복된 항목 오류: " + adminDto.getUserId());
        } catch (Exception e) {
            log.error("등록 실패: ", e);
            throw new RuntimeException("등록 실패: " + e.getMessage(), e);
        }
    }

    // 사용자를 인증하는 메서드
    public Map<String, String> authenticate(LoginRequest userDto) {
        log.info("사용자 인증 중: {}", userDto.getUserId());
        Optional<AbstractUser> userOpt = abstractUserRepository.findByUserId(userDto.getUserId()); // 사용자 ID로 사용자 정보를 조회

        if (userOpt.isPresent() && bCryptPasswordEncoder.matches(userDto.getPassword(), userOpt.get().getPassword())) {
            AbstractUser user = userOpt.get();
            Authentication auth = new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), null, user.getAuthorities()); // 인증 토큰 생성
            SecurityContextHolder.getContext().setAuthentication(auth); // 인증 정보를 보안 컨텍스트에 설정

            String accessToken = jwtTokenProvider.generateToken((CustomUserDetails) auth.getPrincipal()); // 액세스 토큰 생성
            String refreshToken = jwtTokenProvider.generateRefreshToken((CustomUserDetails) auth.getPrincipal()); // 리프레시 토큰 생성

            refreshTokenStore.put(user.getUserId(), refreshToken); // 리프레시 토큰 저장

            log.info("인증 성공: {}", user.getUserId());
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

            return tokens;
        }
        log.warn("인증 실패: {}", userDto.getUserId());
        return null;
    }

    // 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급하는 메서드
    public String refreshAccessToken(String refreshToken) {
        if (jwtTokenProvider.validateToken(refreshToken)) {
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken); // 리프레시 토큰에서 사용자 이름을 추출
            String storedRefreshToken = refreshTokenStore.get(username);

            if (storedRefreshToken != null && storedRefreshToken.equals(refreshToken)) {
                Optional<AbstractUser> userOpt = abstractUserRepository.findByUserId(username);
                if (userOpt.isPresent()) {
                    return jwtTokenProvider.generateToken(new CustomUserDetails(userOpt.get())); // 새로운 액세스 토큰 발급
                }
            }
        }
        throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
    }

    // 사용자를 삭제하는 메서드
    @Transactional
    public void deleteUser(String userId) {
        AbstractUser currentUser = getCurrentUser(); // 현재 사용자 정보를 가져옴
        if (currentUser == null) {
            throw new IllegalArgumentException("인증이 필요합니다.");
        }

        UserRole currentUserRole = currentUser.getRole(); // 현재 사용자의 역할을 가져옴
        if (currentUserRole != UserRole.SUPER_ADMIN && currentUserRole != UserRole.ADMIN) {
            throw new IllegalArgumentException("권한이 부족합니다: 사용자를 삭제할 수 없습니다.");
        }

        AbstractUser userToDelete = abstractUserRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다.")); // 사용자 ID로 삭제할 사용자 정보를 조회

        if (currentUserRole == UserRole.ADMIN) {
            if (userToDelete.getRole() != UserRole.USER) {
                throw new IllegalArgumentException("권한이 부족합니다: 이 사용자를 삭제할 수 없습니다.");
            }

            if (!isSameCompany(currentUser, userToDelete)) {
                throw new IllegalArgumentException("접근 거부: 같은 회사의 사용자만 삭제할 수 있습니다.");
            }
        }

        abstractUserRepository.delete(userToDelete); // 사용자 삭제
        log.info("사용자 삭제 성공: {}", userId);
    }

    // 두 사용자가 같은 회사인지 확인하는 메서드
    private boolean isSameCompany(AbstractUser currentUser, AbstractUser userToDelete) {
        Company currentUserCompany = getUserCompany(currentUser);
        Company userCompany = getUserCompany(userToDelete);
        return currentUserCompany.equals(userCompany); // 같은 회사인지 확인
    }

    // 사용자 프로필 응답을 생성하는 메서드
    private UserProfileResponse createUserProfileResponse(AbstractUser user) {
        if (user instanceof User) {
            User specificUser = (User) user;
            return new UserProfileResponse(
                    user.getUserId(),
                    specificUser.getEmail(),
                    specificUser.getPosition(),
                    specificUser.getName(),
                    specificUser.getPhone(),
                    specificUser.getCompany().getName(),
                    user.getRole().name()
            );
        } else {
            throw new IllegalArgumentException("프로필 조회가 허용되지 않습니다.");
        }
    }
}
