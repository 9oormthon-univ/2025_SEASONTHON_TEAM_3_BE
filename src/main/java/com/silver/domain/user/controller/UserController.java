package com.silver.domain.user.controller;


import com.silver.domain.user.dto.request.LoginRequestDto;
import com.silver.domain.user.dto.request.SignUpRequestDto;
import com.silver.domain.user.dto.response.TokenResponseDto;
import com.silver.domain.user.dto.response.UserInfoResponseDto;
import com.silver.domain.user.service.UserService;
import com.silver.global.common.CustomApiResponse;
import com.silver.global.config.aws.S3Service;
import com.silver.global.config.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "회원관리 API")
@RequestMapping("/user")
@RestController
@AllArgsConstructor

public class UserController {
    private final UserService userService;
    private final S3Service s3Service;

    @Operation(summary = "회원가입", description = "서비스를 이용할 새로운 회원을 등록합니다,")
    @PostMapping("/signUp")
    public ResponseEntity<CustomApiResponse<String>> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        userService.signUp(signUpRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(CustomApiResponse.onSuccess("회원가입성공 했습니다"));
    }

    @Operation(summary = "로그인", description = "회원 로그인을 처리합니다.")
    @PostMapping("/logIn")
    public ResponseEntity<CustomApiResponse<TokenResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto) {
        TokenResponseDto tokenResponseDto = userService.login(loginRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(CustomApiResponse.onSuccess(tokenResponseDto));
    }
    @Operation(summary = "사용자 정보 조회", description = "사용자의 정보를 조회합니다.")
    @GetMapping("/getInfo")
    public ResponseEntity<CustomApiResponse<UserInfoResponseDto>> getInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getId();
        UserInfoResponseDto userInfoResponseDto = userService.getInfo(userId);
        return ResponseEntity.status(HttpStatus.OK).body(CustomApiResponse.onSuccess(userInfoResponseDto));
    }
    @Operation(summary = "프로필 이미지 업로드")
    @PostMapping(
            value = "/profile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CustomApiResponse<String>> addProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart("imageFile") MultipartFile imageFile) {

        String folder = "profile/" + customUserDetails.getId();
        String imageUrl = s3Service.uploadFile(folder, imageFile);

        return ResponseEntity.ok(CustomApiResponse.onSuccess(imageUrl));
    }

}