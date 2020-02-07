package Gui;

import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.concurrent.TransferQueue;

public class GuiGroupButton extends HBox {

    private RadioButton exploration;
    private RadioButton fastestpath;
    private RadioButton imagerec;

    public GuiGroupButton(){
        this(500, 50);
    }

    public GuiGroupButton(int width, int height){
        Paint gray = Color.web("#888888");

        //GuiButton exploration = new GuiButton("Exploration", 150, 50);
        //GuiButton fastestpath = new GuiButton("Fastest Path", 150, 50);
        //GuiButton imagerecg = new GuiButton("Image Rec", 150, 50);
        //exploration.enabled(false);

        ToggleGroup mode = new ToggleGroup();

        Label label = new Label("Mode :");
        label.setTextFill(gray);
        label.setFont(new Font("Arial Black",18));
        label.setMinHeight(height);
        label.setMaxHeight(height);

        exploration = new RadioButton();
        exploration.setText("Exploration");
        exploration.setToggleGroup(mode);
        exploration.setMinHeight(height);
        exploration.setSelected(true);

        fastestpath = new RadioButton();
        fastestpath.setText("Fastest Path");
        fastestpath.setMinHeight(height);
        fastestpath.setToggleGroup(mode);

        imagerec = new RadioButton();
        imagerec.setText("Image Recognition");
        imagerec.setMinHeight(height);
        imagerec.setToggleGroup(mode);

        this.setSpacing(20.);
        this.getChildren().addAll(label, exploration, fastestpath, imagerec);
    }

    public int getSelected(){
        if (exploration.isSelected())
            return 0;

        if (fastestpath.isSelected())
            return 1;

        if (imagerec.isSelected())
            return 2;

        return 0;
    }
}
