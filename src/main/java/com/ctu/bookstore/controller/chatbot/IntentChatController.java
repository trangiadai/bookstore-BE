package com.ctu.bookstore.controller.chatbot;

import com.ctu.bookstore.service.chatbot.IntentChatService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class IntentChatController {

    private final IntentChatService chatService;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(IntentChatController.class);

    public IntentChatController(IntentChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<?> chat(
            @RequestHeader(value = "Authorization", required = false) String bearer,
            @RequestBody ChatRequest request
    ) {
        try {
            if (bearer == null || !bearer.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of(
                        "type", "error",
                        "message", "Vui lòng đăng nhập để sử dụng chatbot"
                ));
            }

            String token = bearer.replace("Bearer ", "").trim();
            String userMsg = request.message();

            log.info("User message: {}", userMsg);

            // 1️⃣ Phân tích intent
            String intentJson = chatService.analyzeIntent(userMsg);

            // Parse intent
            JsonNode intentNode;
            try {
                intentNode = mapper.readTree(intentJson);
            } catch (Exception e) {
                log.error("Failed to parse intent JSON: {}", intentJson);
                // Nếu không parse được, thử xóa markdown wrapper
                String cleaned = intentJson.replaceAll("```json\\s*|```\\s*", "").trim();
                try {
                    intentNode = mapper.readTree(cleaned);
                } catch (Exception ex) {
                    log.error("Still failed after cleaning. Fallback to unknown intent.");
                    intentNode = mapper.createObjectNode().put("intent", "unknown");
                }
            }

            String intent = intentNode.has("intent") ? intentNode.get("intent").asText() : "unknown";

            log.info("Detected intent: {} | Full JSON: {}", intent, intentNode.toString());

            // 2️⃣ Xử lý intent
            if (!intent.equals("unknown")) {
                String result = chatService.routeIntent(intentJson, token);

                if (result == null) {
                    // Intent không xử lý được → fallback
                    String reply = chatService.generateNormalChatReply(userMsg);
                    return ResponseEntity.ok(Map.of(
                            "type", "chat",
                            "answer", reply
                    ));
                }

                try {
                    JsonNode resultNode = mapper.readTree(result);
                    String type = resultNode.has("type") ? resultNode.get("type").asText() : "unknown";

                    // 3️⃣ Xử lý theo loại response
                    switch (type) {
                        case "products":
                            int count = resultNode.has("count") ? resultNode.get("count").asInt() : 0;

                            if (count == 0) {
                                return ResponseEntity.ok(Map.of(
                                        "type", "chat",
                                        "answer", "Không tìm thấy sản phẩm phù hợp. Bạn có thể thử từ khóa khác không?"
                                ));
                            }

                            // Tóm tắt bằng AI
                            String productSummary = chatService.summarizeResults(result, userMsg);
                            return ResponseEntity.ok(Map.of(
                                    "type", "products",
                                    "answer", productSummary,
                                    "products", resultNode.get("products"),
                                    "count", count
                            ));

                        case "orders":
                            int orderCount = resultNode.has("count") ? resultNode.get("count").asInt() : 0;

                            if (orderCount == 0) {
                                return ResponseEntity.ok(Map.of(
                                        "type", "chat",
                                        "answer", "Bạn chưa có đơn hàng nào."
                                ));
                            }

                            String orderSummary = chatService.summarizeResults(result, userMsg);
                            return ResponseEntity.ok(Map.of(
                                    "type", "orders",
                                    "answer", orderSummary,
                                    "orders", resultNode.get("orders"),
                                    "count", orderCount
                            ));

                        case "error":
                            String errorMsg = resultNode.has("message")
                                    ? resultNode.get("message").asText()
                                    : "Có lỗi xảy ra";
                            return ResponseEntity.ok(Map.of(
                                    "type", "chat",
                                    "answer", errorMsg
                            ));

                        default:
                            // Cart hoặc response khác
                            return ResponseEntity.ok(resultNode);
                    }

                } catch (Exception e) {
                    log.error("Parse error: ", e);
                    return ResponseEntity.ok(Map.of(
                            "type", "chat",
                            "answer", result
                    ));
                }
            }

            // 4️⃣ Unknown intent → Chat thường
            String reply = chatService.generateNormalChatReply(userMsg);
            return ResponseEntity.ok(Map.of(
                    "type", "chat",
                    "answer", reply
            ));

        } catch (Exception e) {
            log.error("Controller error: ", e);
            return ResponseEntity.status(500).body(Map.of(
                    "type", "error",
                    "message", "Lỗi hệ thống. Vui lòng thử lại."
            ));
        }
    }

    public record ChatRequest(String message) {}
}