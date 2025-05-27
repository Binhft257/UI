package application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GeminiService {
    // Thay bằng API key thật của bạn (không để placeholder cũ nữa)
    private static final String API_KEY = "AIzaSyCOmuIjXZN--2VqFpbpiX1sKPKeL3U6fuk";

    // Endpoint không kèm ?key=
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.objectMapper = new ObjectMapper();
    }

    public String generateResponse(String userMessage, String productContext) {
        try {
            // Chỉ kiểm tra NULL / empty
            if (API_KEY == null || API_KEY.isBlank()) {
                return "⚠️ Chưa cấu hình API key cho Gemini. Vui lòng liên hệ quản trị viên.";
            }

            // Xây prompt như cũ
            String prompt = createPrompt(userMessage, productContext);

            // Tạo JSON body
            ObjectNode requestBody = objectMapper.createObjectNode();
            ObjectNode content = objectMapper.createObjectNode();
            ObjectNode part = objectMapper.createObjectNode();
            part.put("text", prompt);
            content.set("parts", objectMapper.createArrayNode().add(part));
            requestBody.set("contents", objectMapper.createArrayNode().add(content));

            // Safety settings (giữ nguyên)
            ObjectNode safety = objectMapper.createObjectNode();
            safety.put("category", "HARM_CATEGORY_HARASSMENT");
            safety.put("threshold", "BLOCK_NONE");
            requestBody.set("safetySettings", objectMapper.createArrayNode().add(safety));

            // Generation config (giữ nguyên)
            ObjectNode genConfig = objectMapper.createObjectNode();
            genConfig.put("temperature", 0.7);
            genConfig.put("topP", 0.8);
            genConfig.put("topK", 40);
            genConfig.put("maxOutputTokens", 1024);
            requestBody.set("generationConfig", genConfig);

            // Gửi request
            String raw = sendPostRequest(requestBody.toString());
            return parseGeminiResponse(raw);

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Xin lỗi, tôi không thể trả lời lúc này. Lỗi: "
                    + e.getMessage();
        }
    }

    private String createPrompt(String userMessage, String productContext) {
        return String.format(
                "Bạn là nhân viên tư vấn bán hàng laptop của Seventeen's Store. " +
                        "Hãy trả lời câu hỏi của khách hàng một cách thân thiện, chuyên nghiệp và hữu ích. " +
                        "Chỉ sử dụng thông tin từ dữ liệu sản phẩm được cung cấp dưới đây.\n\n" +
                        "QUY TẮC:\n" +
                        "- Chỉ dùng dữ liệu sản phẩm\n" +
                        "- Nếu không biết thì nói không biết\n\n" +
                        "DỮ LIỆU SẢN PHẨM:\n%s\n\n" +
                        "CÂU HỎI: %s",
                productContext, userMessage
        );
    }

    private String sendPostRequest(String requestBody) throws Exception {
        // Chỉ nối đúng một lần ?key=
        String fullUrl = GEMINI_URL + "?key=" + API_KEY;
        HttpURLConnection conn = (HttpURLConnection) new URL(fullUrl).openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15_000);
        conn.setReadTimeout(30_000);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] bytes = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(bytes);
        }

        int code = conn.getResponseCode();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                code == 200 ? conn.getInputStream() : conn.getErrorStream(),
                StandardCharsets.UTF_8
        ));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();

        if (code != 200) {
            throw new Exception("Gemini API Error (" + code + "): " + sb);
        }
        return sb.toString();
    }

    private String parseGeminiResponse(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        if (root.has("error")) {
            String msg = root.get("error").path("message").asText();
            throw new Exception("Gemini API Error: " + msg);
        }
        JsonNode cand = root.path("candidates");
        if (cand.isArray() && cand.size() > 0) {
            JsonNode first = cand.get(0);
            if ("SAFETY".equals(first.path("finishReason").asText())) {
                return "⚠️ Câu hỏi của bạn không phù hợp.";
            }
            return first.path("content").path("parts").get(0).path("text")
                    .asText().trim();
        }
        return "❓ Không thể tạo phản hồi phù hợp.";
    }

    // Test kết nối
    public boolean testConnection() {
        try {
            String r = generateResponse("Test", "Test");
            return !r.startsWith("⚠️") && !r.startsWith("❌");
        } catch (Throwable t) {
            return false;
        }
    }
}
