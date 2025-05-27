package application.ui.components;

import application.model.ChatMessage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChatBubbleFactory {

    public static HBox createChatBubble(ChatMessage message) {
        HBox bubbleContainer = new HBox();
        bubbleContainer.setPadding(new Insets(5));

        VBox bubble = new VBox(2);
        bubble.setPadding(new Insets(8, 12, 8, 12));
        bubble.setMaxWidth(250);

        Label messageLabel = new Label(message.getContent());
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("chat-message-text");

        Label timeLabel = new Label(message.getFormattedTime());
        timeLabel.getStyleClass().add("chat-time");

        bubble.getChildren().addAll(messageLabel, timeLabel);

        if (message.isUser()) {
            // Tin nhắn của user - bên phải, màu xanh
            bubble.getStyleClass().add("user-bubble");
            bubbleContainer.setAlignment(Pos.CENTER_RIGHT);
            bubbleContainer.getChildren().add(bubble);
        } else {
            // Tin nhắn của bot - bên trái, màu xám
            bubble.getStyleClass().add("bot-bubble");
            bubbleContainer.setAlignment(Pos.CENTER_LEFT);
            bubbleContainer.getChildren().add(bubble);
        }

        return bubbleContainer;
    }

    public static HBox createTypingIndicator() {
        HBox bubbleContainer = new HBox();
        bubbleContainer.setPadding(new Insets(5));
        bubbleContainer.setAlignment(Pos.CENTER_LEFT);

        VBox bubble = new VBox();
        bubble.setPadding(new Insets(8, 12, 8, 12));
        bubble.getStyleClass().add("bot-bubble");

        Label typingLabel = new Label("Đang trả lời...");
        typingLabel.getStyleClass().addAll("chat-message-text", "typing-indicator");

        bubble.getChildren().add(typingLabel);
        bubbleContainer.getChildren().add(bubble);

        return bubbleContainer;
    }
}