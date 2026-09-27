package com.cshlands.service.serviceImpl;

import com.cshlands.dto.UpdateUserDTO;
import com.cshlands.exception.BusinessException;
import com.cshlands.mapper.UserMapper;
import com.cshlands.pojo.User;
import com.cshlands.service.UserService;
import com.cshlands.utils.JwtUtil;
import com.cshlands.utils.Md5Util;
import com.cshlands.utils.ThreadLocalUtil;
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
    public User findById(Integer id) {
        return userMapper.findById(id);
    }

    @Override
    public UserVO getUserInfo(String username) {
        User user = userMapper.findByUserName(username);
        if (user == null) {
            throw BusinessException.notFound("用户未找到");
        }
        return toVO(user);
    }

    @Override
    public UserVO updateUserInfo(UpdateUserDTO updatedUserDTO) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer currentUserId = (Integer) claims.get("id");

        User user = findById(currentUserId);
        checkUserExist(user);
        user.setNickname(updatedUserDTO.getNickname());
        user.setEmail(updatedUserDTO.getEmail());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateUserInfo(user);
        return toVO(user);
    }

    @Override
    public void updateUserAvatar(String avatar){
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer id = (Integer) claims.get("id");
        User user = userMapper.findById(id);
        checkUserExist(user);
        user.setUserPic(avatar);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateUserAvatar(user);
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

    private void checkUserExist(User user) {
        if (user == null) {
            throw BusinessException.notFound("用户未找到");
        }
    }
}
