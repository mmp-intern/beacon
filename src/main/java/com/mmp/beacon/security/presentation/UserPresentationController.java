package com.mmp.beacon.security.presentation;

import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.company.domain.repository.CompanyRepository;
import com.mmp.beacon.security.application.CustomUserDetails;
import com.mmp.beacon.security.application.UserApplicationService;
import com.mmp.beacon.security.presentation.request.CreateUserRequest;
import com.mmp.beacon.security.presentation.request.LoginRequest;
import com.mmp.beacon.security.query.response.UserProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@Validated
@RequiredArgsConstructor
public class UserPresentationController {

    private static final Logger logger = LoggerFactory.getLogger(UserPresentationController.class);

    private final UserApplicationService userApplicationService;
    private final CompanyRepository companyRepository;

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@Valid @RequestBody CreateUserRequest userDto) {
        logger.info("사용자 등록 요청 수신: {}", userDto);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            CustomUserDetails currentUserDetails = (CustomUserDetails) auth.getPrincipal();
            logger.debug("현재 사용자: {}", currentUserDetails.getUsername());
            logger.debug("현재 사용자 역할: {}", currentUserDetails.getUser().getRole().name());
        } else {
            logger.warn("인증되지 않은 사용자");
        }

        try {
            Optional<Company> companyOptional = companyRepository.findByName(userDto.getCompany());
            if (companyOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 회사 이름입니다.");
            }
            userApplicationService.register(userDto);
            return ResponseEntity.ok("사용자 등록 성공");
        } catch (DataIntegrityViolationException e) {
            logger.error("중복 항목 오류: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록 실패: 사용자 ID가 중복되었습니다.");
        } catch (IllegalArgumentException e) {
            logger.error("잘못된 역할 또는 권한 오류: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록 실패: " + e.getMessage());
        } catch (Exception e) {
            logger.error("등록 실패: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록 실패: 생성할 수 없습니다.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest userDto) {
        logger.info("로그인 요청 수신: 사용자 ID: {}", userDto.getUserId());
        Map<String, String> tokens = userApplicationService.authenticate(userDto);
        if (tokens != null) {
            return ResponseEntity.ok(tokens);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok("로그아웃 성공");
    }

    @GetMapping("/profile/me")
    public ResponseEntity<?> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않음");
            }

            CustomUserDetails userDetail = (CustomUserDetails) authentication.getPrincipal();
            UserProfileResponse userProfile = userApplicationService.getUserProfile(userDetail.getUsername());

            return ResponseEntity.ok(userProfile);
        } catch (IllegalStateException e) {
            logger.error("Error in getProfile: ", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable("userId") String userId) {
        try {
            UserProfileResponse userProfile = userApplicationService.getUserProfile(userId);
            return ResponseEntity.ok(userProfile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") String userId) {
        logger.info("사용자 삭제 요청 수신: {}", userId);
        try {
            userApplicationService.deleteUser(userId);
            return ResponseEntity.ok("사용자 삭제 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("사용자 삭제 실패: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 삭제 실패: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh Token이 필요합니다.");
        }
        try {
            String newAccessToken = userApplicationService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(newAccessToken);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 Refresh Token입니다.");
        }
    }
}
