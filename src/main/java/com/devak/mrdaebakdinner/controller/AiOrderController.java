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
                                    "enum", List.of("SIMPLE","GRAND","DELUXE")
                            ),
                            "items", Map.of(
                                    "type", "object",
                                    "properties", ITEM_KEYS.stream().collect(HashMap::new,
                                            (m,k) -> m.put(k, Map.of(
                                                    "type", "integer", "minimum", 0
                                            )),
                                            HashMap::putAll),
                                    "additionalProperties", false
                            ),
                            "deliveryAddress", Map.of("type", "string", "maxLength", 50),
                            "cardNumber", Map.of("type", "string", "description", "카드번호(1234-1234 등)"),
                            "comment", Map.of("type", "string", "maxLength", 100)
                    ),
                    "additionalProperties", false
            );

            // ===== response_format 정의 =====
            Map<String, Object> responseFormat = Map.of(
                    "type", "json_schema",
                    "json_schema", Map.of(
                            "name", "order",
                            "schema", jsonSchema
                    )
            );

            // ===== 프롬프트 정의 =====
            String systemPrompt =
                    """
                            당신은 레스토랑 주문 파서입니다.
                            
                            규칙:
                            1. 사용자의 자연어 주문을 받아 JSON Schema에 맞춰 정확히 반환하세요.
                            2. 주문에는 기본 디너 세트 구성이 있으며, 서빙 스타일을 지정할 수 있습니다.
                            3. 기본 디너 세트 구성:
                               - VALENTINE: 와인 1, 스테이크 1
                               - FRENCH: 커피컵 1, 와인 1, 샐러드 1, 스테이크 1
                               - ENGLISH: 에그스크램블 1, 베이컨 1, 빵 1, 스테이크 1
                               - CHAMPAGNE: 샴페인 1, 바게트 4, 커피포트 1, 와인 1, 스테이크 1
                            4. 서빙 스타일: SIMPLE, GRAND, DELUXE
                               - 스타일 요청이 따로 없을 경우, 샴페인 축제 디너는 GRAND, 나머지 디너는 SIMPLE로 응답하세요.
                            5. 추가 요청이 있으면 해당 아이템 수량을 사용자가 원하는 만큼만 늘리세요.
                               만약 수량이 정해지지 않은 경우 1개만 늘리고, 아이템이 정해지지 않은 경우 변경하지 마세요.
                               예) "발렌타인 디너에 빵 하나 추가" → (와인 1, 스테이크 1, 빵 1)
                               예) "잉글리시 디너에 베이컨 추가" → (에그스크램블 1, 베이컨 2, 빵 1, 스테이크 1)
                            6. 삭제 요청이 있으면 해당 아이템 수량을 사용자가 원하는 만큼만 줄이세요.
                               만약 수량이 정해지지 않은 경우 해당 아이템을 모두 삭제하고, 아이템이 정해지지 않은 경우 변경하지 마세요.
                               예) "프렌치 디너에 샐러드 1개 빼줘" → (커피컵 1, 와인 1, 스테이크 1)
                               예) "샴페인 축제 디너에 바게트 빼줘" → (샴페인 1, 커피포트 1, 와인 1, 스테이크 1)
                            7. 샴페인 축제 디너는 SIMPLE 스타일과 함께 주문할 수 없습니다.
                               만약 사용자가 샴페인 축제 디너와 SIMPLE 스타일을 동시에 요청 시, comment에 안내 후 menu, style, items를 비우고 반환하세요.
                            8. "빵"과 "바게트"는 다른 아이템입니다.
                            9. 만약 사용자가 "추천"을 원하면, 스스로 사용자의 상황에 가장 적절한 menu, style, items를 선택해서 JSON Schema에 맞춰 반환하고 comment에 추천 이유를 작성하세요.
                            10. 모든 수량은 정수로, 0 이상이어야 합니다.
                            11. 이외에 입력이 부적절하거나 없는 필드의 경우, 해당 필드를 비워두고 comment에 안내 문구를 남기세요.
                            """;
            String userPrompt = "사용자 입력: " + userInput;

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

            // ===== 아이템 수량 파싱 =====
            Map<String, Integer> validatedItems = new HashMap<>();
            for (String key : ITEM_KEYS) {
                Integer val = null;
                if (dto.getItems() != null) {
                    val = dto.getItems().get(key);
                }
                // null 또는 음수면 0으로 처리
                validatedItems.put(key, (val != null && val >= 0) ? val : 0);
            }
            dto.setItems(validatedItems);


            if (dto.getMenu() == null) dto.setMenu("");
            if (dto.getStyle() == null) dto.setStyle("");
            if (dto.getItems() == null) dto.setItems(new HashMap<>());
            if (dto.getDeliveryAddress() == null) dto.setDeliveryAddress("");
            if (dto.getCardNumber() == null) dto.setCardNumber("");
            if (dto.getComment() == null) dto.setComment("");

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "AI 처리 오류" + e.getMessage()));
        }
    }
}
