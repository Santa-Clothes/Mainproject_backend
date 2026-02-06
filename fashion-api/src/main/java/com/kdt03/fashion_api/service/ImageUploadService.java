package com.kdt03.fashion_api.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        String savedPath = saveFile(file, "profiles/");

        Member member = memberRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("없는 회원입니다."));

        member.setProfile("/uploads/" + savedPath);

        return savedPath;
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