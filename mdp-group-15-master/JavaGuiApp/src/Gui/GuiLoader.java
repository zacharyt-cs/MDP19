package Gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

public class GuiLoader extends HBox {
    private GuiButton load;
    private GuiButton save;
    private GuiButton clear;
    private TextField text;
    public GuiLoader(){
        this(430, 25);
    }
    public GuiLoader(int width, int height){
        Paint gray = Color.web("#888888");

        VBox vbox = new VBox();
        vbox.setSpacing(10.);
        Label label = new Label("Map :");
        label.setTextFill(gray);
        label.setFont(new Font("Arial Black",18));
        label.setMinHeight(height);
        label.setMaxHeight(height);


        HBox button_row = new HBox();
        button_row.setSpacing(10.);

        load = new GuiButton("Load", 100, 30);
        save = new GuiButton("Save", 100, 30);
        clear = new GuiButton("Reset", 100,30);

        load.shadow(false);
        save.shadow(false);
        clear.shadow(false);
        button_row.getChildren().addAll(load, save, clear);
        button_row.setAlignment(Pos.CENTER_RIGHT);
        text = new TextField();
        text.setDisable(true);
        text.setMaxSize(width, height);
        text.setMinSize(width, height);
        vbox.getChildren().addAll(text, button_row);
        this.setSpacing(10.);
        this.getChildren().addAll(label, vbox);
    }

    public void registerClicked(int id, Command command){
        switch(id){
            case 0 :
                load.registerClicked(command);
                return;
            case 1:
                save.registerClicked(command);
                return;
            case 2:
                clear.registerClicked(command);
                return;
        }
    }

    public void setDisplay(String name){
        text.setText(name);
    }
}
