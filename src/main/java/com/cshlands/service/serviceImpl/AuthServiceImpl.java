package com.cshlands.service.serviceImpl;

import com.cshlands.exception.BusinessException;
import com.cshlands.mapper.UserMapper;
import com.cshlands.pojo.User;
import com.cshlands.service.AuthService;
import com.cshlands.utils.JwtUtil;
import com.cshlands.utils.Md5Util;
import com.cshlands.vo.LoginResponseVO;
import com.cshlands.vo.UserVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {


    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
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

    @Override
    public LoginResponseVO login(String username, String password) {
        User user = userMapper.findByUserName(username);
        if (user == null || !user.getPassword().equals(Md5Util.getMD5String(password))) {
            throw BusinessException.unauthorized("用户名或密码错误");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        String token = jwtUtil.genToken(claims);
        return new LoginResponseVO(token, username);
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
