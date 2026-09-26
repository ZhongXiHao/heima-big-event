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
@RequestMapping("/user")
public class UserController {


}