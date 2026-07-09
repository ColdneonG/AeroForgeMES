package com.fanmes.auth.service;

import com.fanmes.auth.domain.dto.LoginRequest;
import com.fanmes.auth.domain.vo.LoginVO;
import com.fanmes.auth.domain.vo.UserVO;
import java.util.List;

public interface AuthService {
    LoginVO login(LoginRequest request);
    LoginVO currentUser(String token);
    List<UserVO> listUsers();
}
