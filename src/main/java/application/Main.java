package application;

import application.model.Product;
import application.service.ProductClient;
import application.ui.components.NavBarFactory;
import application.ui.components.ChatbotPanelFactory;
import application.ui.layout.ProductGridFactory;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application {
    private Pagination pagination;
    private Map<String, String> currentSearchParams = new HashMap<>();

    @Override
    public void start(Stage stage) {
        // NavBar với nút Home reload lại sản phẩm
        HBox navBar = NavBarFactory.createNavBar(() -> setupPagination());

        // Search đơn giản theo model
        TextField searchField = new TextField();
        searchField.setPromptText("Bạn cần tìm gì?");
        searchField.setPrefWidth(300);

        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().add("search-button");
        searchBtn.setOnAction(e -> {
            currentSearchParams.clear();
            currentSearchParams.put("model", searchField.getText().trim());
            setupPagination();
        });

        // Filter Panel (ẩn/hiện)
        VBox filterPanel = new VBox(10);
        filterPanel.getStyleClass().add("filter-panel");
        filterPanel.setVisible(false);
        filterPanel.setManaged(false);

        GridPane form = application.ui.components.SearchFormFactory.create(p -> {
            currentSearchParams.clear();
            currentSearchParams.putAll(p);
            setupPagination();
        });
        filterPanel.getChildren().setAll(form);

        Button filterBtn = new Button("Filter");
        filterBtn.getStyleClass().add("filter-button");
        filterBtn.setOnAction(e -> {
            boolean show = !filterPanel.isVisible();
            filterPanel.setVisible(show);
            filterPanel.setManaged(show);
        });

        // Chatbot Panel
        VBox chatbotPanel = ChatbotPanelFactory.createChatbotPanel();
        chatbotPanel.setVisible(false);
        chatbotPanel.setManaged(false);

        Button chatBtn = new Button("💬 Chat");
        chatBtn.getStyleClass().add("chat-button");
        chatBtn.setOnAction(e -> {
            boolean show = !chatbotPanel.isVisible();
            chatbotPanel.setVisible(show);
            chatbotPanel.setManaged(show);
        });

        // Header layout
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(10,
                navBar, spacer,
                searchField, searchBtn,
                filterBtn, chatBtn
        );
        header.setAlignment(Pos.CENTER_LEFT);

        // Pagination (setUserData trước setPageFactory để tránh NPE)
        pagination = new Pagination();
        pagination.setUserData(30); // mặc định 30 sản phẩm/trang
        pagination.setPageCount(1); // mặc định tránh lỗi
        pagination.setPageFactory(this::createPage);

        // Main layout
        HBox mainBox = new HBox(10, filterPanel, pagination, chatbotPanel);
        HBox.setHgrow(pagination, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(mainBox);

        Scene scene = new Scene(root, 1400, 900);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Seventeen's Store");
        stage.setMaximized(true);
        stage.show();

        // Lần đầu load
        setupPagination();
    }

    private void setupPagination() {
        int pageSize = 30;
        int totalCount = ProductClient.fetchTotalCount(currentSearchParams);
        int pageCount = (int) Math.ceil(totalCount / (double) pageSize);

        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
        pagination.setUserData(pageSize);
        pagination.setCurrentPageIndex(0);
    }

    private Node createPage(int pageIndex) {
        Integer pageSize = (Integer) pagination.getUserData();
        if (pageSize == null) pageSize = 30;

        Map<String, String> params = new HashMap<>(currentSearchParams);
        params.put("page", String.valueOf(pageIndex + 1));
        params.put("pageSize", String.valueOf(pageSize));

        List<Product> products = ProductClient.fetchWithParams(params);

        if (products == null || products.isEmpty()) {
            Label empty = new Label("Hiện không có sản phẩm phù hợp");
            empty.getStyleClass().add("empty-message");
            StackPane emptyPane = new StackPane(empty);
            emptyPane.setPrefHeight(600);
            return emptyPane;
        }

        // ✅ Bọc ProductGrid trong ScrollPane để có thể cuộn
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(ProductGridFactory.createProductGrid(products));
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:transparent;");
        return scroll;
    }

    public static void main(String[] args) {
        launch();
    }
}
