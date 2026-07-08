package com.fanmes.auth.service;

import com.fanmes.auth.domain.dto.LoginRequest;
import com.fanmes.auth.domain.vo.LoginVO;

public interface AuthService {
    LoginVO login(LoginRequest request);
    LoginVO currentUser(String token);
}
