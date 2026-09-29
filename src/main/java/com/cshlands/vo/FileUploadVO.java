package com.cshlands.vo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Setter
@Getter
public class FileUploadVO {
    private String url;
}
