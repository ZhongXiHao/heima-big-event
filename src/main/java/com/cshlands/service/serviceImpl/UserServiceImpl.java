package com.cshlands.service.serviceImpl;

import com.cshlands.exception.BusinessException;
import com.cshlands.mapper.UserMapper;
import com.cshlands.pojo.User;
import com.cshlands.service.UserService;
import com.cshlands.utils.Md5Util;
import com.cshlands.vo.UserVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    // username, password trimmed length in [5,16]
    public UserVO register(String username, String password) {
        User existUser = userMapper.findByUserName(username);
        if (existUser != null) {
            throw BusinessException.conflict("用户名已存在");
        }
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setUsername(username);
        user.setPassword(Md5Util.getMD5String(password));
        user.setCreateTime(now);
        user.setUpdateTime(now);
        userMapper.register(user);

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
