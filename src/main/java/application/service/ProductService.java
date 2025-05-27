package application.service;

import application.model.Product;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ProductService {

    /**
     * Lấy toàn bộ sản phẩm
     */
    public List<Product> getProducts() {
        return ProductClient.fetchProducts();
    }

    /**
     * Lấy sản phẩm theo tập các query-params
     */
    public List<Product> getProducts(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return getProducts();
        }
        List<Product> list = ProductClient.fetchWithParams(params);
        return list != null ? list : Collections.emptyList();
    }
}
