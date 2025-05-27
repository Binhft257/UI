package application.service;

import application.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatbotService {
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    private List<Product> productData;
    private static final String DATABASE_API_URL = "http://localhost:8081/api/building";

    public ChatbotService() {
        this.geminiService = new GeminiService();
        this.objectMapper = new ObjectMapper();
        loadProductData();
    }

    private void loadProductData() {
        try {
            // Gửi yêu cầu lấy 1000 sản phẩm đầu tiên
            Map<String, String> params = new HashMap<>();
            params.put("page", "1");
            params.put("pageSize", "1000");
            this.productData = ProductClient.fetchWithParams(params);
            System.out.println("Đã tải " + productData.size() + " sản phẩm từ database");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tải dữ liệu từ database: " + e.getMessage());
            this.productData = new ArrayList<>(); // Empty list nếu không load được
        }
    }

    public String processUserMessage(String userMessage) {
        try {
            if (productData.isEmpty()) {
                loadProductData();
            }
            String productContext = createProductContext(userMessage);
            return geminiService.generateResponse(userMessage, productContext);
        } catch (Exception e) {
            e.printStackTrace();
            return handleFallbackResponse(userMessage);
        }
    }

    private String createProductContext(String userMessage) {
        if (productData == null || productData.isEmpty()) {
            return "Hiện tại không có dữ liệu sản phẩm. Vui lòng thử lại sau.";
        }

        StringBuilder context = new StringBuilder();
        String lowerMessage = userMessage.toLowerCase();
        List<Product> relevantProducts = new ArrayList<>();

        for (Product product : productData) {
            if (isProductRelevant(product, lowerMessage)) {
                relevantProducts.add(product);
            }
        }

        if (!relevantProducts.isEmpty()) {
            context.append("Các sản phẩm liên quan:\n\n");
            int count = 0;
            for (Product product : relevantProducts) {
                if (count >= 348) break;
                context.append(formatProductInfo(product)).append("\n\n");
                count++;
            }
        } else {
            context.append("Danh sách một số sản phẩm có sẵn:\n\n");
            int count = 0;
            for (Product product : productData) {
                if (count >= 348) break;
                context.append(formatProductInfo(product)).append("\n\n");
                count++;
            }
        }

        return context.toString();
    }

    private boolean isProductRelevant(Product product, String lowerMessage) {
        if (containsIgnoreCase(product.brand, lowerMessage)) return true;
        if (containsIgnoreCase(product.model, lowerMessage)) return true;
        if (containsIgnoreCase(product.cpu, lowerMessage)) return true;
        if (containsIgnoreCase(product.gpu, lowerMessage)) return true;
        if (containsIgnoreCase(product.ram, lowerMessage)) return true;
        if (containsIgnoreCase(product.storage, lowerMessage)) return true;
        if (containsIgnoreCase(product.os, lowerMessage)) return true;

        String[] keywords = {
            "laptop", "máy tính", "gaming", "văn phòng", "học tập",
            "giá rẻ", "cao cấp", "mỏng nhẹ", "cấu hình cao",
            "intel", "amd", "nvidia", "rtx", "gtx", "ryzen", "core i",
            "dell", "hp", "asus", "acer", "lenovo", "msi", "apple", "macbook"
        };

        for (String keyword : keywords) {
            if (lowerMessage.contains(keyword)) return true;
        }

        if (lowerMessage.contains("giá") || lowerMessage.contains("price") ||
            lowerMessage.contains("triệu") || lowerMessage.contains("nghìn")) {
            return true;
        }

        return false;
    }

    private boolean containsIgnoreCase(String source, String target) {
        return source != null && source.toLowerCase().contains(target);
    }

    private String formatProductInfo(Product product) {
        StringBuilder info = new StringBuilder();

        info.append("🔹 ").append(getValueOrDefault(product.model, "Laptop")).append("\n");
        info.append("• Thương hiệu: ").append(getValueOrDefault(product.brand, "N/A")).append("\n");
        info.append("• Giá: ").append(getValueOrDefault(product.price, "Liên hệ")).append("\n");
        info.append("• CPU: ").append(getValueOrDefault(product.cpu, "N/A")).append("\n");
        info.append("• RAM: ").append(getValueOrDefault(product.ram, "N/A")).append("\n");
        info.append("• GPU: ").append(getValueOrDefault(product.gpu, "N/A")).append("\n");
        info.append("• Lưu trữ: ").append(getValueOrDefault(product.storage, "N/A")).append("\n");
        info.append("• Màn hình: ").append(getValueOrDefault(product.displaySize, "N/A")).append("\n");
        info.append("• Hệ điều hành: ").append(getValueOrDefault(product.os, "N/A")).append("\n");

        if (product.rate != null && !product.rate.trim().isEmpty()) {
            info.append("• Đánh giá: ").append(product.rate).append("/5 ⭐\n");
        }

        return info.toString();
    }

    private String getValueOrDefault(String value, String defaultValue) {
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    private String handleFallbackResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();

        if (lowerMessage.contains("xin chào") || lowerMessage.contains("hello") ||
            lowerMessage.contains("hi") || lowerMessage.contains("chào")) {
            return "👋 Xin chào! Tôi là trợ lý ảo của Seventeen's Store. " +
                    "Tôi có thể giúp bạn tìm hiểu về các sản phẩm laptop. " +
                    "Bạn muốn tìm laptop như thế nào?";
        }

        if (lowerMessage.contains("giá") || lowerMessage.contains("price") ||
            lowerMessage.contains("bao nhiêu") || lowerMessage.contains("cost")) {
            return "💰 Tôi có thể giúp bạn tìm laptop theo mức giá mong muốn. " +
                    "Bạn có thể cho tôi biết ngân sách của bạn không? " +
                    "Ví dụ: 'laptop dưới 15 triệu' hoặc 'laptop từ 20-30 triệu'";
        }

        if (lowerMessage.contains("gaming") || lowerMessage.contains("game") ||
            lowerMessage.contains("chơi game")) {
            return "🎮 Chúng tôi có nhiều laptop gaming với cấu hình mạnh mẽ! " +
                    "Bạn quan tâm đến laptop gaming của thương hiệu nào? " +
                    "MSI, ASUS ROG, Acer Predator hay Dell Alienware?";
        }

        if (lowerMessage.contains("văn phòng") || lowerMessage.contains("học tập") ||
            lowerMessage.contains("office") || lowerMessage.contains("học")) {
            return "📚 Chúng tôi có nhiều laptop phù hợp cho công việc văn phòng và học tập! " +
                    "Bạn cần laptop mỏng nhẹ, pin lâu hay cấu hình mạnh để xử lý đa tác vụ?";
        }

        if (lowerMessage.contains("thương hiệu") || lowerMessage.contains("brand") ||
            lowerMessage.contains("hãng")) {
            return "🏢 Chúng tôi có laptop từ nhiều thương hiệu uy tín: " +
                    "Dell, HP, ASUS, Acer, Lenovo, MSI, Apple MacBook. " +
                    "Bạn có thương hiệu yêu thích nào không?";
        }

        if (lowerMessage.contains("cấu hình") || lowerMessage.contains("config") ||
            lowerMessage.contains("specs")) {
            return "⚙️ Tôi có thể tư vấn cấu hình laptop phù hợp với nhu cầu của bạn. " +
                    "Bạn sử dụng laptop để làm gì chủ yếu? Gaming, văn phòng, đồ họa hay lập trình?";
        }

        return "❓ Xin lỗi, tôi chưa hiểu rõ câu hỏi của bạn. " +
                "Bạn có thể hỏi tôi về:\n" +
                "• Thông tin sản phẩm laptop\n" +
                "• Tư vấn chọn laptop theo nhu cầu\n" +
                "• So sánh cấu hình và giá cả\n" +
                "• Khuyến mãi hiện tại\n\n" +
                "Hãy thử hỏi cụ thể hơn nhé! 😊";
    }

    public void refreshProductData() {
        loadProductData();
    }

    public boolean isDataAvailable() {
        return productData != null && !productData.isEmpty();
    }

    public int getProductCount() {
        return productData != null ? productData.size() : 0;
    }
}
