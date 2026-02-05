package com.kdt03.fashion_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class TrendService {
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String CLIENT_SECRET;

    private final String API_URL = "https://openapi.naver.com/v1/datalab/shopping/category/keywords";

    public List<Map<String, Object>> getIntegratedTrend() {
        // 23개 키워드 리스트 (모던 제외 22개)
        String[] others = {
            "레트로", "로맨틱", "리조트", "매니시", "밀리터리", "섹시", "소피스트케이티드", 
            "스트리트", "스포티", "아방가르드", "오리엔탈", "웨스턴", "젠더리스", "컨트리", 
            "클래식", "키치", "톰보이", "펑크", "페미닌", "프레피", "히피", "힙합"
        };

        List<CompletableFuture<JsonNode>> futures = new ArrayList<>();
        
        // 4개씩 끊어서 6번의 요청 생성 (마지막은 2개)
        for (int i = 0; i < others.length; i += 4) {
            int end = Math.min(i + 4, others.length);
            String[] group = Arrays.copyOfRange(others, i, end);
            futures.add(fetchFromNaver(group));
        }

        // 모든 요청이 끝날 때까지 대기 및 합치기
        List<Map<String, Object>> finalResult = new ArrayList<>();
        
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenAccept(v -> {
            for (CompletableFuture<JsonNode> future : futures) {
                try {
                    JsonNode root = future.get();
                    // 여기서 '모던'의 ratio를 기준으로 나머지 데이터 scaling 로직 추가 가능
                    // 단순 합산을 위해 각 키워드별 평균 ratio 추출
                    root.get("results").forEach(result -> {
                        String title = result.get("title").asText();
                        double avgRatio = 0;
                        for (JsonNode data : result.get("data")) {
                            avgRatio += data.get("ratio").asDouble();
                        }
                        avgRatio /= result.get("data").size();
                        
                        Map<String, Object> map = new HashMap<>();
                        map.put("style", title);
                        map.put("score", Math.round(avgRatio * 100) / 100.0);
                        finalResult.add(map);
                    } );
                } catch (Exception e) { e.printStackTrace(); }
            }
        }).join();

        // 점수(score) 내림차순 정렬
        finalResult.sort((a, b) -> Double.compare((double) b.get("score"), (double) a.get("score")));
        
        return finalResult;
    }

    private CompletableFuture<JsonNode> fetchFromNaver(String[] keywords) {
        return CompletableFuture.supplyAsync(() -> {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", CLIENT_ID);
            headers.set("X-Naver-Client-Secret", CLIENT_SECRET);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Request Body 생성 (모던 포함)
            Map<String, Object> body = new HashMap<>();
            body.put("startDate", "2026-01-01");
            body.put("endDate", "2026-02-04");
            body.put("timeUnit", "week");
            body.put("category", "50000000");

           
            List<Map<String, Object>> keywordsList = new ArrayList<>();
            // 모던 추가
keywordsList.add(Map.of("name", "모던", "param", List.of("모던룩")));

// 나머지 4개
for (String k : keywords) {
    keywordsList.add(Map.of("name", k, "param", List.of(k + "룩")));
}

            body.put("keyword", keywordsList);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

            try {
                return new ObjectMapper().readTree(response.getBody());
            } catch (Exception e) { throw new RuntimeException(e); }
        });
    }
}

