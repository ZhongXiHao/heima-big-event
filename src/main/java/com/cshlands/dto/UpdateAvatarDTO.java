package com.cshlands.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Setter
@Getter
public class UpdateAvatarDTO {
    @NotBlank(message = "头像地址不能为空")
    @URL(message = "头像地址必须是合法的URL")
    private String avatarUrl;
}
