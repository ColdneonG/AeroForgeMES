package com.fanmes.auth.controller;

import com.fanmes.auth.domain.dto.LoginRequest;
import com.fanmes.auth.domain.dto.PasswordUpdateRequest;
import com.fanmes.auth.domain.dto.ProfileUpdateRequest;
import com.fanmes.auth.domain.dto.UserCreateRequest;
import com.fanmes.auth.domain.dto.UserPasswordResetRequest;
import com.fanmes.auth.domain.dto.UserRoleAssignRequest;
import com.fanmes.auth.domain.dto.UserStatusUpdateRequest;
import com.fanmes.auth.domain.dto.UserUpdateRequest;
import com.fanmes.auth.domain.vo.AdminUserVO;
import com.fanmes.auth.domain.vo.LoginVO;
import com.fanmes.auth.domain.vo.ProfileVO;
import com.fanmes.auth.domain.vo.UserVO;
import com.fanmes.auth.domain.vo.RoleVO;
import com.fanmes.auth.domain.vo.UserPageVO;
import com.fanmes.auth.service.AuthService;
import com.fanmes.common.api.Result;
import com.fanmes.common.exception.BusinessException;
import com.fanmes.common.security.JwtTokenService;
import com.fanmes.common.security.RequirePermission;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.WebUtils;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtTokenService jwtTokenService;

    @Value("${fanmes.auth.refresh-cookie.name:fanmes_refresh_token}")
    private String refreshCookieName;

    @Value("${fanmes.auth.refresh-cookie.max-age-seconds:604800}")
    private long refreshCookieMaxAgeSeconds;

    @Value("${fanmes.auth.refresh-cookie.secure:true}")
    private boolean refreshCookieSecure;

    @Value("${fanmes.auth.refresh-cookie.same-site:Strict}")
    private String refreshCookieSameSite;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginVO login = authService.login(request);
        writeRefreshCookie(response, jwtTokenService.createRefreshToken(login.getUserId(), login.getUsername()), refreshCookieMaxAgeSeconds);
        return Result.success(login);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletResponse response) {
        writeRefreshCookie(response, "", 0);
        return Result.success();
    }

    @PostMapping("/refresh")
    public Result<LoginVO> refresh(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, refreshCookieName);
        if (cookie == null || !org.springframework.util.StringUtils.hasText(cookie.getValue())) {
            throw new BusinessException("Refresh token is missing");
        }
        return Result.success(authService.refresh(cookie.getValue()));
    }

    private void writeRefreshCookie(HttpServletResponse response, String value, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, value)
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path("/api/auth")
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @GetMapping("/me")
    public Result<LoginVO> me(@RequestHeader("Authorization") String token) {
        return Result.success(authService.currentUser(token));
    }

    @GetMapping("/me/permissions")
    public Result<LoginVO> permissions(@RequestHeader("Authorization") String token) {
        return Result.success(authService.currentUser(token));
    }

    @GetMapping("/profile")
    public Result<ProfileVO> profile(@RequestHeader("Authorization") String token) {
        return Result.success(authService.profile(token));
    }

    @PutMapping("/profile")
    public Result<ProfileVO> updateProfile(@RequestHeader("Authorization") String token, @Valid @RequestBody ProfileUpdateRequest request) {
        return Result.success(authService.updateProfile(token, request));
    }

    @PutMapping("/profile/password")
    public Result<Void> updatePassword(@RequestHeader("Authorization") String token, @Valid @RequestBody PasswordUpdateRequest request) {
        authService.updatePassword(token, request);
        return Result.success();
    }


    @GetMapping("/users")
    public Result<List<UserVO>> users() {
        return Result.success(authService.listUsers());
    }

    @GetMapping("/admin/users")
    @RequirePermission("system:user:view")
    public Result<UserPageVO> pageUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long orgId,
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) Long roleId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        return Result.success(authService.pageUsers(keyword, status, orgId, teamId, roleId, pageNo, pageSize));
    }

    @GetMapping("/admin/users/{userId}")
    @RequirePermission("system:user:view")
    public Result<AdminUserVO> user(@PathVariable Long userId) {
        return Result.success(authService.getUser(userId));
    }

    @PostMapping("/admin/users")
    @RequirePermission("system:user:create")
    public Result<AdminUserVO> createUser(@Valid @RequestBody UserCreateRequest request) {
        return Result.success(authService.createUser(request));
    }

    @PutMapping("/admin/users/{userId}")
    @RequirePermission("system:user:edit")
    public Result<AdminUserVO> updateUser(@PathVariable Long userId, @Valid @RequestBody UserUpdateRequest request) {
        return Result.success(authService.updateUser(userId, request));
    }

    @PutMapping("/admin/users/{userId}/status")
    @RequirePermission("system:user:status")
    public Result<AdminUserVO> updateUserStatus(@RequestHeader("Authorization") String token, @PathVariable Long userId,
                                                  @Valid @RequestBody UserStatusUpdateRequest request) {
        return Result.success(authService.updateUserStatus(token, userId, request));
    }

    @PutMapping("/admin/users/{userId}/roles")
    @RequirePermission("system:user:assign-role")
    public Result<AdminUserVO> assignUserRoles(@RequestHeader("Authorization") String token, @PathVariable Long userId,
                                                @RequestBody UserRoleAssignRequest request) {
        return Result.success(authService.assignUserRoles(token, userId, request));
    }

    @PostMapping("/admin/users/{userId}/reset-password")
    @RequirePermission("system:user:reset-password")
    public Result<Void> resetUserPassword(@RequestHeader("Authorization") String token, @PathVariable Long userId,
                                          @Valid @RequestBody UserPasswordResetRequest request) {
        authService.resetUserPassword(token, userId, request);
        return Result.success();
    }

    @GetMapping("/admin/roles")
    @RequirePermission("system:role:view")
    public Result<List<RoleVO>> roles() {
        return Result.success(authService.listEnabledRoles());
    }
}
