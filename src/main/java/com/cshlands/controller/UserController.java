package com.cshlands.controller;

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
    public Result<UserVO> updateMe(@RequestBody @Validated User user) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer currentUserId = (Integer) claims.get("id");
        user.setId(currentUserId);
        UserVO updatedUserVO = userService.updateUserInfo(user);
        return Result.success(updatedUserVO);
    }

}