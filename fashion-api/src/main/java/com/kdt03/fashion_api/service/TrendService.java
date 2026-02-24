package com.kdt03.fashion_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt03.fashion_api.domain.dto.MonthlyTrendDTO;
import com.kdt03.fashion_api.domain.dto.YearlyTrendDTO;
import com.kdt03.fashion_api.repository.SalesRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TrendService {
    private final SalesRepository salesLogRepo;

    @Value("${naver.datalab.client-id}")
    private String CLIENT_ID;
    
    @Value("${naver.datalab.client-secret}")
    private String CLIENT_SECRET;

    private static final String API_URL = "https://openapi.naver.com/v1/datalab/shopping/category/keywords";

    public YearlyTrendDTO getTrendByYear(int year) {
        List<Object[]> results = salesLogRepo.findMonthlySalesTrends(year);
        Map<Integer, Map<String, Integer>> monthlyTrends = new TreeMap<>();

        for(Object[] r : results) {
            String monthStr = (String) r[0];
            String style = (String) r[1];
            Integer quantity = ((Number) r[2]).intValue();

            int month = Integer.parseInt(monthStr.substring(5, 7));

            monthlyTrends.putIfAbsent(month, new TreeMap<>());
            monthlyTrends.get(month).put(style, quantity);
        }

        List<MonthlyTrendDTO> monthlyList = monthlyTrends.entrySet().stream()
            .map(e -> MonthlyTrendDTO.builder()
                .month(e.getKey())
                .styles(e.getValue())
                .build())
                .toList();
        
        return YearlyTrendDTO.builder()
                             .year(year)
                             .data(monthlyList)
                             .build();
    }

    public List<YearlyTrendDTO> getAllTrend() {
        List<Integer> years = salesLogRepo.findDistinctYears();
        return years.stream().map(this::getTrendByYear).toList();
    }

    public List<Map<String, Object>> getIntegratedTrend() {
        String[] styles = {
                "트레디셔널", "매니시", "에스닉", "컨템포러리",
                "내추럴", "젠더리스", "스포츠", "서브컬처", "캐주얼"
        };

        List<CompletableFuture<JsonNode>> trendRequests = new ArrayList<>();
        List<Map<String, Object>> finalResult = new ArrayList<>();

        for (int i = 0; i < styles.length; i += 4) {
            int end = Math.min(i + 4, styles.length);
            String[] group = Arrays.copyOfRange(styles, i, end);
            trendRequests.add(fetchFromNaver(group));
        }

        CompletableFuture.allOf(trendRequests.toArray(new CompletableFuture[0]))
                 .thenAccept(v -> {
                    for (CompletableFuture<JsonNode> trend : trendRequests) {
                        try {
                            JsonNode resp = trend.get();
                            double feminineSum = 0.0;
                            Map<String, Double> tempSums = new HashMap<>();

                            for (JsonNode result : resp.get("results")) {
                                String title = result.get("title").asText();
                                double sum = 0;
                                
                                for (JsonNode data : result.get("data")) {
                                    sum += data.get("ratio").asDouble();
                                }

                                if ("페미닌".equals(title)) {
                                    feminineSum = sum;
                                } else {
                                    tempSums.put(title, sum);
                                }
                            }

                            if (feminineSum > 0) {
                                for (Map.Entry<String, Double> entry : tempSums.entrySet()) {
                                    Map<String, Object> map = new HashMap<>();
                                    double relativeScore = entry.getValue() / feminineSum; // 합계 비율 계산
                                
                                    map.put("style", entry.getKey());
                                    map.put("score", Math.round(relativeScore * 1000) / 1000.0); // 소수점 3자리
                                    finalResult.add(map);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).join();

        Map<String, Object> feminineBase = new HashMap<>();
        feminineBase.put("style", "페미닌");
        feminineBase.put("score", 1.0);
        finalResult.add(feminineBase);

        // 점수 내림차순 정렬
        finalResult.sort((a, b) -> Double.compare((double) b.get("score"), (double) a.get("score")));

        double totalScore = finalResult.stream()
                            .mapToDouble(m -> (double) m.get("score"))
                            .sum();

        if (totalScore > 0) {
            for (Map<String, Object> item : finalResult) {
                double rawScore = (double) item.get("score");
                
                // 1. 순수 숫자 비중 계산
                double percentage = (rawScore / totalScore) * 100;
                double roundedPercentage = Math.round(percentage * 100) / 100.0;
                
                // 2. 숫자 데이터 (그래프 그리기용)
                item.put("value", roundedPercentage); 
                
                // 3. 퍼센트 기호가 붙은 문자열 (툴팁이나 텍스트 표시용)
                item.put("percentStr", roundedPercentage + "%");
                
                // 원본 score 제거
                item.remove("score"); 
            }
        }

        return finalResult;
    }

    private String getParamByStyle(String style) {
        return switch (style) {
            case "트레디셔널" -> "올드머니";
            case "매니시" -> "보이시";
            case "에스닉" -> "보헤미안";
            case "컨템포러리" -> "출근룩";
            case "내추럴" -> "여행룩";
            case "젠더리스" -> "남녀공용";
            case "스포츠" -> "운동복";
            case "서브컬처" -> "힙합";
            case "캐주얼" -> "옷";
            case "페미닌" -> "데이트룩";
            default -> style + "룩";
        };
    }
   
    private CompletableFuture<JsonNode> fetchFromNaver(String[] keywords) {
        return CompletableFuture.supplyAsync(() -> {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", CLIENT_ID);
            headers.set("X-Naver-Client-Secret", CLIENT_SECRET);
            headers.setContentType(MediaType.APPLICATION_JSON);

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusWeeks(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            Map<String, Object> body = new HashMap<>();
            body.put("startDate", startDate.format(formatter));
            body.put("endDate", endDate.format(formatter));
            body.put("timeUnit", "month");
            body.put("category", "50000000");
            body.put("ages", List.of("30","40","50"));
            body.put("gender", "f");

            List<Map<String, Object>> keywordList = new ArrayList<>();

            keywordList.add(Map.of(
                "name", "페미닌",
                "param", List.of(getParamByStyle("페미닌"))
            ));

            for (String k : keywords) {
                if ("페미닌".equals(k)) continue;

                keywordList.add(Map.of(
                        "name", k,
                        "param", List.of(getParamByStyle(k)) // 1:1 매칭된 대표 키워드 사용
                ));
            }

            body.put("keyword", keywordList);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

            try {
                return new ObjectMapper().readTree(response.getBody());
            } catch (Exception e) {
                throw new RuntimeException("네이버 api 응답 파싱 실패", e);
            }
        });
    }
}