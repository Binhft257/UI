package application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import application.model.Product;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ProductClient {
    private static final String BASE_URL = "http://localhost:8081/api/building";

    public static int fetchTotalCount(Map<String, String> params) {
        try {
            StringBuilder sb = new StringBuilder(BASE_URL.replaceAll("/$", ""))
                .append("/count");

            if (params != null && !params.isEmpty()) {
                sb.append("?");
                for (Map.Entry<String, String> e : params.entrySet()) {
                    sb.append(URLEncoder.encode(e.getKey(), "UTF-8"))
                      .append("=")
                      .append(URLEncoder.encode(e.getValue(), "UTF-8"))
                      .append("&");
                }
                sb.deleteCharAt(sb.length() - 1);
            }

            URL url = new URL(sb.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);

            try (InputStream in = conn.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line = reader.readLine();
                return Integer.parseInt(line.trim());
            }
        } catch (Exception ex) {
            System.err.println("❌ Lỗi khi lấy tổng số sản phẩm:");
            ex.printStackTrace();
            return 0;
        }
    }

    public static List<Product> fetchProducts() {
        return fetchWithParams(Collections.emptyMap());
    }

    public static List<Product> fetchWithParams(Map<String, String> params) {
        try {
            StringBuilder sb = new StringBuilder(BASE_URL);
            if (params != null && !params.isEmpty()) {
                sb.append("?");
                for (Map.Entry<String, String> e : params.entrySet()) {
                    sb.append(URLEncoder.encode(e.getKey(), "UTF-8"))
                      .append("=")
                      .append(URLEncoder.encode(e.getValue(), "UTF-8"))
                      .append("&");
                }
                sb.deleteCharAt(sb.length() - 1);
            }

            URL url = new URL(sb.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(100000);
            conn.setReadTimeout(100000);

            try (InputStream in = conn.getInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                List<Product> list = mapper.readValue(in, new TypeReference<List<Product>>() {});
                System.out.println("📦 Đã tải " + list.size() + " sản phẩm");
                return list;
            }
        } catch (Exception ex) {
            System.err.println("❌ Lỗi khi tải sản phẩm:");
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
}