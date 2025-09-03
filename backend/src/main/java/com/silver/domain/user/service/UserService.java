package com.silver.domain.user.service;

import com.silver.domain.user.dto.request.LoginRequestDto;
import com.silver.domain.user.dto.request.SignUpRequestDto;
import com.silver.domain.user.dto.response.TokenResponseDto;
import com.silver.domain.user.dto.response.UserInfoResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void signUp(SignUpRequestDto signUpRequestDto);

    TokenResponseDto login(LoginRequestDto loginRequestDto);

    UserInfoResponseDto getInfo(Long userId);
}
