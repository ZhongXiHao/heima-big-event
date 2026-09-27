package com.cshlands.service.serviceImpl;

import com.cshlands.exception.BusinessException;
import com.cshlands.mapper.UserMapper;
import com.cshlands.pojo.User;
import com.cshlands.service.UserService;
import com.cshlands.utils.JwtUtil;
import com.cshlands.utils.Md5Util;
import com.cshlands.vo.LoginResponseVO;
import com.cshlands.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User findByUserName(String username) {
        return userMapper.findByUserName(username);
    }

    @Override
    public UserVO getUserInfo(String username) {
        User user = userMapper.findByUserName(username);
        if (user == null) {
            throw BusinessException.notFound("用户未找到");
        }
        return toVO(user);
    }



    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setUserPic(user.getUserPic());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }
}
