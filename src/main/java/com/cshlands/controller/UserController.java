package com.cshlands.controller;

import com.cshlands.exception.BusinessException;
import com.cshlands.pojo.Result;
import com.cshlands.pojo.User;
import com.cshlands.service.UserService;
import com.cshlands.vo.LoginResponseVO;
import com.cshlands.vo.UserVO;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/auth")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Result<UserVO>> register(
            @Pattern(regexp = "^\\S{5,16}$", message = "用户名必须是5~16位非空字符") String username,
            @Pattern(regexp = "^\\S{5,16}$", message = "密码必须是5~16位非空字符") String password) {
        // 查询用户
        UserVO userVO = userService.register(username, password);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.success(userVO));
    }

    @PostMapping("/login")
    public Result<LoginResponseVO> login(@Pattern(regexp = "^\\S{5,16}$", message = "用户名必须是5~16位非空字符") String username,
                                         String password) {
        // 查询用户
        LoginResponseVO loginResponseVO = userService.login(username, password);
        return Result.success(loginResponseVO);
    }
}