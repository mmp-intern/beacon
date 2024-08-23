package com.mmp.beacon.security.application;

import com.mmp.beacon.beacon.domain.repository.BeaconRepository;
import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.company.domain.repository.CompanyRepository;
import com.mmp.beacon.security.presentation.request.AdminCreateRequest;
import com.mmp.beacon.security.presentation.request.CreateUserRequest;
import com.mmp.beacon.security.presentation.request.LoginRequest;
import com.mmp.beacon.security.presentation.request.UpdateUserRequest;
import com.mmp.beacon.security.provider.JwtTokenProvider;
import com.mmp.beacon.security.query.response.UserProfileResponse;
import com.mmp.beacon.user.domain.*;
import com.mmp.beacon.user.domain.repository.AbstractUserRepository;
import com.mmp.beacon.user.domain.repository.UserRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.mmp.beacon.beacon.domain.Beacon;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private static final Long FIXED_COMPANY_ID = 1L; // 고정된 회사 ID

    private final UserRepository userRepository;
    private final AbstractUserRepository abstractUserRepository; // 사용자 저장소에 접근하기 위한 객체
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // 비밀번호 암호화를 위한 객체
    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 생성을 위한 객체
    private final CompanyRepository companyRepository; // 회사 저장소에 접근하기 위한 객체
    private final BeaconRepository beaconRepository; // 회사 저장소에 접근하기 위한 객체
    private final Map<String, String> refreshTokenStore = new HashMap<>(); // 리프레시 토큰 저장소

    // 현재 인증된 사용자 정보를 반환하는 메서드
    public AbstractUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); // 현재 인증 정보를 가져옴
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetail = (CustomUserDetails) auth.getPrincipal(); // 사용자 세부 정보를 가져옴
            return abstractUserRepository.findByUserIdAndIsDeletedFalse(userDetail.getUsername())
                    .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다.")); // 사용자 ID로 사용자 정보를 조회
        }
        return null; // 인증되지 않은 경우 null 반환
    }

    @Transactional
    public void updateUserProfile(String userId, UpdateUserRequest request) {
        AbstractUser user = abstractUserRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user instanceof User) {
            Beacon beacon = request.getMacAddr() != null
                    ? beaconRepository.findByMacAddrAndIsDeletedFalse(request.getMacAddr())
                    .filter(b -> b.getUser() == null || b.getUser().equals(user))
                    .orElse(null)
                    : null;

            ((User) user).updateProfile(
                    request.getName(),
                    request.getEmail(),
                    request.getPhone(),
                    request.getPosition(),
                    beacon
            );
            abstractUserRepository.save(user);
        } else {
            throw new IllegalArgumentException("User type is not supported for profile update");
        }
    }


    @Transactional(readOnly = true)
    public Page<UserProfileResponse> getAllUsers(int page, int size, String searchTerm, String searchBy) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;

        if (searchTerm != null && !searchTerm.isEmpty()) {
            if ("id".equalsIgnoreCase(searchBy)) {
                userPage = userRepository.findByUserIdContainingAndIsDeletedFalse(searchTerm, pageable);
            } else if ("name".equalsIgnoreCase(searchBy)) {
                userPage = userRepository.findByNameContainingAndIsDeletedFalse(searchTerm, pageable);
            } else {
                throw new IllegalArgumentException("Invalid searchBy parameter. Must be 'id' or 'name'.");
            }
        } else {
            userPage = userRepository.findAllByIsDeletedFalse(pageable);
        }

        return userPage.map(this::createUserProfileResponse);
    }


    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String userId) {
        AbstractUser currentUser = getCurrentUser(); // 현재 사용자 정보를 가져옴
        AbstractUser user = abstractUserRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UsernameNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다.")); // 사용자 ID로 사용자 정보를 조회

        // 조회하는 사람 A, 조회되는 사람 B
        // A와 B가 동일 인물일 경우 조회 가능
        if (currentUser.getUserId().equals(user.getUserId())) {
            return createUserProfileResponse(user);
        }

        // A가 SuperAdmin일 경우 조회 가능
        if (currentUser.getRole() == UserRole.SUPER_ADMIN) {
            return createUserProfileResponse(user);
        }

        // A가 Admin/User일 경우 B가 User이고 A와 B가 같은 회사일 경우 조회 가능
        if (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.USER) {
            if (user.getRole() == UserRole.USER) {
                Company currentUserCompany = getUserCompany(currentUser);
                Company userCompany = getUserCompany(user);

                if (currentUserCompany != null && userCompany != null && currentUserCompany.equals(userCompany)) {
                    return createUserProfileResponse(user);
                }
            }
        }
        throw new IllegalArgumentException("같은 회사의 사용자만 조회할 수 있습니다.");
    }

    // 사용자의 회사 정보를 반환하는 메서드
    private Company getUserCompany(AbstractUser user) {
        return companyRepository.findByIdAndIsDeletedFalse(FIXED_COMPANY_ID)
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));
    }

    @Transactional
    public void register(CreateUserRequest userDto) {
        try {
            String encPassword = bCryptPasswordEncoder.encode(userDto.getPassword());
            UserRole role = UserRole.USER;

            AbstractUser currentUser = getCurrentUser();
            if (currentUser == null) {
                throw new IllegalArgumentException("인증이 필요합니다.");
            }

            // 고정된 회사 정보를 가져옴
            Company company = companyRepository.findByIdAndIsDeletedFalse(FIXED_COMPANY_ID)
                    .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));

            // 사용자 생성 시 회사 정보 포함
            AbstractUser user = new User(userDto.getUserId(), encPassword, role, company, userDto.getName(), userDto.getEmail(), userDto.getPhone(), userDto.getPosition());
            abstractUserRepository.save(user);

            if (userDto.getBeaconId() != null && !userDto.getBeaconId().isEmpty()) {
                Beacon beacon = beaconRepository.findByIdAndIsDeletedFalse(Long.parseLong(userDto.getBeaconId()))
                        .filter(b -> b.getUser() == null || b.getUser().equals(user))
                        .orElseThrow(() -> new IllegalArgumentException("비콘을 찾을 수 없습니다."));

                // 비콘과 사용자 연결
                beacon.assignUser((User) user);
                beaconRepository.save(beacon);
            }

            log.info("사용자 등록 성공: {}", userDto.getUserId());
        } catch (DataIntegrityViolationException e) {
            log.error("중복된 항목 오류: ", e);
            throw new DataIntegrityViolationException("중복된 항목 오류: " + userDto.getUserId());
        } catch (Exception e) {
            log.error("등록 실패: ", e);
            throw new RuntimeException("등록 실패: " + e.getMessage(), e);
        }
    }


    // 새로운 관리자를 등록하는 메서드
    @Transactional
    public void registerAdmin(AdminCreateRequest adminDto) {
        String encPassword = bCryptPasswordEncoder.encode(adminDto.getPassword()); // 비밀번호 암호화
        UserRole role = UserRole.ADMIN; // 역할을 항상 ADMIN으로 지정

        AbstractUser currentUser = getCurrentUser(); // 현재 사용자 정보를 가져옴
        if (currentUser == null) {
            throw new IllegalArgumentException("인증이 필요합니다.");
        }

        Company company = companyRepository.findByIdAndIsDeletedFalse(FIXED_COMPANY_ID)
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다.")); // 고정된 회사 ID로 회사 조회

        AbstractUser user = new Admin(adminDto.getUserId(), encPassword, role, company); // 새로운 관리자 객체 생성

        abstractUserRepository.save(user); // 관리자 저장
        log.info("관리자 등록 성공: {}", adminDto.getUserId());
    }

    // 사용자를 인증하는 메서드
    @Transactional
    public Map<String, String> authenticate(LoginRequest userDto) {
        log.info("사용자 인증 중: {}", userDto.getUserId());
        // is_deleted가 false인 사용자만 조회
        Optional<AbstractUser> userOpt = abstractUserRepository.findByUserIdAndIsDeletedFalse(userDto.getUserId());

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
                Optional<AbstractUser> userOpt = abstractUserRepository.findByUserIdAndIsDeletedFalse(username);
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
        log.info("Current User: {}", currentUser.getUserId());
        log.info("Deleting User with ID: {}", userId);  // 여기서 전달된 userId를 로그로 확인합니다.
        if (currentUser == null) {
            throw new IllegalArgumentException("인증이 필요합니다.");
        }

        UserRole currentUserRole = currentUser.getRole(); // 현재 사용자의 역할을 가져옴
        if (currentUserRole != UserRole.SUPER_ADMIN && currentUserRole != UserRole.ADMIN) {
            throw new IllegalArgumentException("권한이 부족합니다: 사용자를 삭제할 수 없습니다.");
        }

        AbstractUser userToDelete = abstractUserRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다.")); // 사용자 ID로 삭제할 사용자 정보를 조회
        log.info("삭제 요청된 사용자 ID: {}", userId);
        if (currentUserRole == UserRole.ADMIN) {
            if (userToDelete.getRole() != UserRole.USER) {
                throw new IllegalArgumentException("권한이 부족합니다: 이 사용자를 삭제할 수 없습니다.");
            }
        }

        userToDelete.delete(); // 소프트 삭제
        abstractUserRepository.save(userToDelete);
        log.info("사용자 삭제 성공: {}", userId);
    }

    // 사용자 프로필 응답을 생성하는 메서드
    private UserProfileResponse createUserProfileResponse(AbstractUser user) {
        Beacon beacon = null;
        if (user instanceof User) {
            User specificUser = (User) user;

            // Fetch the beacon associated with the user, if any
            Optional<Beacon> beaconOpt = beaconRepository.findByUserAndIsDeletedFalse(specificUser);
            if (beaconOpt.isPresent()) {
                beacon = beaconOpt.get();
            }

            return new UserProfileResponse(
                    user.getId(),
                    user.getUserId(),
                    specificUser.getEmail(),
                    specificUser.getPosition(),
                    specificUser.getName(),
                    specificUser.getPhone(),
                    FIXED_COMPANY_ID,
                    specificUser.getCompany().getName(),
                    user.getRole().name(),
                    beacon != null ? beacon.getId() : null,  // Beacon ID
                    beacon != null ? List.of(beacon.getMacAddr()) : null  // Beacon MAC Address
            );
        } else if (user instanceof Admin) {
            Admin specificAdmin = (Admin) user;
            return new UserProfileResponse(
                    user.getId(),
                    user.getUserId(),
                    null,
                    null,
                    null,
                    null,
                    FIXED_COMPANY_ID,
                    specificAdmin.getCompany().getName(),
                    user.getRole().name(),
                    null,
                    null
            );
        } else if (user instanceof SuperAdmin) {
            return new UserProfileResponse(
                    user.getId(),
                    user.getUserId(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    user.getRole().name(),
                    null,
                    null
            );
        } else {
            return null; // Return null if the user type is unknown
        }
    }
}
