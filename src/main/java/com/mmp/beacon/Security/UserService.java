package com.mmp.beacon.Security;

import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.user.domain.AbstractUser;
import com.mmp.beacon.user.domain.User;
import com.mmp.beacon.user.domain.UserRepository;
import com.mmp.beacon.user.domain.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

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

            //security 인증 설정
            Authentication auth = new UsernamePasswordAuthenticationToken(new UserDetail(user), null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            //세션 생성 및 유저 정보 저장
            HttpSession session = request.getSession(true);//세션이 없으면 새로 생성, 있으면 기존 세션 반환
            session.setAttribute("user", user);//세션에 유저 정보 저장

            return true;
        }
        return false;
    }

    public AbstractUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetail) {
            return ((UserDetail) auth.getPrincipal()).getUser();
        }
        return null;
    }

    public Optional<User> profile() {
        AbstractUser currentUser = getCurrentUser();
        if (currentUser != null && currentUser instanceof User) {
            return Optional.of((User) currentUser);
        }
        return Optional.empty();
    }
}
