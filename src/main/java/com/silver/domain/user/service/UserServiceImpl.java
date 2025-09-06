package com.silver.domain.user.service;

import com.silver.domain.user.dto.request.LoginRequestDto;
import com.silver.domain.user.dto.request.SignUpRequestDto;
import com.silver.domain.user.dto.response.TokenResponseDto;
import com.silver.domain.user.dto.response.UserInfoResponseDto;
import com.silver.domain.user.entity.Allergy;
import com.silver.domain.user.entity.Purpose;
import com.silver.domain.user.entity.Role;
import com.silver.domain.user.entity.User;
import com.silver.domain.user.repository.UserRepository;
import com.silver.global.config.lwt.JwtToken;
import com.silver.global.config.lwt.JwtUtill;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtill jwtUtil;

    @Override
    public void signUp(SignUpRequestDto signUpRequestDto) {
        if (userRepository.findByEmail(signUpRequestDto.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());
        User user = User.builder()
                .email(signUpRequestDto.getEmail())
                .password(encodedPassword)
                .username(signUpRequestDto.getName())
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .allergy(Allergy.valueOf(signUpRequestDto.getAllergy().toUpperCase()))
                .purpose(Purpose.valueOf(signUpRequestDto.getPurpose().toUpperCase()))
                .build();

        userRepository.save(user);
    }

    @Override
    public TokenResponseDto login(LoginRequestDto loginRequestDto) {

        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        JwtToken jwtToken = jwtUtil.generateToken(user.getEmail());

        return TokenResponseDto.builder()
                .accessToken(jwtToken.getAccessToken())
                .refreshToken(jwtToken.getRefreshToken())
                .build();
    }

    @Override
    public UserInfoResponseDto getInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return UserInfoResponseDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .allergy(user.getAllergy().name())
                .purpose(user.getPurpose().name())
                .build();
    }
}
