package com.fanmes.auth.service.impl;

import com.fanmes.auth.domain.dto.LoginRequest;
import com.fanmes.auth.domain.dto.PasswordUpdateRequest;
import com.fanmes.auth.domain.dto.ProfileUpdateRequest;
import com.fanmes.auth.domain.dto.UserCreateRequest;
import com.fanmes.auth.domain.dto.UserPasswordResetRequest;
import com.fanmes.auth.domain.dto.UserRoleAssignRequest;
import com.fanmes.auth.domain.dto.UserStatusUpdateRequest;
import com.fanmes.auth.domain.dto.UserUpdateRequest;
import com.fanmes.auth.domain.entity.SysUser;
import com.fanmes.auth.domain.vo.AdminUserVO;
import com.fanmes.auth.domain.vo.LoginVO;
import com.fanmes.auth.domain.vo.ProfileVO;
import com.fanmes.auth.domain.vo.UserVO;
import com.fanmes.auth.domain.vo.RoleVO;
import com.fanmes.auth.domain.vo.UserPageVO;
import com.fanmes.auth.mapper.AuthMapper;
import com.fanmes.auth.service.AuthService;
import com.fanmes.common.exception.BusinessException;
import com.fanmes.common.security.JwtTokenService;
import java.util.HashSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import com.fanmes.common.id.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final String ENABLED = "启用";
    private static final String DISABLED = "停用";
    private final AuthMapper authMapper;
    private final JwtTokenService jwtTokenService;

    @Override
    public LoginVO login(LoginRequest request) {
        if (request == null || !StringUtils.hasText(request.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
        SysUser user = authMapper.findUserByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (isDisabled(user.getStatus())) {
            throw new BusinessException("用户已停用");
        }
        if (!request.getPassword().equals(user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        return buildLogin(user);
    }

    @Override
    public LoginVO currentUser(String token) {
        String username = parseUsername(token);
        SysUser user = authMapper.findUserByUsername(username);
        if (user == null) {
            throw new BusinessException("token 对应用户不存在");
        }
        if (isDisabled(user.getStatus())) {
            throw new BusinessException("用户已停用");
        }
        return buildLogin(user);
    }

    @Override
    public ProfileVO profile(String token) {
        return buildProfile(findCurrentUser(token));
    }

    @Override
    public ProfileVO updateProfile(String token, ProfileUpdateRequest request) {
        SysUser user = findCurrentUser(token);
        authMapper.updateProfile(user.getId(), request.getDisplayName().trim(), StringUtils.trimWhitespace(request.getMobile()));
        return buildProfile(findCurrentUser(token));
    }

    @Override
    public void updatePassword(String token, PasswordUpdateRequest request) {
        SysUser user = findCurrentUser(token);
        if (!request.getOldPassword().equals(user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的新密码不一致");
        }
        if (request.getNewPassword().equals(request.getOldPassword())) {
            throw new BusinessException("新密码不能与旧密码相同");
        }
        authMapper.updatePassword(user.getId(), request.getNewPassword());
    }

    @Override
    public List<UserVO> listUsers() {
        return authMapper.findAllUsers().stream().map(user -> {
            UserVO vo = new UserVO();
            vo.setId(user.getId());
            vo.setDisplayName(user.getDisplayName());
            return vo;
        }).toList();
    }

    private LoginVO buildLogin(SysUser user) {
        Set<String> roles = new HashSet<>(authMapper.findRoleCodes(user.getId()));
        Set<String> permissions = new HashSet<>(authMapper.findPermissionCodes(user.getId()));
        LoginVO vo = new LoginVO();
        vo.setAccessToken(jwtTokenService.createToken(user.getId(), user.getUsername(), roles, permissions));
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setDisplayName(user.getDisplayName());
        vo.setRoles(roles);
        vo.setPermissions(permissions);
        return vo;
    }

    @Override
    public UserPageVO pageUsers(String keyword, String status, Long orgId, Long teamId, Long roleId, int pageNo, int pageSize) {
        int safePageNo = Math.max(pageNo, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        String normalizedStatus = StringUtils.hasText(status) ? normalizeStatus(status) : null;
        List<AdminUserVO> records = authMapper.findUsers(trim(keyword), normalizedStatus, orgId, teamId, roleId,
                        (safePageNo - 1) * safePageSize, safePageSize)
                .stream().map(this::toAdminUser).toList();
        return new UserPageVO(authMapper.countUsers(trim(keyword), normalizedStatus, orgId, teamId, roleId), records);
    }

    @Override
    public AdminUserVO getUser(Long userId) {
        return toAdminUser(requireUser(userId));
    }

    @Override
    @Transactional
    public AdminUserVO createUser(UserCreateRequest request) {
        String username = trim(request.getUsername());
        if (authMapper.findUserByUsername(username) != null) {
            throw new BusinessException("登录账号已存在");
        }
        Set<Long> roleIds = normalizedRoleIds(request.getRoleIds());
        validateRoles(roleIds);
        SysUser user = new SysUser();
        user.setId(IdGenerator.nextId());
        user.setUsername(username);
        user.setPassword(request.getPassword());
        user.setDisplayName(trim(request.getDisplayName()));
        user.setMobile(trim(request.getMobile()));
        user.setEmployeeNo(trim(request.getEmployeeNo()));
        user.setOrgId(request.getOrgId());
        user.setTeamId(request.getTeamId());
        user.setStatus(StringUtils.hasText(request.getStatus()) ? normalizeStatus(request.getStatus()) : ENABLED);
        authMapper.insertUser(user);
        replaceRoles(user.getId(), roleIds);
        return getUser(user.getId());
    }

    @Override
    @Transactional
    public AdminUserVO updateUser(Long userId, UserUpdateRequest request) {
        SysUser user = requireUser(userId);
        user.setDisplayName(trim(request.getDisplayName()));
        user.setMobile(trim(request.getMobile()));
        user.setEmployeeNo(trim(request.getEmployeeNo()));
        user.setOrgId(request.getOrgId());
        user.setTeamId(request.getTeamId());
        authMapper.updateUser(user);
        return getUser(userId);
    }

    @Override
    @Transactional
    public AdminUserVO updateUserStatus(String token, Long userId, UserStatusUpdateRequest request) {
        SysUser target = requireUser(userId);
        SysUser operator = findCurrentUser(token);
        String status = normalizeStatus(request.getStatus());
        if (operator.getId().equals(target.getId()) && DISABLED.equals(status)) {
            throw new BusinessException("不能停用当前登录账号");
        }
        authMapper.updateUserStatus(userId, status);
        return getUser(userId);
    }

    @Override
    @Transactional
    public AdminUserVO assignUserRoles(String token, Long userId, UserRoleAssignRequest request) {
        SysUser target = requireUser(userId);
        Set<Long> roleIds = normalizedRoleIds(request.getRoleIds());
        validateRoles(roleIds);
        SysUser operator = findCurrentUser(token);
        if (operator.getId().equals(target.getId()) && roleIds.isEmpty()) {
            throw new BusinessException("不能移除当前登录账号的全部角色");
        }
        replaceRoles(userId, roleIds);
        return getUser(userId);
    }

    @Override
    @Transactional
    public void resetUserPassword(String token, Long userId, UserPasswordResetRequest request) {
        SysUser target = requireUser(userId);
        SysUser operator = findCurrentUser(token);
        if (operator.getId().equals(target.getId())) {
            throw new BusinessException("请通过个人资料页面修改自己的密码");
        }
        authMapper.updatePassword(target.getId(), request.getPassword());
    }

    @Override
    public List<RoleVO> listEnabledRoles() {
        return authMapper.findEnabledRoles();
    }

    private SysUser findCurrentUser(String token) {
        String username = parseUsername(token);
        SysUser user = authMapper.findUserProfileByUsername(username);
        if (user == null) {
            throw new BusinessException("token 对应用户不存在");
        }
        return user;
    }

    private AdminUserVO toAdminUser(SysUser user) {
        AdminUserVO vo = new AdminUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setDisplayName(user.getDisplayName());
        vo.setMobile(user.getMobile());
        vo.setEmployeeNo(user.getEmployeeNo());
        vo.setOrgId(user.getOrgId());
        vo.setOrgName(user.getOrgName());
        vo.setTeamId(user.getTeamId());
        vo.setTeamName(user.getTeamName());
        vo.setStatus(user.getStatus());
        vo.setRoles(authMapper.findRolesByUserId(user.getId()));
        return vo;
    }

    private SysUser requireUser(Long userId) {
        if (userId == null) throw new BusinessException("用户 ID 不能为空");
        SysUser user = authMapper.findUserById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        return user;
    }

    private Set<Long> normalizedRoleIds(List<Long> values) {
        if (values == null || values.isEmpty()) return Collections.emptySet();
        if (values.stream().anyMatch(java.util.Objects::isNull)) {
            throw new BusinessException("角色 ID 不能为空");
        }
        Set<Long> roleIds = values.stream().filter(java.util.Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
        if (roleIds.size() != values.size()) {
            throw new BusinessException("角色 ID 不能重复");
        }
        return roleIds;
    }

    private void validateRoles(Set<Long> roleIds) {
        if (roleIds.isEmpty()) return;
        if (authMapper.countEnabledRoles(roleIds) != roleIds.size()) throw new BusinessException("角色不存在或已停用");
    }

    private void replaceRoles(Long userId, Set<Long> roleIds) {
        authMapper.deleteUserRoles(userId);
        for (Long roleId : roleIds) authMapper.insertUserRole(IdGenerator.nextId(), userId, roleId);
    }

    private String normalizeStatus(String value) {
        String status = trim(value);
        if ("ENABLED".equalsIgnoreCase(status) || ENABLED.equals(status)) return ENABLED;
        if ("DISABLED".equalsIgnoreCase(status) || DISABLED.equals(status)) return DISABLED;
        throw new BusinessException("账号状态仅支持 启用 或 停用");
    }

    private String trim(String value) { return StringUtils.hasText(value) ? value.trim() : null; }

    private boolean isDisabled(String status) {
        return DISABLED.equals(status) || "DISABLED".equalsIgnoreCase(status);
    }

    private ProfileVO buildProfile(SysUser user) {
        ProfileVO vo = new ProfileVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setDisplayName(user.getDisplayName());
        vo.setMobile(user.getMobile());
        vo.setEmployeeNo(user.getEmployeeNo());
        vo.setOrgId(user.getOrgId());
        vo.setOrgName(user.getOrgName());
        vo.setTeamId(user.getTeamId());
        vo.setTeamName(user.getTeamName());
        vo.setStatus(user.getStatus());
        vo.setRoles(new HashSet<>(authMapper.findRoleCodes(user.getId())));
        vo.setPermissions(new HashSet<>(authMapper.findPermissionCodes(user.getId())));
        return vo;
    }


    private String parseUsername(String token) {
        return jwtTokenService.verify(token).username();
    }
}
