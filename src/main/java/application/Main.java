package application;

import application.model.Product;
import application.service.ProductClient;
import application.ui.components.NavBarFactory;
import application.ui.components.SearchFormFactory;
import application.ui.components.ChatbotPanelFactory;
import application.ui.layout.ProductGridFactory;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Main extends Application {
    private ScrollPane productPane;
    private VBox filterPanel;
    private VBox chatbotPanel;

    @Override
    public void start(Stage stage) {
        // --- NAV BAR & CONTROLS ---
        HBox navBar = NavBarFactory.createNavBar();

        TextField searchField = new TextField();
        searchField.setPromptText("Bạn cần tìm gì?");
        searchField.setPrefWidth(300);

        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().add("search-button");

        Button filterBtn = new Button("Filter");
        filterBtn.getStyleClass().add("filter-button");

        Button chatBtn = new Button("💬 Chat");
        chatBtn.getStyleClass().add("chat-button");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(10, navBar, spacer, searchField, searchBtn, filterBtn, chatBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        // --- PRODUCT PANEL ---
        productPane = new ScrollPane();
        productPane.setFitToWidth(true);

        // --- FILTER PANEL (initially hidden) ---
        filterPanel = new VBox(10);
        filterPanel.getStyleClass().add("filter-panel");
        filterPanel.setVisible(false);
        filterPanel.setManaged(false);

        // --- CHATBOT PANEL (initially hidden) ---
        chatbotPanel = ChatbotPanelFactory.createChatbotPanel();
        chatbotPanel.setVisible(false);
        chatbotPanel.setManaged(false);

        // --- MAIN LAYOUT ---
        HBox mainBox = new HBox(10, filterPanel, productPane, chatbotPanel);
        HBox.setHgrow(productPane, Priority.ALWAYS);

        // --- SEARCH FORM: callback expects List<Product> ---
        GridPane form = SearchFormFactory.create(this::loadProductsToPane);
        filterPanel.getChildren().setAll(form);

        // --- simple search by model field ---
        searchBtn.setOnAction(e ->
            doSearch(Map.of("model", searchField.getText().trim()))
        );

        // --- first load: random 50 products ---
        doSearch(Map.of());

        // --- toggle filter panel ---
        filterBtn.setOnAction(e -> {
            boolean show = !filterPanel.isVisible();
            filterPanel.setVisible(show);
            filterPanel.setManaged(show);
        });

        // --- toggle chatbot panel ---
        chatBtn.setOnAction(e -> {
            boolean show = !chatbotPanel.isVisible();
            chatbotPanel.setVisible(show);
            chatbotPanel.setManaged(show);
        });

        // --- SCENE & STAGE ---
        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(mainBox);

        Scene scene = new Scene(root, 1400, 900);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Seventeen's Store");
        stage.setMaximized(true);
        stage.show();
    }

    /**
     * Thực thi tìm kiếm với params (có thể rỗng),
     * hoặc random 50 sản phẩm nếu params trống.
     */
    private void doSearch(Map<String, String> params) {
        List<Product> hits = ProductClient.fetchWithParams(params);
        if (params == null || params.isEmpty()) {
            Collections.shuffle(hits);
            if (hits.size() > 50) {
                hits = hits.subList(0, 50);
            }
        }
        loadProductsToPane(hits);
    }

    /**
     * Đổ danh sách products vào ProductGrid và gán lên ScrollPane.
     */
    private void loadProductsToPane(List<Product> hits) {
        productPane.setContent(ProductGridFactory.createProductGrid(hits));
    }

    public static void main(String[] args) {
        launch();
    }
}
