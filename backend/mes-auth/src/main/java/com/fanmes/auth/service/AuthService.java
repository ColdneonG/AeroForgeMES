package com.fanmes.auth.service;

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
import com.fanmes.auth.domain.vo.RoleVO;
import com.fanmes.auth.domain.vo.UserPageVO;
import com.fanmes.auth.domain.vo.UserVO;
import java.util.List;

public interface AuthService {
    LoginVO login(LoginRequest request);
    LoginVO currentUser(String token);
    ProfileVO profile(String token);
    ProfileVO updateProfile(String token, ProfileUpdateRequest request);
    void updatePassword(String token, PasswordUpdateRequest request);
    List<UserVO> listUsers();
    UserPageVO pageUsers(String keyword, String status, Long orgId, Long teamId, Long roleId, int pageNo, int pageSize);
    AdminUserVO getUser(Long userId);
    AdminUserVO createUser(UserCreateRequest request);
    AdminUserVO updateUser(Long userId, UserUpdateRequest request);
    AdminUserVO updateUserStatus(String token, Long userId, UserStatusUpdateRequest request);
    AdminUserVO assignUserRoles(String token, Long userId, UserRoleAssignRequest request);
    void resetUserPassword(String token, Long userId, UserPasswordResetRequest request);
    List<RoleVO> listEnabledRoles();
}
