package com.devak.mrdaebakdinner.controller;

import com.devak.mrdaebakdinner.dto.AiOrderDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class AiOrderController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @Value("${openai.api-key}")
    private String openaiApiKey;

    // 아이템 키 목록
    private static final List<String> ITEM_KEYS = List.of(
            "wine", "steak", "coffee_cup", "coffee_pot",
            "salad", "eggscramble", "bacon", "bread",
            "baguette", "champagne"
    );

    /**
     * 자연어 주문 → OpenAI API 호출 → Structured JSON → DTO 변환
     */
    @PostMapping("/ai-parse-order")
    public ResponseEntity<?> parseOrder(@RequestBody Map<String, String> payload) {
        if (openaiApiKey == null || openaiApiKey.isBlank()) {
            return ResponseEntity.status(500).body(Map.of("error", "OpenAI API Key가 설정되지 않았습니다."));
        }

        String userInput = payload.get("text");
        String url = "https://api.openai.com/v1/chat/completions";

        try {
            // ===== JSON Schema 정의 =====
            Map<String, Object> jsonSchema = Map.of(
                    "type", "object",
                    "title", "Order",
                    "description", "레스토랑 주문 스키마",
                    "properties", Map.of(
                            "menu", Map.of(
                                    "type", "string",
                                    "enum", List.of("VALENTINE","FRENCH","ENGLISH","CHAMPAGNE")
                            ),
                            "style", Map.of(
                                    "type", "string",
                                    "enum", List.of("SIMPLE","GRAND","DELUX")
                            ),
                            "items", Map.of(
                                    "type", "object",
                                    "properties", ITEM_KEYS.stream().collect(HashMap::new,
                                            (m,k) -> m.put(k, Map.of(
                                                    "oneOf", List.of(
                                                            Map.of("type","integer","minimum",0),
                                                            Map.of("type","string","pattern","^\\d+$")
                                                    )
                                            )),
                                            HashMap::putAll),
                                    "additionalProperties", false
                            ),
                            "deliveryAddress", Map.of("type", "string", "maxLength", 50),
                            "cardNumber", Map.of("type", "string", "description", "카드번호(1234-1234 등)"),
                            "comment", Map.of("type", "string", "maxLength", 100)
                    ),
                    "required", List.of("menu", "style", "items"),
                    "additionalProperties", false
            );

            // ===== 프롬프트 정의 =====
            String systemPrompt = "당신은 레스토랑 주문 파서입니다. 한국어 자연어 주문을 받아 JSON Schema에 맞춰 정확히 반환하세요. "
                    + "입력이 부적합하면 빈 값 또는 comment에 적절히 안내 문구를 넣으세요."; // TODO: 기본세팅 반영, 경계값 처리
            String userPrompt = "사용자 입력: " + userInput;

            // ===== response_format 정의 =====
            Map<String, Object> responseFormat = Map.of(
                    "type", "json_schema",
                    "json_schema", Map.of(
                            "name", "order",
                            "schema", jsonSchema
                    )
            );

            // ===== 요청 본문 =====
            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4o-mini",
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userPrompt)
                    ),
                    "temperature", 0,
                    "response_format", responseFormat
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(openaiApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getBody() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "AI 응답이 없습니다."));
            }

            // ===== 응답 파싱 =====
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode messageContent = root.path("choices").get(0).path("message").path("content");

            AiOrderDTO dto;
            if (messageContent.isTextual()) {
                dto = objectMapper.readValue(messageContent.asText(), AiOrderDTO.class);
            } else {
                dto = objectMapper.treeToValue(messageContent, AiOrderDTO.class);
            }

            // ===== 기본값 보정 =====
            Map<String, Integer> validatedItems = new HashMap<>();
            if (dto.getItems() != null) {
                for (String key : ITEM_KEYS) {
                    try {
                        Integer val = dto.getItems().getOrDefault(key, 0);
                        validatedItems.put(key, (val != null && val >= 0) ? val : 0);
                    } catch (Exception ignore) {
                        validatedItems.put(key, 0);
                    }
                }
            }
            dto.setItems(validatedItems);

            if (dto.getDeliveryAddress() == null) dto.setDeliveryAddress("");
            if (dto.getCardNumber() == null) dto.setCardNumber("");
            if (dto.getComment() == null) dto.setComment("");

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "AI 처리 오류" + e.getMessage()));
        }
    }
}
