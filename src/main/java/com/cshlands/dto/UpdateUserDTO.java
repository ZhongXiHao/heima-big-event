package com.cshlands.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateUserDTO {
    @NotEmpty
    @Pattern(regexp = "^\\S{1,10}", message = "昵称长度为1-10个字符，且不能包含空格")
    private String nickname;

    @Email
    private String email;
    private String avatar;

}
