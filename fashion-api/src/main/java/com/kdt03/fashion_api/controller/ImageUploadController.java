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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {

            Map<String, Object> result = imageUploadService.uploadImage(file);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류: " + e.getLocalizedMessage());
        }
    }

    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("id") String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            String imageUrl = imageUploadService.uploadProfileImage(file, id);

            response.put("success", true);
            response.put("imageUrl", imageUrl);
            response.put("message", "업로드 성공");

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

}
