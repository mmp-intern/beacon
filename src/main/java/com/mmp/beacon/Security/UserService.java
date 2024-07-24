package com.mmp.beacon.Security;

import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.user.domain.AbstractUser;
import com.mmp.beacon.user.domain.User;
import com.mmp.beacon.user.domain.UserRepository;
import com.mmp.beacon.user.domain.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void register(UserRegisterDTO userDto, Company company) {
        String encPassword = bCryptPasswordEncoder.encode(userDto.getPassword());
        User user = new User(userDto.getUserId(), encPassword, userDto.getName(), userDto.getPhone(), userDto.getEmail(), userDto.getSex(), userDto.getPosition(), company);
        user.setRole(UserRole.USER);
        userRepository.save(user);
    }

    public boolean authenticate(UserLoginDTO userDto, HttpServletRequest request) {
        Optional<AbstractUser> userOpt = userRepository.findByUserId(userDto.getUserId());

        if (userOpt.isPresent() && bCryptPasswordEncoder.matches(userDto.getPassword(), userOpt.get().getPassword())) {
            AbstractUser user = userOpt.get();

            Authentication auth = new UsernamePasswordAuthenticationToken(new com.mmp.beacon.security.UserDetail(user), null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            logger.info("User authenticated: {}", user.getUserId());
            logger.info("Session ID: {}", session.getId());
            logger.info("Authentication: {}", SecurityContextHolder.getContext().getAuthentication());

            return true;
        }
        return false;
    }

    public AbstractUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof com.mmp.beacon.security.UserDetail) {
            return ((com.mmp.beacon.security.UserDetail) auth.getPrincipal()).getUser();
        }
        return null;
    }
}
