package Gui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.animation.Animation;

public class GuiTimer extends VBox{

    private int seconds = 0;
    private int minutes = 0;
    private Label timer;
    private Timeline timeline;
    private boolean started;

    public GuiTimer(){
        this(200);
    }

    public GuiTimer(int width){
        Paint blue = Color.web("#3498DB");
        Paint white = Color.web("#ECF0F1");
        Paint gray = Color.web("#888888");

        started = false;
        StackPane topbar;
        StackPane content;

        Label topbar_label;
        Label content_label;

        Rectangle topbar_rect;
        Rectangle content_rect;

        topbar = new StackPane();
        topbar_rect = new Rectangle(width, 27, blue);
        topbar_label = new Label("  Timer");
        topbar_label.setTextFill(white);
        topbar_label.setMinWidth(width);
        topbar_label.setMinHeight(27);
        topbar_label.setTextAlignment(TextAlignment.LEFT);
        topbar_label.setFont(new Font("Arial Black",18));

        content = new StackPane();
        content_rect = new Rectangle(width, 83, white);
        String time = String.format("%d Min %02d Sec", minutes, seconds);
        content_label = new Label(time);
        content_label.setTextFill(gray);
        content_label.setFont(new Font("Arial Black",20));
        timer = content_label;

        topbar.getChildren().addAll(topbar_rect, topbar_label);
        content.getChildren().addAll(content_rect, content_label);
        this.getChildren().addAll(topbar, content);

        //Set Shadow
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(2.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        this.setEffect(dropShadow);
    }

    public void update(){
        seconds++;
        if (seconds > 59){
            minutes++;
            seconds = 0;
        }
        String time = String.format("%d Min %02d Sec", minutes, seconds);
        this.timer.setText(time);
    }

    public void start(){
        if(started)
            return;

        started = true;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e->update()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    public void stop() {
        timeline.stop();
        started = false;
    }
}