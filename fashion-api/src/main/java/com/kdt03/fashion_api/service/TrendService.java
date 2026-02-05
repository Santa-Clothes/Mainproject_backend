package com.kdt03.fashion_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class TrendService {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String CLIENT_SECRET;

    private static final String API_URL = "https://openapi.naver.com/v1/datalab/shopping/category/keywords";

    public List<Map<String, Object>> getIntegratedTrend() {

        String[] others = {
                "레트로", "로맨틱", "리조트", "매니시", "밀리터리", "섹시", "소피스트케이티드",
                "스트리트", "스포티", "아방가르드", "오리엔탈", "웨스턴", "젠더리스", "컨트리",
                "클래식", "키치", "톰보이", "펑크", "페미닌", "프레피", "히피", "힙합"
        };

        List<CompletableFuture<JsonNode>> futures = new ArrayList<>();

        //  4개씩 묶되, 모든 요청에 모던 포함
        for (int i = 0; i < others.length; i += 4) {
            int end = Math.min(i + 4, others.length);
            String[] group = Arrays.copyOfRange(others, i, end);
            futures.add(fetchFromNaver(group));
        }

        List<Map<String, Object>> finalResult = new ArrayList<>();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenAccept(v -> {
                    for (CompletableFuture<JsonNode> future : futures) {
                        try {
                            JsonNode root = future.get();

                            double modernAvg = 0.0;
                            Map<String, Double> styleAvgMap = new HashMap<>();

                            //  각 키워드 평균 계산
                            for (JsonNode result : root.get("results")) {
                                String title = result.get("title").asText();
                                double sum = 0;

                                for (JsonNode data : result.get("data")) {
                                    sum += data.get("ratio").asDouble();
                                }
                                double avg = sum / result.get("data").size();

                                if ("모던".equals(title)) {
                                    modernAvg = avg;
                                } else {
                                    styleAvgMap.put(title, avg);
                                }
                            }

                            //  모던 기준 스케일링
                            for (Map.Entry<String, Double> entry : styleAvgMap.entrySet()) {
                                double ratio = entry.getValue() / modernAvg;

                                Map<String, Object> map = new HashMap<>();
                                map.put("style", entry.getKey());
                                map.put("score", Math.round(ratio * 100) / 100.0);
                                finalResult.add(map);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).join();

        // 모던 <- 기준점
        Map<String, Object> modernMap = new HashMap<>();
        modernMap.put("style", "모던");
        modernMap.put("score", 1.0);
        finalResult.add(modernMap);

        // 점수 내림차순 정렬
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

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(12);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            Map<String, Object> body = new HashMap<>();
            body.put("startDate", startDate.format(formatter));
            body.put("endDate", endDate.format(formatter));
            body.put("timeUnit", "month");
            body.put("category", "50000000");
            body.put("ages", List.of("40"));
            body.put("gender", "f");

            List<Map<String, Object>> keywordList = new ArrayList<>();

            // 🔥 기준 키워드
            keywordList.add(Map.of(
                    "name", "모던",
                    "param", List.of("모던룩")));

            for (String k : keywords) {
                keywordList.add(Map.of(
                        "name", k,
                        "param", List.of(k + "룩")));
            }

            body.put("keyword", keywordList);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

            try {
                return new ObjectMapper().readTree(response.getBody());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}