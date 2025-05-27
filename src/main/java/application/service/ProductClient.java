package application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import application.model.Product;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ProductClient {

    // GET all
    public static List<Product> fetchProducts() {
        return fetchWithParams(Collections.emptyMap());
    }

    // GET with params
    public static List<Product> fetchWithParams(Map<String, String> params) {
        try {
            // 🔧 Build URL
            StringBuilder sb = new StringBuilder("http://localhost:8081/api/building/");
            if (params != null && !params.isEmpty()) {
                sb.append("?");
                for (Map.Entry<String, String> e : params.entrySet()) {
                    sb.append(URLEncoder.encode(e.getKey(), "UTF-8"))
                      .append("=")
                      .append(URLEncoder.encode(e.getValue(), "UTF-8"))
                      .append("&");
                }
                sb.deleteCharAt(sb.length() - 1); // remove last &
            }

            // 🌐 Open connection
            URL url = new URL(sb.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // ⏱ Set timeout to speed up or prevent freezing
            conn.setConnectTimeout(30000); // 3s timeout
            conn.setReadTimeout(30000);    // 5s read timeout

            // 📥 Read response
            try (InputStream in = conn.getInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                List<Product> all = mapper.readValue(in, new TypeReference<List<Product>>() {});

                // 📢 Print all products
                System.out.println("📦 Đã tải " + all.size() + " sản phẩm:");
                for (Product p : all) {
                    System.out.println("→ " + (p.model != null ? p.model : "No model")
                                     + " | Brand: " + p.brand
                                     + " | CPU: " + p.cpu
                                     + " | Price: " + p.price);
                }

                return all;
            }

        } catch (Exception ex) {
            System.err.println("❌ Lỗi khi tải sản phẩm:");
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
}
