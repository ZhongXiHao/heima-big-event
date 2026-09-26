package com.cshlands.service;

import com.cshlands.vo.LoginResponseVO;
import com.cshlands.vo.UserVO;

public interface AuthService {
    // 注册用户
    UserVO register(String username, String password);

    // 登录用户
    LoginResponseVO login(String username, String password);
}
