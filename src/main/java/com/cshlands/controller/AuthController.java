package com.cshlands.controller;

import com.cshlands.pojo.Result;
import com.cshlands.service.AuthService;
import com.cshlands.service.UserService;
import com.cshlands.vo.LoginResponseVO;
import com.cshlands.vo.UserVO;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Result<UserVO>> register(
            @Pattern(regexp = "^\\S{5,16}$", message = "用户名必须是5~16位非空字符") String username,
            @Pattern(regexp = "^\\S{5,16}$", message = "密码必须是5~16位非空字符") String password) {
        // 查询用户
        UserVO userVO = authService.register(username, password);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.success(userVO));
    }

    @PostMapping("/login")
    public Result<LoginResponseVO> login(@Pattern(regexp = "^\\S{5,16}$", message = "用户名必须是5~16位非空字符") String username,
                                         String password) {
        // 查询用户
        LoginResponseVO loginResponseVO = authService.login(username, password);
        return Result.success(loginResponseVO);
    }
}
