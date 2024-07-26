package com.mmp.beacon.Security.auth;

import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.company.domain.repository.CompanyRepository;
import com.mmp.beacon.Security.presentation.request.CreateUserRequest;
import com.mmp.beacon.Security.presentation.request.LoginRequest;
import com.mmp.beacon.Security.query.response.UserProfileResponse;
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
        logger.info("Register request received: {}", userDto);
        try {
            Optional<Company> companyOptional = companyRepository.findByName(userDto.getCompany());
            if (companyOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid company name.");
            }
            Company company = companyOptional.get();
            userApplicationService.register(userDto, company);
            return ResponseEntity.ok("User registered successfully");
        } catch (DataIntegrityViolationException e) {
            logger.error("Duplicate entry error: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: Duplicate entry.");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid role or permission error: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Registration failed: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest userDto, HttpServletRequest request, HttpServletResponse response) {
        logger.info("Login request received for userId: {}", userDto.getUserId());
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

                return ResponseEntity.ok("Login successful. User ID: " + user.getUserId());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

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

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}
