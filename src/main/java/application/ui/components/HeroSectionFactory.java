package application.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HeroSectionFactory {
    public static VBox createHeroSection() {
        VBox heroSection = new VBox(15);
        heroSection.setAlignment(Pos.CENTER_LEFT);
        heroSection.getStyleClass().add("hero-section");

        Label heroTitle = new Label("Welcome to our computer shop");
        heroTitle.getStyleClass().add("hero-title");

        Label heroSubtitle = new Label("17th Street's best laptops!");
        heroSubtitle.getStyleClass().add("hero-subtitle");

        heroSection.getChildren().addAll(heroTitle, heroSubtitle);
        return heroSection;
    }
}