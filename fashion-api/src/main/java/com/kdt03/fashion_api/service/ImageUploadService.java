package com.kdt03.fashion_api.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt03.fashion_api.domain.Member;
import com.kdt03.fashion_api.domain.dto.AnalysisResponseDTO;
import com.kdt03.fashion_api.domain.dto.FastApiAnalysisDTO;
import com.kdt03.fashion_api.domain.dto.SimilarProductDTO;
import com.kdt03.fashion_api.repository.MemberRepository;
import com.kdt03.fashion_api.repository.NaverProductRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageUploadService {
    private final WebClient webClient;
    private final MemberRepository memberRepo;
    private final NaverProductRepository naverProductRepo;
    private final com.kdt03.fashion_api.repository.RecommandRepository recRepo;

    @Value("${SUPABASE_URL}")
    private String supabaseUrl;

    @Value("${SUPABASE_KEY}")
    private String supabaseKey;

    @Value("${app.supabase.bucket.upload}")
    private String uploadBucket;

    @Value("${app.supabase.bucket.profile}")
    private String profileBucket;

    public ImageUploadService(WebClient.Builder webClientBuilder, MemberRepository memberRepo,
            NaverProductRepository naverProductRepo,
            com.kdt03.fashion_api.repository.RecommandRepository recRepo,
            @Value("${app.fastapi.url}") String fastApiUrl) {

        this.webClient = webClientBuilder
                .clone()
                .baseUrl(fastApiUrl)
                .build();
        this.memberRepo = memberRepo;
        this.naverProductRepo = naverProductRepo;
        this.recRepo = recRepo;
    }

    @Transactional
    public Map<String, Object> uploadImage(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String savedFilename = UUID.randomUUID().toString() + extension;

        String uploadUrl = supabaseUrl + "/storage/v1/object/" + uploadBucket + "/" + savedFilename;
        String publicUrl = supabaseUrl + "/storage/v1/object/public/" + uploadBucket + "/" + savedFilename;

        // 1. Supabase 업로드 태스크 (비동기)
        CompletableFuture<Void> supabaseTask = CompletableFuture.runAsync(() -> {
            try {
                log.info("Starting Supabase upload for {}", savedFilename);
                webClient.post()
                        .uri(uploadUrl)
                        .header("Authorization", "Bearer " + supabaseKey)
                        .header("apikey", supabaseKey)
                        .contentType(MediaType.parseMediaType(file.getContentType()))
                        .body(BodyInserters.fromResource(file.getResource()))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                log.info("Supabase upload completed for {}", savedFilename);
            } catch (Exception e) {
                log.error("Supabase upload task failed: {}", e.getMessage());
                throw new RuntimeException("Supabase 업로드 중 오류 발생", e);
            }
        });

        // 2. FastAPI 업로드 및 분석 태스크 (비동기)
        CompletableFuture<Map<String, Object>> fastApiTask = CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Starting FastAPI upload for {}", savedFilename);
                MultipartBodyBuilder builder = new MultipartBodyBuilder();
                builder.part("file", file.getResource());

                Map<String, Object> response = webClient.post()
                        .uri("/upload-image")
                        .body(BodyInserters.fromMultipartData(builder.build()))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                        })
                        .block();

                log.info("FastAPI upload completed for {}", savedFilename);
                return response != null ? response : new HashMap<>();
            } catch (Exception e) {
                log.warn("FastAPI task failed (ignoring): {}", e.getMessage());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "FastAPI 서버 연결 불가 (무시됨)");
                errorResponse.put("status", "disconnected");
                return errorResponse;
            }
        });

        // 3. 두 태스크 병렬 실행 대기
        try {
            CompletableFuture.allOf(supabaseTask, fastApiTask).join();
            Map<String, Object> fastApiResponseMap = fastApiTask.get();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("imageUrl", publicUrl);
            result.put("savedPath", publicUrl);
            result.put("fastApiResult", fastApiResponseMap);
            return result;
        } catch (Exception e) {
            log.error("Parallel execution in uploadImage failed: {}", e.getMessage());
            throw new IOException("파일 처리 중 오류 발생: " + e.getMessage(), e);
        }
    }

    @Transactional
    public String uploadProfileImage(MultipartFile file, String id) throws IOException {
        String savedFilename = id;
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + profileBucket + "/" + savedFilename;

        try {
            webClient.post()
                    .uri(uploadUrl)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("apikey", supabaseKey)
                    .header("x-upsert", "true")
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .body(BodyInserters.fromResource(file.getResource()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            String publicUrl = supabaseUrl + "/storage/v1/object/public/" + profileBucket + "/" + savedFilename;

            Member member = memberRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("없는 회원입니다."));

            member.setProfile(publicUrl);

            return publicUrl;
        } catch (Exception e) {
            log.error("Profile image upload to Supabase failed for user {}: {}", id, e.getMessage());
            throw new IOException("Supabase 업로드 실패: " + e.getMessage());
        }
    }

    public AnalysisResponseDTO uploadAndAnalyze(MultipartFile file) throws IOException {
        log.info("Processing uploadAndAnalyze for file: {}", file.getOriginalFilename());

        FastApiAnalysisDTO fastApiResponse = null;
        List<SimilarProductDTO> similarProducts = new ArrayList<>();
        List<SimilarProductDTO> internalProducts = new ArrayList<>();

        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", file.getResource()).filename(file.getOriginalFilename());

            fastApiResponse = webClient.post()
                    .uri("/vector")
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(FastApiAnalysisDTO.class)
                    .block();

            log.debug("FastAPI response: {}", fastApiResponse);

            if (fastApiResponse != null && fastApiResponse.getVector() != null
                    && !fastApiResponse.getVector().isEmpty()) {
                List<Double> embeddingList = fastApiResponse.getVector();

                String vectorString = embeddingList.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(",", "[", "]"));

                log.info("Performing vector search with vector length: {}", vectorString.length());

                // 네이버와 내부 상품 검색을 병렬로 수행하여 성능 향상
                CompletableFuture<List<SimilarProductDTO>> naverTask = CompletableFuture
                        .supplyAsync(() -> naverProductRepo.findTopSimilarProducts(vectorString).stream()
                                .map(p -> new SimilarProductDTO(
                                        p.getProductId(),
                                        p.getTitle(),
                                        p.getPrice(),
                                        p.getImageUrl(),
                                        p.getProductLink(),
                                        p.getSimilarityScore()))
                                .collect(Collectors.toList()));

                CompletableFuture<List<SimilarProductDTO>> internalTask = CompletableFuture
                        .supplyAsync(() -> recRepo.findTopSimilarInternalProducts(vectorString).stream()
                                .map(p -> new SimilarProductDTO(
                                        p.getProductId(),
                                        p.getTitle(),
                                        p.getPrice(),
                                        p.getImageUrl(),
                                        p.getProductLink(),
                                        p.getSimilarityScore()))
                                .collect(Collectors.toList()));

                CompletableFuture.allOf(naverTask, internalTask).join();
                similarProducts = naverTask.get();
                internalProducts = internalTask.get();

                log.info("Found {} similar products and {} internal products.", similarProducts.size(),
                        internalProducts.size());
            } else {
                log.warn("FastAPI response is null or missing vector data.");
            }
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            String errorBody = e.getResponseBodyAsString();
            log.error("FastAPI WebClientResponseException ({}): {}", e.getStatusCode(), errorBody);
            if (fastApiResponse == null) {
                fastApiResponse = FastApiAnalysisDTO.builder().error(errorBody).build();
            } else {
                fastApiResponse.setError(errorBody);
            }
        } catch (Exception e) {
            log.error("Error during uploadAndAnalyze: {}", e.getMessage(), e);
            if (fastApiResponse == null) {
                fastApiResponse = FastApiAnalysisDTO.builder().error(e.getMessage()).build();
            } else {
                fastApiResponse.setError(e.getMessage());
            }
        }

        // Object Mapper를 이용해 DTO 전체 구조를 Map으로 안전하게 변환
        Map<String, Object> finalAnalysisResultMap = new HashMap<>();
        if (fastApiResponse != null) {
            ObjectMapper mapper = new ObjectMapper();
            // 에러가 있는 경우 에러 담음
            if (fastApiResponse.getError() != null) {
                finalAnalysisResultMap.put("error", fastApiResponse.getError());
                finalAnalysisResultMap.put("status", fastApiResponse.getStatus());
            } else {
                finalAnalysisResultMap = mapper.convertValue(fastApiResponse, new TypeReference<Map<String, Object>>() {
                });
                // 응답에서 vector 값은 너무 길어서 제외 (필요시)
                finalAnalysisResultMap.remove("vector");
            }
        }

        AnalysisResponseDTO response = AnalysisResponseDTO.builder()
                .analysisResult(finalAnalysisResultMap)
                .naverProducts(similarProducts)
                .internalProducts(internalProducts)
                .build();

        log.info("Final response similarProducts count: {}, internalProducts count: {}",
                (response.getNaverProducts() != null ? response.getNaverProducts().size() : 0),
                (response.getInternalProducts() != null ? response.getInternalProducts().size() : 0));
        return response;
    }
}
