package com.cshlands.controller;

import com.cshlands.dto.UpdateAvatarDTO;
import com.cshlands.dto.UpdatePasswordDTO;
import com.cshlands.dto.UpdateUserDTO;
import com.cshlands.exception.BusinessException;
import com.cshlands.pojo.Result;
import com.cshlands.pojo.User;
import com.cshlands.service.UserService;
import com.cshlands.utils.JwtUtil;
import com.cshlands.utils.ThreadLocalUtil;
import com.cshlands.vo.LoginResponseVO;
import com.cshlands.vo.UserVO;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/me")
    public Result<UserVO> me() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        String username = claims.get("username").toString();
        UserVO userVO = userService.getUserInfo(username);
        return Result.success(userVO);
    }

    @PutMapping("/me")
    public Result<UserVO> updateMe(@RequestBody @Validated UpdateUserDTO dto) {
        UserVO updatedUserVO = userService.updateUserInfo(dto);
        return Result.success(updatedUserVO);
    }

    @PutMapping("/me/avatar")
    public Result<Void> updateAvatar(@RequestBody @Validated UpdateAvatarDTO dto) {
        userService.updateUserAvatar(dto.getAvatarUrl());
        return Result.success();
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Validated UpdatePasswordDTO dto) {
        userService.updateUserPassword(dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}