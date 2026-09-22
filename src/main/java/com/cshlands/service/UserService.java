package com.cshlands.service;

import com.cshlands.pojo.User;
import com.cshlands.vo.UserVO;

public interface UserService {

    // 根据用户名查询用户
    User findByUserName(String username);
    // 注册用户
    UserVO register(String username, String password);
}
