package com.mmp.beacon.Security;

import com.mmp.beacon.company.domain.CompanyService;
import com.mmp.beacon.user.domain.AbstractUser;
import com.mmp.beacon.company.domain.Company;
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


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Validated
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final CompanyService companyService;

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

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDTO userDto) {
        logger.info("Login request received for userId: {}", userDto.getUserId());
        boolean isAuthenticated = userService.authenticate(userDto);
        if (isAuthenticated) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/profile/me")
    public ResponseEntity<AbstractUser> profile() {
        return userService.getProfile()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/profile/{user_no}")
    public ResponseEntity<AbstractUser> profileById(@PathVariable("user_no") Long userId) {
        return userService.getProfileById(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

}
