package application.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * NavBarFactory cung cấp thanh điều hướng với logo và nút Home.
 * Nút Home khi được nhấn sẽ gọi lại callback onHome để hiển thị lại 50 sản phẩm ban đầu.
 */
public class NavBarFactory {
    public static HBox createNavBar(Runnable onHome) {
        HBox navBar = new HBox(20);
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.getStyleClass().add("nav-bar");

        Label logo = new Label("Seventeen's Store");
        logo.getStyleClass().add("nav-logo");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button homeButton = new Button("Home");
        homeButton.getStyleClass().add("nav-button");
        homeButton.setOnAction(e -> onHome.run());

        navBar.getChildren().addAll(logo, spacer, homeButton);
        return navBar;
    }
}
