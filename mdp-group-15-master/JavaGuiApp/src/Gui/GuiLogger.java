package Gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class GuiLogger extends VBox{
    private TextArea content_textarea;
    private StackPane topbar;
    private StackPane content;
    private Label topbar_label;

    private Rectangle topbar_rect;
    private Rectangle content_rect;

    public GuiLogger(){
        this(340);
    }

    public GuiLogger(int width){
        Paint blue = Color.web("#3498DB");
        Paint white = Color.web("#ECF0F1");
        Paint gray = Color.web("#888888");

        topbar = new StackPane();
        topbar_rect = new Rectangle(width, 27, blue);
        topbar_label = new Label("  Log");
        topbar_label.setTextFill(white);
        topbar_label.setMinWidth(width);
        topbar_label.setMinHeight(27);
        topbar_label.setTextAlignment(TextAlignment.LEFT);
        topbar_label.setFont(new Font("Arial Black",18));

        content = new StackPane();
        content_rect = new Rectangle(width, 83, white);
        content_textarea = new TextArea();
        content_textarea.setEditable(false);
        content_textarea.setMaxSize(width - 5,83 - 5);
        content_textarea.setBackground(new Background(new BackgroundFill(white, CornerRadii.EMPTY, Insets.EMPTY)));


        topbar.getChildren().addAll(topbar_rect, topbar_label);
        content.getChildren().addAll(content_rect, content_textarea);
        this.getChildren().addAll(topbar, content);

        //Set Shadow
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(2.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        this.setEffect(dropShadow);
    }

    public void appendText(String str){
        String my_str = "System Log: " +str + "\n";
        content_textarea.appendText(my_str);
    }
}