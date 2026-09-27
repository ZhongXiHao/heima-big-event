package com.cshlands.service;

import com.cshlands.pojo.User;
import com.cshlands.vo.LoginResponseVO;
import com.cshlands.vo.UserVO;

public interface UserService {

    // 根据用户名查询用户
    User findByUserName(String username);

    User findById(Integer id);

    UserVO getUserInfo(String username);

    UserVO updateUserInfo(User updatedUser);


}
