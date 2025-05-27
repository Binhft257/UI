package application.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class NavBarFactory {
    public static HBox createNavBar() {
        HBox navBar = new HBox(20);
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.getStyleClass().add("nav-bar");

        Label logo = new Label("Seventeen's Store");
        logo.getStyleClass().add("nav-logo");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button homeButton = new Button("Home");
        Button searchButton = new Button("Search");

        for (Button btn : new Button[]{homeButton, searchButton}) {
            btn.getStyleClass().add("nav-button");
        }

        navBar.getChildren().addAll(logo, spacer, homeButton, searchButton);
        return navBar;
    }
}