package com.bjpowernode; // 使用你的包名

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
public class FileController {

    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        // 调用修改后的 fileUpload 方法
        String fileUrl = fastdfs.fileUpload(file);
        return fileUrl;
    }
}