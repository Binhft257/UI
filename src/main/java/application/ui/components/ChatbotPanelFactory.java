package application.ui.components;

import application.model.ChatMessage;
import application.service.ChatbotService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.util.concurrent.CompletableFuture;

public class ChatbotPanelFactory {

    public static VBox createChatbotPanel() {
        VBox chatPanel = new VBox();
        chatPanel.getStyleClass().add("chatbot-panel");

        // Header
        HBox header = createChatHeader();

        // Messages area
        ScrollPane messagesScrollPane = createMessagesArea();
        VBox.setVgrow(messagesScrollPane, Priority.ALWAYS);

        // Input area
        HBox inputArea = createInputArea(messagesScrollPane);

        chatPanel.getChildren().addAll(header, messagesScrollPane, inputArea);

        // Thêm tin nhắn chào mừng
        addWelcomeMessage(messagesScrollPane);

        return chatPanel;
    }

    private static HBox createChatHeader() {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10));
        header.getStyleClass().add("chatbot-header");

        Label avatar = new Label("🤖");
        avatar.getStyleClass().add("chatbot-avatar");

        VBox info = new VBox(2);
        Label title = new Label("CHATBOT của Seventeen's Store");
        title.getStyleClass().add("chatbot-title");

        Label subtitle = new Label("Người đồng hành shopping cùng của bạn");
        subtitle.getStyleClass().add("chatbot-subtitle");

        info.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(avatar, info, spacer);

        return header;
    }

    private static ScrollPane createMessagesArea() {
        VBox messagesContainer = new VBox(5);
        messagesContainer.setPadding(new Insets(10));
        messagesContainer.getStyleClass().add("messages-container");

        ScrollPane scrollPane = new ScrollPane(messagesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("messages-scroll");

        return scrollPane;
    }

    private static HBox createInputArea(ScrollPane messagesScrollPane) {
        HBox inputArea = new HBox(10);
        inputArea.setAlignment(Pos.CENTER);
        inputArea.setPadding(new Insets(10));
        inputArea.getStyleClass().add("chat-input-area");

        TextField messageInput = new TextField();
        messageInput.setPromptText("Nhập câu hỏi của bạn...");
        messageInput.getStyleClass().add("chat-input");
        HBox.setHgrow(messageInput, Priority.ALWAYS);

        Button sendButton = new Button("▶");
        sendButton.getStyleClass().add("send-button");

        ChatbotService chatbotService = new ChatbotService();

        Runnable sendMessage = () -> {
            String userMessage = messageInput.getText().trim();
            if (!userMessage.isEmpty()) {
                VBox messagesContainer = (VBox) messagesScrollPane.getContent();

                // Thêm tin nhắn của user
                ChatMessage userChatMessage = new ChatMessage(userMessage, true);
                messagesContainer.getChildren().add(ChatBubbleFactory.createChatBubble(userChatMessage));

                // Thêm typing indicator
                HBox typingIndicator = ChatBubbleFactory.createTypingIndicator();
                messagesContainer.getChildren().add(typingIndicator);

                messageInput.clear();
                scrollToBottom(messagesScrollPane);

                // Xử lý tin nhắn bất đồng bộ
                CompletableFuture.supplyAsync(() ->
                        chatbotService.processUserMessage(userMessage)
                ).thenAccept(botResponse -> {
                    Platform.runLater(() -> {
                        // Xóa typing indicator
                        messagesContainer.getChildren().remove(typingIndicator);

                        // Thêm phản hồi của bot
                        ChatMessage botChatMessage = new ChatMessage(botResponse, false);
                        messagesContainer.getChildren().add(ChatBubbleFactory.createChatBubble(botChatMessage));

                        scrollToBottom(messagesScrollPane);
                    });
                }).exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        // Xóa typing indicator
                        messagesContainer.getChildren().remove(typingIndicator);

                        // Thêm tin nhắn lỗi
                        ChatMessage errorMessage = new ChatMessage(
                                "Xin lỗi, có lỗi xảy ra. Vui lòng thử lại sau.", false);
                        messagesContainer.getChildren().add(ChatBubbleFactory.createChatBubble(errorMessage));

                        scrollToBottom(messagesScrollPane);
                    });
                    return null;
                });
            }
        };

        sendButton.setOnAction(e -> sendMessage.run());
        messageInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendMessage.run();
            }
        });

        inputArea.getChildren().addAll(messageInput, sendButton);

        return inputArea;
    }

    private static void addWelcomeMessage(ScrollPane messagesScrollPane) {
        VBox messagesContainer = (VBox) messagesScrollPane.getContent();

        ChatMessage welcomeMessage = new ChatMessage(
                "👋 Xin chào! Tôi là CHATBOT tư vấn laptop của Seventeen's Store. " +
                        "Hãy hỏi tôi về bất kỳ sản phẩm laptop nào bạn quan tâm nhé!",
                false
        );

        messagesContainer.getChildren().add(ChatBubbleFactory.createChatBubble(welcomeMessage));
    }

    private static void scrollToBottom(ScrollPane scrollPane) {
        Platform.runLater(() -> {
            scrollPane.setVvalue(1.0);
        });
    }
}