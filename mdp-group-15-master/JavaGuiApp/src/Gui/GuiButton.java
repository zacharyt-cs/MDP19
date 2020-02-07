package Gui;

import javafx.event.Event;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;


interface Command{
    public void run(Event e);
}

public class GuiButton extends VBox{
    private Paint blue;
    private Paint dark_blue;
    private Paint white;
    private Rectangle button_rect;
    private Command command;
    private boolean hover = true;
    private boolean enabled = true;
    private boolean shadow = true;
    private String label;
    private int width;
    private int height;

    public GuiButton(String label){
        this(label, 550, 56);
    }

    public GuiButton(String label, int width, int height){
        this.blue = Color.web("#3498DB");
        this.dark_blue = Color.web("#2980B9");
        this.white = Color.web("#ECF0F1");

        this.label = label;
        this.width = width;
        this.height = height;

        this.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> mouseEnter(e));
        this.addEventFilter(MouseEvent.MOUSE_EXITED, e -> mouseExit(e));
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> mouseClicked(e));

        draw();
    }

    public void draw(){
        StackPane button;
        Label button_label;

        Paint to_use = dark_blue;
        if(enabled)
            to_use = blue;

        button = new StackPane();
        button_rect = new Rectangle(width, height, to_use);
        button_label = new Label(label);
        button_label.setTextFill(white);
        button_label.setFont(new Font("Arial Black",18));
        button.getChildren().addAll(button_rect, button_label);
        this.getChildren().clear();
        this.getChildren().add(button);

        //Set Shadow
        DropShadow dropShadow = new DropShadow();
        if(this.shadow){
            dropShadow.setRadius(5.0);
            dropShadow.setOffsetX(2.0);
            dropShadow.setOffsetY(2.0);
            dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        } else {
            dropShadow.setRadius(0);
            dropShadow.setOffsetX(0);
            dropShadow.setOffsetY(0);
            dropShadow.setColor(Color.color(0,0,0));
        }


        this.setEffect(dropShadow);
        this.setMaxSize(width, height);
        this.setMinSize(width, height);
    }

    public void shadow(boolean enabled){
        this.shadow = enabled;
        draw();
    }

    public void enableHover(boolean enabled){
        this.hover = enabled;
    }

    public void mouseEnter(MouseEvent event){
        if(this.enabled && this.hover)
            this.button_rect.setFill(this.dark_blue);
    }

    public void mouseExit(MouseEvent event){
        if(this.enabled && this.hover)
            this.button_rect.setFill(this.blue);
    }

    public void mouseClicked(MouseEvent event){
        if(this.enabled && this.command != null)
            this.command.run(event);
    }

    public void registerClicked(Command command){
        this.command = command;
    }

    public void enabled(boolean enable){
        this.enabled = enable;
        draw();
    }
}