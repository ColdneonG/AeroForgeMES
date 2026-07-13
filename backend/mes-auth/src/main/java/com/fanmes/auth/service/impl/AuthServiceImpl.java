package com.fanmes.auth.service.impl;

import com.fanmes.auth.domain.dto.LoginRequest;
import com.fanmes.auth.domain.entity.SysUser;
import com.fanmes.auth.domain.vo.LoginVO;
import com.fanmes.auth.domain.vo.UserVO;
import com.fanmes.auth.repository.AuthRepository;
import com.fanmes.auth.service.AuthService;
import com.fanmes.common.exception.BusinessException;
import com.fanmes.common.security.JwtTokenService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthRepository authRepository;
    private final JwtTokenService jwtTokenService;

    @Override
    public LoginVO login(LoginRequest request) {
        if (request == null || !StringUtils.hasText(request.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
        SysUser user = authRepository.findUserByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if ("停用".equals(user.getStatus())) {
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
        SysUser user = authRepository.findUserByUsername(username);
        if (user == null) {
            throw new BusinessException("token 对应用户不存在");
        }
        return buildLogin(user);
    }

    @Override
    public List<UserVO> listUsers() {
        return authRepository.findAllUsers().stream().map(user -> {
            UserVO vo = new UserVO();
            vo.setId(user.getId());
            vo.setDisplayName(user.getDisplayName());
            return vo;
        }).toList();
    }

    private LoginVO buildLogin(SysUser user) {
        Set<String> roles = new HashSet<>(authRepository.findRoleCodes(user.getId()));
        Set<String> permissions = new HashSet<>(authRepository.findPermissionCodes(user.getId()));
        LoginVO vo = new LoginVO();
        vo.setAccessToken(jwtTokenService.createToken(user.getId(), user.getUsername(), roles, permissions));
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setDisplayName(user.getDisplayName());
        vo.setRoles(roles);
        vo.setPermissions(permissions);
        return vo;
    }

    private String parseUsername(String token) {
        return jwtTokenService.verify(token).username();
    }
}
