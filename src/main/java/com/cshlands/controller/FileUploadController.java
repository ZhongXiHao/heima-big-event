package com.cshlands.controller;

import com.cshlands.pojo.Result;
import com.cshlands.utils.AliOssUtil;
import com.cshlands.vo.FileUploadVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileUploadController {
    @PostMapping
    public Result<FileUploadVO> upload(MultipartFile file) throws IOException {
        // save the file into local disk
        String fileName = UUID.randomUUID() + Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
//        file.transferTo(new File("C:\\Users\\Xixi\\Desktop\\files\\" + fileName));
        String uploaded = AliOssUtil.uploadUserPictures(fileName, file.getInputStream());
        FileUploadVO fileUploadVO = new FileUploadVO();
        fileUploadVO.setUrl(uploaded);
        return Result.success(fileUploadVO);
    }
}
