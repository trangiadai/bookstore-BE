package com.ctu.bookstore.service.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class IntentChatService {

    @Value("${gemini.api.key}")
    private String apiKey;

    // ✅ Đổi sang gemini-2.0-flash để tránh vượt quota
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";
    private static final String BASE_API_URL = "http://localhost:8080/bookstore";

    private final RestTemplate rest;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(IntentChatService.class);

    public IntentChatService(RestTemplate rest) {
        this.rest = rest;
    }

    // 1️⃣ AI phân tích intent
    public String analyzeIntent(String userMessage) {
        try {
            String prompt = buildPrompt(userMessage);

            ObjectNode body = mapper.createObjectNode();
            ArrayNode contents = body.putArray("contents");
            ObjectNode part = contents.addObject();
            part.putArray("parts").addObject().put("text", prompt);

            ObjectNode generationConfig = body.putObject("generationConfig");
            generationConfig.put("response_mime_type", "application/json");

            String responseBody = callGeminiApi(body);
            log.info("=== GEMINI INTENT RESPONSE ===\n{}\n===========================", responseBody);

            String intentText = extractGeminiText(responseBody);
            log.info("=== EXTRACTED INTENT JSON ===\n{}\n===========================", intentText);

            return intentText;

        } catch (Exception e) {
            log.error("Lỗi phân tích intent: ", e);
            return "{\"intent\":\"unknown\"}";
        }
    }

    // 2️⃣ Router xử lý intent
    public String routeIntent(String intentJson, String token) {
        try {
            JsonNode json = mapper.readTree(intentJson);
            String intent = json.has("intent") ? json.get("intent").asText() : "unknown";

            log.info("Processing intent: {}", intent);

            switch (intent) {
                case "get_my_cart":
                    return callApi(BASE_API_URL + "/carts/my-cart", token);

                case "get_my_orders":
                    String orders = callApi(BASE_API_URL + "/orders/user", token);
                    return formatOrdersList(orders);

                case "get_best_selling":
                    int limit = json.has("limit") ? json.get("limit").asInt() : 10;
                    String bestSelling = callApi(BASE_API_URL + "/orders/best-selling-products?limit=" + limit, token);
                    return formatProductsList(bestSelling);

                case "search_by_price":
                    Double minPrice = json.has("minPrice") ? json.get("minPrice").asDouble() : 0.0;
                    Double maxPrice = json.has("maxPrice") ? json.get("maxPrice").asDouble() : 10000000.0;
                    String priceProducts = callApi(BASE_API_URL + "/products/filter-by-price?minPrice=" + minPrice
                            + "&maxPrice=" + maxPrice + "&page=1&size=50", token);
                    return formatProductsList(priceProducts);

                case "search_by_category":
                    String category = json.has("category") ? json.get("category").asText() : null;
                    if (category != null) {
                        String categoryProducts = callApi(BASE_API_URL + "/products/filter-by-category?categoryId="
                                + URLEncoder.encode(category, StandardCharsets.UTF_8) + "&page=1&size=50", token);
                        return formatProductsList(categoryProducts);
                    }
                    return "{\"type\":\"error\",\"message\":\"Không xác định được thể loại.\"}";

                case "search_by_rating":
                    Double minRating = json.has("minRating") ? json.get("minRating").asDouble() : 4.0;
                    String ratingProducts = callApi(BASE_API_URL + "/products/filter-by-min-rating?minStars=" + minRating
                            + "&page=1&size=50", token);
                    return formatProductsList(ratingProducts);

                case "search_by_name":
                    String keyword = json.has("keyword") ? json.get("keyword").asText() : null;
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        // Lấy TẤT CẢ sản phẩm và filter theo tên
                        String allProducts = callApi(BASE_API_URL + "/products?page=1&size=100", token);
                        return searchProductsByName(allProducts, keyword);
                    }
                    return "{\"type\":\"error\",\"message\":\"Vui lòng cung cấp tên sách cần tìm.\"}";

                case "get_all_products":
                    String all = callApi(BASE_API_URL + "/products?page=1&size=50", token);
                    return formatProductsList(all);

                default:
                    return null; // Để controller xử lý unknown intent
            }
        } catch (Exception e) {
            log.error("Lỗi routing intent: ", e);
            return "{\"type\":\"error\",\"message\":\"Lỗi xử lý yêu cầu.\"}";
        }
    }

    // 3️⃣ Tìm kiếm sản phẩm theo tên
    private String searchProductsByName(String allProductsJson, String keyword) {
        try {
            JsonNode root = mapper.readTree(allProductsJson);
            JsonNode result = root.get("result");

            if (result != null) {
                JsonNode content = result.get("content");
                if (content != null && content.isArray()) {
                    ArrayNode matched = mapper.createArrayNode();
                    String normalizedKeyword = keyword.toLowerCase().trim();

                    for (JsonNode product : content) {
                        String productName = product.has("productName")
                                ? product.get("productName").asText().toLowerCase()
                                : "";

                        if (productName.contains(normalizedKeyword)) {
                            matched.add(product);
                        }
                    }

                    if (matched.size() > 0) {
                        ObjectNode wrapper = mapper.createObjectNode();
                        wrapper.put("type", "products");
                        wrapper.set("products", matched);
                        wrapper.put("count", matched.size());
                        return mapper.writeValueAsString(wrapper);
                    }
                }
            }

            return "{\"type\":\"products\",\"products\":[],\"count\":0,\"message\":\"Không tìm thấy sản phẩm có tên: " + keyword + "\"}";
        } catch (Exception e) {
            log.error("Lỗi search by name: ", e);
            return "{\"type\":\"error\",\"message\":\"Lỗi tìm kiếm.\"}";
        }
    }

    // 4️⃣ Format danh sách sản phẩm
    private String formatProductsList(String apiResponse) {
        try {
            JsonNode root = mapper.readTree(apiResponse);
            JsonNode result = root.get("result");

            if (result != null) {
                // Nếu là response từ best-selling (array trực tiếp)
                if (result.isArray()) {
                    ObjectNode wrapper = mapper.createObjectNode();
                    wrapper.put("type", "products");
                    wrapper.set("products", result);
                    wrapper.put("count", result.size());
                    return mapper.writeValueAsString(wrapper);
                }

                // Nếu là response có pagination (content array)
                JsonNode content = result.get("content");
                if (content != null && content.isArray()) {
                    ObjectNode wrapper = mapper.createObjectNode();
                    wrapper.put("type", "products");
                    wrapper.set("products", content);
                    wrapper.put("count", content.size());
                    return mapper.writeValueAsString(wrapper);
                }
            }

            return "{\"type\":\"products\",\"products\":[],\"count\":0,\"message\":\"Không tìm thấy sản phẩm.\"}";
        } catch (Exception e) {
            log.error("Lỗi format products: ", e);
            return "{\"type\":\"error\",\"message\":\"Lỗi xử lý dữ liệu.\"}";
        }
    }

    // 5️⃣ Format danh sách đơn hàng
    private String formatOrdersList(String apiResponse) {
        try {
            JsonNode root = mapper.readTree(apiResponse);
            JsonNode resultArr = root.get("result");

            if (resultArr != null && resultArr.isArray()) {
                ObjectNode wrapper = mapper.createObjectNode();
                wrapper.put("type", "orders");
                wrapper.set("orders", resultArr);
                wrapper.put("count", resultArr.size());
                return mapper.writeValueAsString(wrapper);
            }

            return "{\"type\":\"orders\",\"orders\":[],\"count\":0,\"message\":\"Bạn chưa có đơn hàng nào.\"}";
        } catch (Exception e) {
            log.error("Lỗi format orders: ", e);
            return "{\"type\":\"error\",\"message\":\"Lỗi xử lý dữ liệu.\"}";
        }
    }

    // 6️⃣ Gọi API
    private String callApi(String url, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();

        } catch (Exception e) {
            log.error("Lỗi gọi API {}: {}", url, e.getMessage());
            return "{\"type\":\"error\",\"message\":\"Không thể kết nối hệ thống.\"}";
        }
    }

    // 7️⃣ Build prompt - CỰC KỲ ĐƠN GIẢN
    private String buildPrompt(String userMessage) {
        return """
    Phân tích câu này và trả về JSON:

    "%s"

    Chọn 1 intent phù hợp nhất:

    1. get_my_cart - nếu hỏi về "giỏ hàng"
    2. get_my_orders - nếu hỏi về "đơn hàng" hoặc "đã mua"
    3. search_by_name - nếu "tìm sách tên X" hoặc "sách có tên X"
       → trích xuất keyword từ câu hỏi
    4. search_by_price - nếu nhắc đến "giá", "rẻ", "đắt", "dưới", "từ"
       → "rẻ nhất" = minPrice:0, maxPrice:100000
       → "dưới 200k" = minPrice:0, maxPrice:200000
    5. get_best_selling - nếu hỏi "bán chạy", "hot", "bestseller"
    6. get_all_products - nếu "xem tất cả", "có sách gì"
    7. unknown - không khớp với bất kỳ intent nào

    Trả về JSON (KHÔNG thêm gì khác):
    {
      "intent": "tên_intent",
      "keyword": "từ_khóa_hoặc_null",
      "minPrice": số_hoặc_null,
      "maxPrice": số_hoặc_null
    }
    """.formatted(userMessage);
    }

    // 8️⃣ AI tóm tắt kết quả - NGẮN GỌN
    public String summarizeResults(String dataJson, String userQuestion) {
        try {
            JsonNode data = mapper.readTree(dataJson);
            String type = data.has("type") ? data.get("type").asText() : "unknown";

            ObjectNode body = mapper.createObjectNode();
            String prompt;

            if ("products".equals(type)) {
                JsonNode products = data.get("products");
                int count = data.has("count") ? data.get("count").asInt() : 0;

                if (count == 0) {
                    return "Xin lỗi, không tìm thấy sản phẩm phù hợp. Bạn có thể thử từ khóa khác không?";
                }

                prompt = """
                Khách hỏi: "%s"
                
                Tìm thấy %d sản phẩm:
                %s
                
                Hãy:
                1. Nói "Tìm thấy X sản phẩm"
                2. Liệt kê 3-5 sản phẩm đầu (tên, giá)
                3. Nếu nhiều hơn, nói "và X sản phẩm khác"
                
                Trả lời NGẮN GỌN (2-3 câu), TIẾNG VIỆT.
                """.formatted(userQuestion, count, products.toString());

            } else if ("orders".equals(type)) {
                JsonNode orders = data.get("orders");
                int count = data.has("count") ? data.get("count").asInt() : 0;

                if (count == 0) {
                    return "Bạn chưa có đơn hàng nào.";
                }

                prompt = """
                Khách hỏi: "%s"
                
                Có %d đơn hàng:
                %s
                
                Hãy tóm tắt: số lượng đơn, trạng thái, tổng tiền (nếu có).
                NGẮN GỌN (2-3 câu), TIẾNG VIỆT.
                """.formatted(userQuestion, count, orders.toString());

            } else {
                return "Đã xử lý yêu cầu của bạn.";
            }

            ArrayNode contents = body.putArray("contents");
            ObjectNode part = contents.addObject();
            part.putArray("parts").addObject().put("text", prompt);

            String responseBody = callGeminiApi(body);
            return extractGeminiText(responseBody);

        } catch (Exception e) {
            log.error("Lỗi tóm tắt: ", e);
            return "Đã tìm thấy kết quả.";
        }
    }

    // 9️⃣ Chat thường - NGẮN GỌN
    public String generateNormalChatReply(String userMessage) {
        try {
            ObjectNode body = mapper.createObjectNode();

            String prompt = """
            Bạn là chatbot CTU Bookstore.
            Trả lời NGẮN GỌN (1-2 câu), THÂN THIỆN, TIẾNG VIỆT.
            
            Bạn giúp: tìm sách, xem giỏ hàng, đơn hàng, tư vấn sách.
            
            User: %s
            """.formatted(userMessage);

            ArrayNode contents = body.putArray("contents");
            ObjectNode part = contents.addObject();
            part.putArray("parts").addObject().put("text", prompt);

            String responseBody = callGeminiApi(body);
            return extractGeminiText(responseBody);

        } catch (Exception e) {
            log.error("Lỗi chat: ", e);
            return "Xin lỗi, tôi không hiểu. Bạn có thể nói rõ hơn được không?";
        }
    }

    // Helper methods
    private String callGeminiApi(ObjectNode body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
        ResponseEntity<String> response = rest.exchange(GEMINI_URL + apiKey, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    private String extractGeminiText(String fullJson) throws Exception {
        JsonNode root = mapper.readTree(fullJson);
        JsonNode candidates = root.get("candidates");
        if (candidates != null && candidates.isArray() && !candidates.isEmpty()) {
            JsonNode content = candidates.get(0).get("content");
            if (content != null) {
                JsonNode parts = content.get("parts");
                if (parts != null && parts.isArray() && !parts.isEmpty()) {
                    return parts.get(0).get("text").asText();
                }
            }
        }
        return "";
    }
}