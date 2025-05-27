package application.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    public String brand;
    public String model;
    public String os;
    public String cpu;
    public String material;
    public String specifications;
    public String displaySize;
    public String displayResolution;
    public String refreshRate;
    public String color;
    public String ram;
    public String gpu;
    public String storage;
    public String price;
    public String image_url;
    public String rate;
    public List<Review> reviews;

    // ✅ Constructor mặc định (bắt buộc để Jackson hoạt động)
    public Product() {
    }

	public Product(String brand, String model, String os, String cpu, String material, String specifications,
			String displaySize, String displayResolution, String refreshRate, String color, String ram, String gpu,
			String storage, String price, String image_url, String rate, List<Review> reviews) {
		super();
		this.brand = brand;
		this.model = model;
		this.os = os;
		this.cpu = cpu;
		this.material = material;
		this.specifications = specifications;
		this.displaySize = displaySize;
		this.displayResolution = displayResolution;
		this.refreshRate = refreshRate;
		this.color = color;
		this.ram = ram;
		this.gpu = gpu;
		this.storage = storage;
		this.price = price;
		this.image_url = image_url;
		this.rate = rate;
		this.reviews = reviews;
	}

    // ✅ Constructor đầy đủ (giữ nguyên để tạo object thủ công)

}
