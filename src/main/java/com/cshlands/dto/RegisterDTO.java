package com.cshlands.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterDTO {
    @Pattern(regexp = "^\\S{5,16}$", message = "用户名必须是5~16位非空字符")
    private String username;
    @Pattern(regexp = "^\\S{5,16}$", message = "密码必须是5~16位非空字符")
    private String password;
}
