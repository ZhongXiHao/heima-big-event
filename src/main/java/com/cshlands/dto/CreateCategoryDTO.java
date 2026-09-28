package com.cshlands.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateCategoryDTO {
    @NotBlank
    private String categoryName;
    @NotBlank
    private String categoryAlias;
}
