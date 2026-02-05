package com.kdt03.fashion_api.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    @Transactional
    public Map<String, Object> uploadImage(MultipartFile file) throws IOException {
        // 1. 파일을 먼저 저장합니다. 예.. (이건 잘 되고 있어요)
        String savedPath = saveFile(file, "clothes/");

        // 2. [중요!] 방금 저장한 그 파일을 다시 찾아옵니다. 을..!
        // 그래야 임시 파일이 사라져도 안전하게 배달할 수 있거든요. 예..
        File savedFile = new File(BASE_DIR + savedPath);
        org.springframework.core.io.FileSystemResource resource = new org.springframework.core.io.FileSystemResource(
                savedFile);

        Map<String, Object> fastApiResponse = new HashMap<>();

        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            // 예.. 임시 파일(file) 대신 진짜 저장된 파일(resource)을 담습니다. 을..!
            builder.part("file", resource);

            fastApiResponse = webClient.post()
                    .uri("/upload-image")
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            // 예.. 에러가 나면 여기서 잡히겠죠.
            fastApiResponse.put("error", "FastAPI 전달 중 사고 발생: " + e.getMessage());
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