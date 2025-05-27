package application.ui.layout;

import application.model.Product;
import application.ui.components.ProductCardFactory;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

import java.util.List;

public class ProductGridFactory {
    public static GridPane createProductGrid(List<Product> products) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("product-grid");

        int col = 0, row = 0;
        int maxCol = 3;

        for (Product p : products) {
            grid.add(ProductCardFactory.createProductCard(p), col, row);
            if (++col == maxCol) {
                col = 0;
                row++;
            }
        }
        return grid;
    }
}