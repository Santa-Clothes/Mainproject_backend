package com.kdt03.fashion_api.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.kdt03.fashion_api.domain.Member;
import com.kdt03.fashion_api.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageUploadService {
    private final WebClient webClient = WebClient.builder().baseUrl("http://127.0.0.1:8000").build();
    private final String BASE_DIR = "C:/uploads/";
    private final MemberRepository memberRepo;

    @Value("${SUPABASE_URL}")
    private String supabaseUrl;

    @Value("${SUPABASE_KEY}")
    private String supabaseKey;

    @Transactional
    public Map<String, Object> uploadImage(MultipartFile file) throws IOException {
        String savedPath = saveFile(file, "clothes/");
        File savedFile = new File(BASE_DIR + savedPath);
        org.springframework.core.io.FileSystemResource resource = new org.springframework.core.io.FileSystemResource(
                savedFile);

        Map<String, Object> fastApiResponse = new HashMap<>();
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", resource);
            fastApiResponse = webClient.post()
                    .uri("/upload-image")
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            fastApiResponse.put("error", "FastAPI 전달 중 문제 발생: " + e.getMessage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("savedPath", savedPath);
        result.put("fastApiResult", fastApiResponse);
        return result;
    }

    @Transactional
    public String uploadProfileImage(MultipartFile file, String id) throws IOException {
        String bucketName = "profileimage";

        // 확장자를 제외하고 ID만 파일명으로 사용 (덮어쓰기 유도)
        String savedFilename = id;

        // Supabase Storage Upload URL
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + savedFilename;

        try {
            // Supabase API를 통한 이미지 업로드
            WebClient.create().post()
                    .uri(uploadUrl)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("apikey", supabaseKey)
                    .header("x-upsert", "true") // 동일 파일명일 경우 덮어쓰기 허용
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .body(BodyInserters.fromResource(file.getResource()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 업로드 성공 후 공개 URL 구성
            String publicUrl = supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + savedFilename;

            Member member = memberRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("없는 회원입니다."));

            member.setProfile(publicUrl);

            return publicUrl;
        } catch (Exception e) {
            throw new IOException("Supabase 업로드 실패: " + e.getMessage());
        }
    }

    private String saveFile(MultipartFile file, String subDir) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String savedFilename = UUID.randomUUID() + "_" + originalFilename;
        File dest = new File(BASE_DIR + subDir + savedFilename);

        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        return subDir + savedFilename;
    }
}