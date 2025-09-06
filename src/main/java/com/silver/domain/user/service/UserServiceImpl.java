package com.silver.domain.user.service;

import com.silver.domain.user.dto.request.LoginRequestDto;
import com.silver.domain.user.dto.request.SignUpRequestDto;
import com.silver.domain.user.dto.request.UpdateProfileRequestDto;
import com.silver.domain.user.dto.response.TokenResponseDto;
import com.silver.domain.user.dto.response.UserInfoResponseDto;
import com.silver.domain.user.entity.*;
import com.silver.domain.user.repository.*;
import com.silver.global.config.lwt.JwtToken;
import com.silver.global.config.lwt.JwtUtill;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AllergyRepository allergyRepository;
    private final PurposeRepository purposeRepository;
    private final UserAllergyRepository userAllergyRepository;
    private final UserPurposeRepository userPurposeRepository;
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
               .build();

        userRepository.save(user);


        if (signUpRequestDto.getAllergies() != null) {
            for (String code : signUpRequestDto.getAllergies()) {
                Allergy allergy = allergyRepository.findByCode(code)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown allergy code: " + code));
                if (!userAllergyRepository.existsByUserIdAndAllergyId(user.getId(), allergy.getId())) {
                    userAllergyRepository.save(
                            UserAllergy.builder()
                                    .user(user)
                                    .allergy(allergy)
                                    .build()
                    );
                }
            }
        }

        if (signUpRequestDto.getPurposes() != null) {
            for (String code : signUpRequestDto.getPurposes()) {
                Purpose purpose = purposeRepository.findByCode(code)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown purpose code: " + code));
                if (!userPurposeRepository.existsByUserIdAndPurposeId(user.getId(), purpose.getId())) {
                    userPurposeRepository.save(
                            UserPurpose.builder()
                                    .user(user)
                                    .purpose(purpose)
                                    .build()
                    );
                }
            }
        }

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
    @Transactional(readOnly = true)
    public UserInfoResponseDto getInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Set<String> allergyNames = userAllergyRepository.findAllByUserId(userId).stream()
                .map(ua -> ua.getAllergy().getName())
                .collect(Collectors.toSet());

        Set<String> purposeNames = userPurposeRepository.findAllByUserId(userId).stream()
                .map(up -> up.getPurpose().getName())
                .collect(Collectors.toSet());

        return UserInfoResponseDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .allergies(allergyNames)
                .purposes(purposeNames)
                .build();
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequestDto req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (req.getName() != null && !req.getName().isBlank()) {
            user.setUsername(req.getName().trim());
        }

        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            String newEmail = req.getEmail().trim();
            if (!newEmail.equalsIgnoreCase(user.getEmail())
                    && userRepository.findByEmail(newEmail).isPresent()) {
                throw new RuntimeException("이미 사용 중인 이메일입니다.");
            }
            user.setEmail(newEmail);
        }

        if (req.getAllergies() != null) {
            var currentLinks = userAllergyRepository.findAllByUserId(userId);
            var currentCodes = currentLinks.stream()
                    .map(ua -> ua.getAllergy().getCode())
                    .collect(java.util.stream.Collectors.toSet());

            var requestedCodes = req.getAllergies().stream()
                    .filter(c -> c != null && !c.isBlank())
                    .map(String::trim)
                    .collect(java.util.stream.Collectors.toSet());

            var toRemove = currentLinks.stream()
                    .filter(ua -> !requestedCodes.contains(ua.getAllergy().getCode()))
                    .toList();
            if (!toRemove.isEmpty()) userAllergyRepository.deleteAllInBatch(toRemove);

            var toAddCodes = requestedCodes.stream()
                    .filter(code -> !currentCodes.contains(code))
                    .toList();
            for (String code : toAddCodes) {
                Allergy allergy = allergyRepository.findByCode(code)
                        .orElseThrow(() -> new RuntimeException("알 수 없는 알러지 코드: " + code));
                userAllergyRepository.save(UserAllergy.builder()
                        .user(user)
                        .allergy(allergy)
                        .build());
            }
        }

        if (req.getPurposes() != null) {
            var currentLinks = userPurposeRepository.findAllByUserId(userId);
            var currentCodes = currentLinks.stream()
                    .map(up -> up.getPurpose().getCode())
                    .collect(java.util.stream.Collectors.toSet());

            var requestedCodes = req.getPurposes().stream()
                    .filter(c -> c != null && !c.isBlank())
                    .map(String::trim)
                    .collect(java.util.stream.Collectors.toSet());

            var toRemove = currentLinks.stream()
                    .filter(up -> !requestedCodes.contains(up.getPurpose().getCode()))
                    .toList();
            if (!toRemove.isEmpty()) userPurposeRepository.deleteAllInBatch(toRemove);

            var toAddCodes = requestedCodes.stream()
                    .filter(code -> !currentCodes.contains(code))
                    .toList();
            for (String code : toAddCodes) {
                Purpose purpose = purposeRepository.findByCode(code)
                        .orElseThrow(() -> new RuntimeException("알 수 없는 목적 코드: " + code));
                userPurposeRepository.save(UserPurpose.builder()
                        .user(user)
                        .purpose(purpose)
                        .build());
            }
        }

    }

}
