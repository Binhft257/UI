package application.ui.common;

import application.model.Product;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class LabelFactory {
    public static Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("product-detail");
        return label;
    }

    public static Label createLabel(String text, int size, String textColor) {
        Label label = new Label(text);
        label.getStyleClass().add("product-detail");
        return label;
    }

    public static Node[] revealLabel(Product product) {
        Label brandLabel = LabelFactory.createLabel("Brand: " + product.brand);
        Label osLabel = LabelFactory.createLabel("OS: " + product.os);
        Label cpuLabel = LabelFactory.createLabel("CPU: " + product.cpu);
        Label materialLabel = LabelFactory.createLabel("Material: " + product.material);
        Label specsLabel = LabelFactory.createLabel("Specs: " + product.specifications);
        Label displaySizeLabel = LabelFactory.createLabel("Display Size: " + product.displaySize);
        Label resolutionLabel = LabelFactory.createLabel("Resolution: " + product.displayResolution);
        Label refreshRateLabel = LabelFactory.createLabel("Refresh Rate: " + product.refreshRate);
        Label colorLabel = LabelFactory.createLabel("Color: " + product.color);
        Label ramLabel = LabelFactory.createLabel("RAM: " + product.ram);
        Label gpuLabel = LabelFactory.createLabel("GPU: " + product.gpu);
        Label storageLabel = LabelFactory.createLabel("Storage: " + product.storage);
        Label rateLabel = LabelFactory.createLabel("Rate: " + product.rate);

        return new Node[]{brandLabel, osLabel, cpuLabel, materialLabel, specsLabel, displaySizeLabel, resolutionLabel, refreshRateLabel, colorLabel, ramLabel, gpuLabel, storageLabel, rateLabel};
    }
}