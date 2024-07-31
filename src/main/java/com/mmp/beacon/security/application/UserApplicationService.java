package com.mmp.beacon.security.application;

import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.company.domain.repository.CompanyRepository;
import com.mmp.beacon.security.presentation.request.CreateUserRequest;
import com.mmp.beacon.security.presentation.request.LoginRequest;
import com.mmp.beacon.security.provider.JwtTokenProvider;
import com.mmp.beacon.security.query.response.UserProfileResponse;
import com.mmp.beacon.user.domain.*;
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

import java.util.Collections;
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
            AbstractUser user = abstractUserRepository.findByUserId(userDetail.getUsername())
                    .orElseThrow(() -> new IllegalStateException("User not found."));
            if (user.getCompany() == null) {
                logger.error("User {} does not belong to any company.", user.getUserId());
                throw new IllegalStateException("Authenticated user does not belong to any company.");
            }
            return user;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String userId) {
        AbstractUser currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalArgumentException("Authentication required.");
        }

        AbstractUser user = abstractUserRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        if (!currentUser.getUserId().equals(userId)) {
            if (currentUser.getCompany() == null || user.getCompany() == null ||
                    !currentUser.getCompany().getId().equals(user.getCompany().getId())) {
                logger.error("Access denied: 현재 사용자 회사 ID = {}, 조회하려는 사용자 회사 ID = {}",
                        currentUser.getCompany() != null ? currentUser.getCompany().getId() : "null",
                        user.getCompany() != null ? user.getCompany().getId() : "null");
                throw new IllegalArgumentException("다른 회사의 프로필을 볼 수 없습니다.");
            }
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

            Company company = companyRepository.findByName(userDto.getCompanyName())
                    .orElseThrow(() -> new IllegalArgumentException("Company not found."));

            if (!currentUser.getCompany().getId().equals(company.getId())) {
                throw new IllegalArgumentException("Access denied: Cannot create account for another company.");
            }

            if (currentUser.getRole() == UserRole.SUPER_ADMIN ||
                    (currentUser.getRole() == UserRole.ADMIN && role == UserRole.USER)) {
                AbstractUser user;
                switch (role) {
                    case SUPER_ADMIN:
                        user = new SuperAdmin(userDto.getUserId(), encPassword, role, company);
                        break;
                    case ADMIN:
                        user = new Admin(userDto.getUserId(), encPassword, role, company);
                        break;
                    case USER:
                        user = new User(userDto.getUserId(), encPassword, role, company, userDto.getName(), userDto.getPhone(), userDto.getEmail(), userDto.getPosition());
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid role.");
                }
                abstractUserRepository.save(user);
                logger.info("User registration successful: {}", userDto.getUserId());
            } else {
                throw new IllegalArgumentException("Invalid role or insufficient permissions.");
            }
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

        if (!currentUser.getCompany().getId().equals(userToDelete.getCompany().getId())) {
            throw new IllegalArgumentException("Access denied: Cannot delete user from another company.");
        }

        if (currentUserRole == UserRole.ADMIN && userToDelete.getRole() != UserRole.USER) {
            throw new IllegalArgumentException("Insufficient permissions: Cannot delete this user.");
        }

        abstractUserRepository.delete(userToDelete);
        logger.info("User deletion successful: {}", userId);
    }

    private UserProfileResponse createUserProfileResponse(AbstractUser user) {
        String email = "";
        String name = "";
        String position = "";
        String phone = "";

        if (user instanceof User) {
            email = ((User) user).getEmail();
            name = ((User) user).getName();
            position = ((User) user).getPosition();
            phone = ((User) user).getPhone();
        }

        return new UserProfileResponse(
                user.getUserId(),
                email,
                position,
                name,
                phone,
                user.getCompany() != null ? user.getCompany().getName() : "",
                user.getRole().name());
    }
}
