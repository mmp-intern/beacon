package com.mmp.beacon.Security;

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
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CompanyRepository companyRepository;

    public void register(CreateUserRequest userDto, Company company) {
        logger.info("Registering user: {}", userDto.getUserId());
        try {
            String encPassword = bCryptPasswordEncoder.encode(userDto.getPassword());
            UserRole role = UserRole.valueOf(userDto.getRole().toUpperCase());

            // Get the current authenticated user and their role
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                throw new IllegalArgumentException("Authentication required");
            }
            User currentUser = ((UserDetail) auth.getPrincipal()).getUser();
            UserRole currentUserRole = currentUser.getRole();

            // Check if the current user's company matches the target company
            if (!currentUser.getCompany().getName().equals(company.getName())) {
                throw new IllegalArgumentException("Access denied: You cannot create accounts for other companies.");
            }

            // Check if the current user has the right to create the specified role
            if (currentUserRole == UserRole.SUPER_ADMIN || (currentUserRole == UserRole.ADMIN && role == UserRole.USER)) {
                User user = new User(userDto.getUserId(), encPassword, userDto.getName(), userDto.getPhone(), userDto.getEmail(), userDto.getPosition(), company);
                user.setRole(role);

                // Save the user to the database
                userRepository.save(user);
                logger.info("User registered successfully: {}", userDto.getUserId());
            } else {
                throw new IllegalArgumentException("Invalid role or insufficient permissions");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid role provided: {}", userDto.getRole(), e);
            throw new IllegalArgumentException("Invalid role or insufficient permissions: " + userDto.getRole());
        } catch (DataIntegrityViolationException e) {
            logger.error("Duplicate entry error: ", e);
            throw new DataIntegrityViolationException("Duplicate entry: " + userDto.getUserId());
        } catch (Exception e) {
            logger.error("Registration failed: ", e);
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }

    public boolean authenticate(UserLoginDTO userDto, HttpServletRequest request) {
        logger.info("Authenticating user: {}", userDto.getUserId());
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

            logger.info("User authenticated: {}", user.getUserId());
            return true;
        }
        logger.warn("Authentication failed for user: {}", userDto.getUserId());
        return false;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetail) {
            UserDetail userDetail = (UserDetail) auth.getPrincipal();
            return userRepository.findByUserId(userDetail.getUsername())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
        }
        return null;
    }

    public UserProfileDTO getUserProfile(String userId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalArgumentException("Authentication required");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if the current user's company matches the target user's company
        if (!currentUser.getCompany().getName().equals(user.getCompany().getName())) {
            throw new IllegalArgumentException("Access denied: You cannot view profiles from other companies.");
        }

        return new UserProfileDTO(
                user.getUserId(),
                user.getEmail(),
                user.getPosition(),
                user.getName(),
                user.getPhone(),
                user.getCompany().getName(),
                user.getRole().name());
    }
}