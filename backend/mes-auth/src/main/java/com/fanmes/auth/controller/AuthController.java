package com.fanmes.auth.controller;

import com.fanmes.auth.domain.dto.LoginRequest;
import com.fanmes.auth.domain.vo.LoginVO;
import com.fanmes.auth.service.AuthService;
import com.fanmes.common.api.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @GetMapping("/me")
    public Result<LoginVO> me(@RequestHeader("Authorization") String token) {
        return Result.success(authService.currentUser(token));
    }

    @GetMapping("/me/permissions")
    public Result<LoginVO> permissions(@RequestHeader("Authorization") String token) {
        return Result.success(authService.currentUser(token));
    }
}
