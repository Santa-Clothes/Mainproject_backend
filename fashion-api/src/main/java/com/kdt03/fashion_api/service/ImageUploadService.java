package com.kdt03.fashion_api.service;

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
    private final MemberRepository memberRepo;

    @Value("${SUPABASE_URL}")
    private String supabaseUrl;

    @Value("${SUPABASE_KEY}")
    private String supabaseKey;

    @Transactional
    public Map<String, Object> uploadImage(MultipartFile file) throws IOException {
        String bucketName = "uploadcloth";

        // 파일명에 한글/공백이 있으면 URL이 깨지므로 안전하게 UUID와 확장자만 사용하거나 인코딩 필요
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String savedFilename = UUID.randomUUID().toString() + extension;

        // Supabase Storage Upload URL
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + savedFilename;

        try {
            // Supabase API를 통한 이미지 업로드
            WebClient.create().post()
                    .uri(uploadUrl)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("apikey", supabaseKey)
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .body(BodyInserters.fromResource(file.getResource()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            throw new IOException("Supabase 업로드 실패: " + e.getMessage());
        }

        // 업로드 성공 후 공개 URL 구성
        String publicUrl = supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + savedFilename;

        // FastAPI 연동 (선택 사항: 실패해도 전체 프로세스는 성공)
        Map<String, Object> fastApiResponse = new HashMap<>();
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", file.getResource());

            fastApiResponse = webClient.post()
                    .uri("/upload-image")
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (fastApiResponse == null)
                fastApiResponse = new HashMap<>();
        } catch (Exception e) {
            System.err.println("FastAPI Connection Skipping...: " + e.getMessage());
            fastApiResponse.put("error", "FastAPI 서버 연결 불가 (무시됨)");
            fastApiResponse.put("status", "disconnected");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true); // JSP가 기대하는 키
        result.put("imageUrl", publicUrl); // JSP가 기대하는 키
        result.put("savedPath", publicUrl); // 기존 호환성 유지
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
}
