package com.mmp.beacon.security.presentation;

import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.company.domain.repository.CompanyRepository;
import com.mmp.beacon.request.domain.ProfileUpdateRequest;
import com.mmp.beacon.security.application.UserApplicationService;
import com.mmp.beacon.security.config.UserDetail;
import com.mmp.beacon.security.presentation.request.CreateUserRequest;
import com.mmp.beacon.security.presentation.request.LoginRequest;
import com.mmp.beacon.security.query.response.UserProfileResponse;
import com.mmp.beacon.user.domain.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
        try {
            Optional<Company> companyOptional = companyRepository.findByName(userDto.getCompany());
            if (companyOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 회사 이름입니다.");
            }
            Company company = companyOptional.get();
            userApplicationService.register(userDto, company);
            return ResponseEntity.ok("사용자 등록 성공");
        } catch (DataIntegrityViolationException e) {
            logger.error("중복 항목 오류: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록 실패: 중복 항목.");
        } catch (IllegalArgumentException e) {
            logger.error("잘못된 역할 또는 권한 오류: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록 실패: " + e.getMessage());
        } catch (Exception e) {
            logger.error("등록 실패: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록 실패: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest userDto, HttpServletRequest request, HttpServletResponse response) {
        logger.info("로그인 요청 수신: 사용자 ID: {}", userDto.getUserId());
        boolean isAuthenticated = userApplicationService.authenticate(userDto, request);
        if (isAuthenticated) {
            User user = userApplicationService.getCurrentUser();
            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);

                Cookie cookie = new Cookie("JSESSIONID", session.getId());
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setMaxAge(60 * 60);
                response.addCookie(cookie);

                return ResponseEntity.ok("로그인 성공. 사용자 ID: " + user.getUserId());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 자격 증명");
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

    @GetMapping("profile/me")
    public ResponseEntity<?> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetail)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않음");
        }

        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        UserProfileResponse userProfile = new UserProfileResponse(
                userDetail.getUsername(),
                userDetail.getEmail(),
                userDetail.getPosition(),
                userDetail.getName(),
                userDetail.getPhone(),
                userDetail.getCompany(),
                userDetail.getRole()
        );

        return ResponseEntity.ok(userProfile);
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

}
