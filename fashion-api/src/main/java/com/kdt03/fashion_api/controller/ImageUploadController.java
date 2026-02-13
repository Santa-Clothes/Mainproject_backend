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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "이미지 업로드 (Image Upload)", description = "상품 및 프로필 이미지 업로드 관련 API")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/imageupload")
@RequiredArgsConstructor
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    @Operation(summary = "상품 이미지 업로드", description = "상품 이미지를 Supabase 버킷에 업로드하고 FastAPI 추천 서버와 연동하여 결과를 반환합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업로드 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"success\": true, \"imageUrl\": \"http://...\", \"vector\": [...]}")))
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            @Parameter(description = "업로드할 이미지 파일", required = true) @RequestParam("file") MultipartFile file) {
        try {

            Map<String, Object> result = imageUploadService.uploadImage(file);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류: " + e.getLocalizedMessage());
        }
    }

    @Operation(summary = "프로필 이미지 업로드", description = "회원의 프로필 이미지를 Supabase 버킷에 업로드하고 회원 정보를 업데이트합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업로드 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"success\": true, \"imageUrl\": \"http://...\", \"message\": \"업로드 성공\"}")))
    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfile(
            @Parameter(description = "업로드할 프로필 이미지 파일", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "이미지를 업데이트할 회원의 ID", required = true) @RequestParam("id") String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            String imageUrl = imageUploadService.uploadProfileImage(file, id);

            response.put("success", true);
            response.put("imageUrl", imageUrl);
            response.put("message", "업로드 성공");

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
