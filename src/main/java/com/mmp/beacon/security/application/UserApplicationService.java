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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(UserApplicationService.class);

    private final AbstractUserRepository abstractUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CompanyRepository companyRepository;

    private final Map<String, String> refreshTokenStore = new HashMap<>();

    public AbstractUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetail = (CustomUserDetails) auth.getPrincipal();
            return abstractUserRepository.findByUserId(userDetail.getUsername())
                    .orElseThrow(() -> new IllegalStateException("User not found."));
        }
        return null;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String userId) {
        AbstractUser currentUser = getCurrentUser();
        AbstractUser user = abstractUserRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        // 슈퍼 관리자와 관리자가 자신의 프로필을 조회하려고 할 때 예외를 던짐
        if (user.getUserId().equals(currentUser.getUserId()) &&
                (user.getRole() == UserRole.SUPER_ADMIN || user.getRole() == UserRole.ADMIN)) {
            throw new IllegalArgumentException("Profile retrieval not allowed for this role.");
        }

        // 관리자와 사용자는 자신이 속한 회사의 USER만 조회할 수 있음
        Company currentUserCompany = null;
        Company userCompany = null;

        if (currentUser instanceof Admin) {
            currentUserCompany = ((Admin) currentUser).getCompany();
        } else if (currentUser instanceof User) {
            currentUserCompany = ((User) currentUser).getCompany();
        }

        if (user instanceof Admin) {
            userCompany = ((Admin) user).getCompany();
        } else if (user instanceof User) {
            userCompany = ((User) user).getCompany();
        }

        if (currentUserCompany == null || userCompany == null || !currentUserCompany.equals(userCompany)) {
            throw new IllegalArgumentException("Access denied: You can only view users from your own company.");
        }

        if (user.getRole() != UserRole.USER) {
            throw new IllegalArgumentException("Access denied: Only USER profiles can be viewed.");
        }

        // 슈퍼 관리자와 관리자는 오직 USER 역할을 가진 사용자만 조회할 수 있음
        if ((currentUser.getRole() == UserRole.SUPER_ADMIN || currentUser.getRole() == UserRole.ADMIN) &&
                user.getRole() != UserRole.USER) {
            throw new IllegalArgumentException("Access denied: Only USER profiles can be viewed.");
        }

        return createUserProfileResponse(user);
    }

    @Transactional
    public void register(CreateUserRequest userDto) {
        try {
            String encPassword = bCryptPasswordEncoder.encode(userDto.getPassword());
            UserRole role = UserRole.valueOf(userDto.getRole().toUpperCase());

            AbstractUser currentUser = getCurrentUser();
            if (currentUser == null) {
                throw new IllegalArgumentException("Authentication required.");
            }

            Company company = null;

            if (currentUser.getRole() == UserRole.SUPER_ADMIN) {
                // 슈퍼 관리자는 USER 또는 ADMIN 역할만 생성 가능하게 설정
                if (role != UserRole.USER && role != UserRole.ADMIN) {
                    throw new IllegalArgumentException("Invalid role or insufficient permissions: SUPER_ADMIN can only create USER or ADMIN roles.");
                }
                if (userDto.getCompany() == null || userDto.getCompany().isEmpty()) {
                    throw new IllegalArgumentException("Company name cannot be null or empty for SUPER_ADMIN.");
                }
                company = companyRepository.findByName(userDto.getCompany())
                        .orElseThrow(() -> new IllegalArgumentException("Company not found."));
            } else if (currentUser.getRole() == UserRole.ADMIN) {
                // 관리자는 자신이 속한 회사의 사용자만 생성 가능
                if (role != UserRole.USER) {
                    throw new IllegalArgumentException("Invalid role or insufficient permissions: Only USER role can be created by ADMIN.");
                }
                company = ((Admin) currentUser).getCompany();

                // 관리자가 다른 회사의 사용자를 생성하려고 할 때 예외 발생
                if (!company.getName().equals(userDto.getCompany())) {
                    throw new IllegalArgumentException("Admin can only create users within their own company.");
                }
            } else {
                throw new IllegalArgumentException("Insufficient permissions.");
            }


            AbstractUser user = new User(userDto.getUserId(), encPassword, role, company, userDto.getName(), userDto.getEmail(), userDto.getPhone(), userDto.getPosition());
            abstractUserRepository.save(user);
            logger.info("User registration successful: {}", userDto.getUserId());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid role or insufficient permissions: {}", userDto.getRole(), e);
            throw new IllegalArgumentException("Invalid role or insufficient permissions: " + userDto.getRole());
        } catch (DataIntegrityViolationException e) {
            logger.error("Duplicate entry error: ", e);
            throw new DataIntegrityViolationException("Duplicate entry: " + userDto.getUserId());
        } catch (Exception e) {
            logger.error("Registration failed: ", e);
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void registerAdmin(AdminCreateRequest adminDto) {
        try {
            String encPassword = bCryptPasswordEncoder.encode(adminDto.getPassword());
            UserRole role = UserRole.valueOf(adminDto.getRole().toUpperCase());

            AbstractUser currentUser = getCurrentUser();
            if (currentUser == null) {
                throw new IllegalArgumentException("Authentication required.");
            }

            Company company = companyRepository.findByName(adminDto.getCompany())
                    .orElseThrow(() -> new IllegalArgumentException("Company not found."));

            if (role != UserRole.ADMIN) {
                throw new IllegalArgumentException("Invalid role or insufficient permissions: Only ADMIN role can be created.");
            }

            AbstractUser user = new Admin(adminDto.getUserId(), encPassword, role, company);

            abstractUserRepository.save(user);
            logger.info("Admin registration successful: {}", adminDto.getUserId());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid role or insufficient permissions: {}", adminDto.getRole(), e);
            throw new IllegalArgumentException("Invalid role or insufficient permissions: " + adminDto.getRole());
        } catch (DataIntegrityViolationException e) {
            logger.error("Duplicate entry error: ", e);
            throw new DataIntegrityViolationException("Duplicate entry: " + adminDto.getUserId());
        } catch (Exception e) {
            logger.error("Registration failed: ", e);
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }

    public Map<String, String> authenticate(LoginRequest userDto) {
        logger.info("Authenticating user: {}", userDto.getUserId());
        Optional<AbstractUser> userOpt = abstractUserRepository.findByUserId(userDto.getUserId());

        if (userOpt.isPresent() && bCryptPasswordEncoder.matches(userDto.getPassword(), userOpt.get().getPassword())) {
            AbstractUser user = userOpt.get();
            Authentication auth = new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            String accessToken = jwtTokenProvider.generateToken((CustomUserDetails) auth.getPrincipal());
            String refreshToken = jwtTokenProvider.generateRefreshToken((CustomUserDetails) auth.getPrincipal());

            refreshTokenStore.put(user.getUserId(), refreshToken);

            logger.info("Authentication successful: {}", user.getUserId());
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

            return tokens;
        }
        logger.warn("Authentication failed: {}", userDto.getUserId());
        return null;
    }

    public String refreshAccessToken(String refreshToken) {
        if (jwtTokenProvider.validateToken(refreshToken)) {
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            String storedRefreshToken = refreshTokenStore.get(username);

            if (storedRefreshToken != null && storedRefreshToken.equals(refreshToken)) {
                Optional<AbstractUser> userOpt = abstractUserRepository.findByUserId(username);
                if (userOpt.isPresent()) {
                    return jwtTokenProvider.generateToken(new CustomUserDetails(userOpt.get()));
                }
            }
        }
        throw new IllegalArgumentException("Invalid refresh token.");
    }

    @Transactional
    public void deleteUser(String userId) {
        AbstractUser currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalArgumentException("Authentication required.");
        }

        UserRole currentUserRole = currentUser.getRole();
        if (currentUserRole != UserRole.SUPER_ADMIN && currentUserRole != UserRole.ADMIN) {
            throw new IllegalArgumentException("Insufficient permissions: Cannot delete user.");
        }

        AbstractUser userToDelete = abstractUserRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (currentUserRole == UserRole.ADMIN) {
            if (userToDelete.getRole() != UserRole.USER) {
                throw new IllegalArgumentException("Insufficient permissions: Cannot delete this user.");
            }

            // 관리자는 자신이 소속된 회사의 USER만 삭제할 수 있음
            if (!(currentUser instanceof Admin) || !(userToDelete instanceof User)) {
                throw new IllegalArgumentException("Insufficient permissions: Admins can only delete users from their own company.");
            }

            Company currentUserCompany = ((Admin) currentUser).getCompany();
            Company userCompany = ((User) userToDelete).getCompany();
            if (!currentUserCompany.equals(userCompany)) {
                throw new IllegalArgumentException("Access denied: Admins can only delete users from their own company.");
            }
        }

        abstractUserRepository.delete(userToDelete);
        logger.info("User deletion successful: {}", userId);
    }


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
            throw new IllegalArgumentException("Profile retrieval not allowed for this role.");
        }
    }
}
