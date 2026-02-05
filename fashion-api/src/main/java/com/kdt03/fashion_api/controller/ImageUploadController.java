package com.kdt03.fashion_api.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kdt03.fashion_api.service.ImageUploadService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/imageupload")
@RequiredArgsConstructor
public class ImageUploadController {

    private final ImageUploadService imageUploadService;
    private final String SERVER_URL = "http://10.125.121.182:8080";

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            // 서비스가 이제 Map을 돌려줍니다. 예..
            Map<String, Object> result = imageUploadService.uploadImage(file);
            return ResponseEntity.ok(result); // 을..! 이제 포스트맨에 다 뜹니다.

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("서버 오류");
        }
    }

    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("id") String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            String savedPath = imageUploadService.uploadProfileImage(file, id);

            response.put("success", true);
            response.put("fileName", savedPath);
            response.put("imageUrl", SERVER_URL + "/uploads/" + savedPath);
            response.put("message", "업로드 성공");

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "서버 오류");
            return ResponseEntity.internalServerError().body(response);
        }
    }

}
