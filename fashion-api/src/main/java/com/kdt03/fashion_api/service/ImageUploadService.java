package com.kdt03.fashion_api.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageUploadService {

    public String uploadImage(MultipartFile file) throws IOException{

        String originalFilename = file.getOriginalFilename();
        String savedFilename = UUID.randomUUID() + "_" + originalFilename;

        String uploadDir = "C:/workspace_fashion/uploads/";
        File dest = new File(uploadDir + savedFilename);

        if (!dest.getParentFile().exists())
            dest.getParentFile().mkdirs();

        file.transferTo(dest);

        return savedFilename;
    }
}
