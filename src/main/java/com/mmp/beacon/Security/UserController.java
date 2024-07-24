package com.mmp.beacon.Security;

import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.company.domain.CompanyService;
import com.mmp.beacon.user.domain.AbstractUser;
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
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Validated
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final com.mmp.beacon.Security.UserService userService;
    private final CompanyService companyService;

    // 회원가입 엔드포인트
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegisterDTO userDto) {
        logger.info("Register request received: {}", userDto);
        try {
            Company company = companyService.findCompanyById(userDto.getCompany());
            userService.register(userDto, company);
            return ResponseEntity.ok("User registered successfully");
        } catch (NumberFormatException e) {
            logger.error("Invalid company ID format: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid company ID format.");
        } catch (DataIntegrityViolationException e) {
            logger.error("Duplicate entry error: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: Duplicate entry.");
        } catch (Exception e) {
            logger.error("Registration failed: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }

    // 로그인 엔드포인트
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDTO userDto, HttpServletRequest request, HttpServletResponse response) {
        logger.info("Login request received for userId: {}", userDto.getUserId());
        boolean isAuthenticated = userService.authenticate(userDto, request);
        if (isAuthenticated) {
            AbstractUser user = userService.getCurrentUser();
            if (user != null) {
                // 세션에 사용자 정보를 저장
                HttpSession session = request.getSession();
                session.setAttribute("user", user);

                Cookie cookie = new Cookie("JSESSIONID", session.getId());
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setMaxAge(60 * 60);
                response.addCookie(cookie);

                return ResponseEntity.ok("Login successful. User ID: " + user.getUserId());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    // 로그아웃 엔드포인트
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok("Logout successful");
    }




    @GetMapping("profile/me")
    public ResponseEntity<?> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetail)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        UserProfileDTO userProfile = new UserProfileDTO(
                userDetail.getUsername(),
                userDetail.getEmail(),
                userDetail.getSex(),
                userDetail.getPosition(),
                userDetail.getName(),
                userDetail.getPhone(),
                userDetail.getCompany()
        );

        return ResponseEntity.ok(userProfile);
    }


    // 홈 엔드포인트
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}
