package Gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GuiLabelButton extends HBox{
    public GuiLabelButton(String label, String button){
        Label textLabel = new Label("Robot: Disconnected");
        textLabel.setTextFill(Color.web("#000000"));
        textLabel.setFont(new Font("Arial",13));
        this.setAlignment(Pos.CENTER_RIGHT);
        this.setSpacing(20);
        this.getChildren().addAll(textLabel ,new GuiButton("Connect", 100, 25));
    }
}
