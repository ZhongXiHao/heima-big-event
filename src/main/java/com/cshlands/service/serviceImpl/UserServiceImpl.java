package com.cshlands.service.serviceImpl;

import ch.qos.logback.core.util.StringUtil;
import com.cshlands.dto.UpdatePasswordDTO;
import com.cshlands.dto.UpdateUserDTO;
import com.cshlands.exception.BusinessException;
import com.cshlands.mapper.UserMapper;
import com.cshlands.pojo.User;
import com.cshlands.service.UserService;
import com.cshlands.utils.Md5Util;
import com.cshlands.utils.RedisKeyUtil;
import com.cshlands.utils.ThreadLocalUtil;
import com.cshlands.vo.UserVO;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public UserServiceImpl(UserMapper userMapper, StringRedisTemplate stringRedisTemplate) {
        this.userMapper = userMapper;
        this.stringRedisTemplate = stringRedisTemplate;
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
    public void updateUserAvatar(String avatar) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer id = (Integer) claims.get("id");
        User user = userMapper.findById(id);
        checkUserExist(user);
        user.setUserPic(avatar);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateUserAvatar(user);
    }

    @Override
    public void updateUserPassword(UpdatePasswordDTO dto) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer id = ((Number) claims.get("id")).intValue();

        User user = userMapper.findById(id);
        checkUserExist(user);

        String oldPassword = dto.getOldPassword();
        String newPassword = dto.getNewPassword();
        String confirmNewPassword = dto.getConfirmNewPassword();

        if (!newPassword.equals(confirmNewPassword)) {
            throw BusinessException.badRequest("新密码与确认密码不一致");
        }

        String md5OldPassword = Md5Util.getMD5String(oldPassword);
        if (!user.getPassword().equals(md5OldPassword)) {
            throw BusinessException.badRequest("原密码错误");
        }

        user.setPassword(Md5Util.getMD5String(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateUserPassword(user);

        // 单点登录：改密码后清除该用户在 Redis 中的 token，使其所有已登录会话立即失效
        stringRedisTemplate.delete(RedisKeyUtil.loginTokenKey(id));
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
