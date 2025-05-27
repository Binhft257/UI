package application.ui.components;

import application.model.Product;
import application.model.Review;
import application.ui.common.LabelFactory;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ProductCardFactory {
    public static VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("product-card");

        // Ảnh
        ImageView iv = new ImageView();
        String raw = product.image_url != null ? product.image_url.trim() : "";
        if (raw.startsWith("//")) raw = "https:" + raw;
        try {
            HttpURLConnection hc = (HttpURLConnection) new URL(raw).openConnection();
            hc.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                            + "(KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
            if (hc.getResponseCode() == 200) {
                try (InputStream is = hc.getInputStream()) {
                    iv.setImage(new Image(is, 270, 180, true, true));
                }
            } else {
                throw new Exception("HTTP " + hc.getResponseCode());
            }
        } catch (Exception e) {
            iv.setImage(new Image("https://via.placeholder.com/270x180.png?text=No+Image", true));
        }

        // Model + Price
        Label name = new Label(product.model);
        name.getStyleClass().add("product-name");

        String rawPrice = product.price;
        String priceText;
        if (rawPrice != null && !rawPrice.isBlank()) {
            try {
                long v = Long.parseLong(rawPrice);
                NumberFormat fmt = NumberFormat.getInstance(new Locale("vi","VN"));
                priceText = fmt.format(v) + "₫";
            } catch (Exception ex) {
                priceText = rawPrice + "₫";
            }
        } else {
            priceText = "Đang cập nhật";
        }
        Label price = new Label("Giá: " + priceText);
        price.getStyleClass().add("product-price");

        card.getChildren().addAll(iv, name, price);

        // Hover nodes
        Node[] details = LabelFactory.revealLabel(product);
        for (Node detail : details) {
            detail.getStyleClass().add("product-detail");
        }

        VBox reviewBox = null;
        if (product.reviews != null && !product.reviews.isEmpty()) {
            reviewBox = new VBox(5);
            Label ttl = new Label("Đánh giá:");
            ttl.getStyleClass().add("review-title");
            reviewBox.getChildren().add(ttl);
            for (Review r : product.reviews) {
                String txt = r.author + ": " +
                        ((r.comment == null || r.comment.isBlank())
                                ? "(Không có bình luận)"
                                : r.comment);
                Label l = new Label(txt);
                l.getStyleClass().add("review-text");
                reviewBox.getChildren().add(l);
            }
        }

        Button cartBtn = new Button("Add To Cart");
        cartBtn.getStyleClass().add("add-to-cart-button");

        List<Node> hoverNodes = new ArrayList<>(Arrays.asList(details));
        if (reviewBox != null) hoverNodes.add(reviewBox);
        hoverNodes.add(cartBtn);
        Node[] nodes = hoverNodes.toArray(new Node[0]);
        showDetail(false, nodes);

        card.setOnMouseEntered(e -> showDetail(true, nodes));
        card.setOnMouseExited(e -> showDetail(false, nodes));

        card.getChildren().addAll(nodes);
        return card;
    }

    private static void showDetail(boolean show, Node... nodes) {
        for (Node n : nodes) {
            n.setVisible(show);
            n.setManaged(show);
        }
    }
}