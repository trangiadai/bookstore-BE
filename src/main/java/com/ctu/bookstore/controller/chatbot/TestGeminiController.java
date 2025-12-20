package com.ctu.bookstore.controller.chatbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/test-gemini")
public class TestGeminiController {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public TestGeminiController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity<?> testGemini(@RequestBody Map<String, String> request) {
        try {
            String userMessage = request.get("message");
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

            // Simple test prompt
            String prompt = """
            Phân tích câu này: "%s"
            
            Trả về JSON này:
            {"intent": "test", "message": "hiểu rồi"}
            """.formatted(userMessage);

            ObjectNode body = mapper.createObjectNode();
            ArrayNode contents = body.putArray("contents");
            ObjectNode part = contents.addObject();
            part.putArray("parts").addObject().put("text", prompt);

            // Enable JSON mode
            ObjectNode config = body.putObject("generationConfig");
            config.put("response_mime_type", "application/json");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "geminiResponse", response.getBody()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "error", e.getMessage(),
                    "stackTrace", e.toString()
            ));
        }
    }
}