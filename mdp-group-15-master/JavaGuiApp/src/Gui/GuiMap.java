package Gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.awt.*;

public class GuiMap extends VBox {
    private int[][] mapArray;
    private int[][] backup;
    private Canvas canvas;
    private GraphicsContext gc;
    private boolean editable = true;

    //robot
    private boolean sim = true;

    public GuiMap(){
        this(375, 500);
    }

    public GuiMap(int width, int height){
        mapArray  = new int[20][15];
        backup = new int[20][15];

        for(int r = 0; r < 20; r++)
            for(int c = 0; c < 15; c++)
                mapArray[r][c] = 0;

        mapArray[0][12] = 1;
        mapArray[0][13] = 1;
        mapArray[0][14] = 1;
        mapArray[1][12] = 1;
        mapArray[1][13] = 1;
        mapArray[1][14] = 1;
        mapArray[2][12] = 1;
        mapArray[2][13] = 1;
        mapArray[2][14] = 1;

        mapArray[17][0] = 2;
        mapArray[17][1] = 2;
        mapArray[17][2] = 2;
        mapArray[18][0] = 2;
        mapArray[18][1] = 2;
        mapArray[18][2] = 2;
        mapArray[19][0] = 2;
        mapArray[19][1] = 2;
        mapArray[19][2] = 2;

        for(int i=0; i<mapArray.length; i++)
            for(int j=0; j<mapArray[i].length; j++)
                backup[i][j] = mapArray[i][j];

        Paint black = Color.web("#000000");

        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        gc.setStroke(black);

        drawMap();
        initRobot();

        canvas.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> mouseClicked(e));

        String legends[] = {"Start", "Goal", "Obstacle", "Explored", "Unexplored"};
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        for(int i = 0; i < legends.length; i++){
            HBox hbox = new HBox();
            Paint color = getColor(legends[i]);
            Rectangle rect = new Rectangle(20, 20, color);
            Label label = new Label(" - " + legends[i]);
            label.setTextFill(black);
            label.setFont(new Font("Arial Black",12));
            hbox.getChildren().addAll(rect, label);

            int x = (int)Math.round(i/3);
            grid.add(hbox, i%3, x);
        }
        this.getChildren().addAll(canvas, grid);

        //Set Shadow
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(2.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        canvas.setEffect(dropShadow);

        this.setSpacing(15.);
    }

    private Color getColor(String name){
        switch(name){
            case "Start":
                return Color.web("#8BC34A");
            case "Goal":
                return Color.web("#03A9F4");
            case "Explored":
                return Color.web("#bdc3c7");
            case "Unexplored":
                return Color.web("#c0392b");
            case "Obstacle":
                return Color.web("#2c3e50");
        }
        return null;
    }

    private Color getColor(int index){
        switch(index){
            case 2:
                return Color.web("#8BC34A");
            case 1:
                return Color.web("#03A9F4");
            case 3:
                return Color.web("#bdc3c7");
            case 0:
                return Color.web("#c0392b");
            case 4:
                return Color.web("#2c3e50");
            case 5:
                return Color.web("#402382");
        }
        return null;
    }

    public int[][] getMapArray(){
        return mapArray;
    }

    public void loadMap(int[][] map){
        mapArray = map;

        for(int i=0; i<mapArray.length; i++)
            for(int j=0; j<mapArray[i].length; j++)
                backup[i][j] = mapArray[i][j];

        drawMap();
    }

    public void drawMap(){
        for(int r = 0; r < 20; r++){
            for(int c = 0; c < 15; c++){
                gc.setLineWidth(1.0);
                gc.setFill(getColor(mapArray[r][c]));
                gc.fillRect(c*25, r*25,25, 25);
                gc.strokeRect(c*25, r*25,25, 25);
            }
        }
    }

    public void setEdit(Boolean edit){
        this.editable  = edit;
    }

    public void mouseClicked(MouseEvent e){
        if(!editable)
            return;

        double x = e.getX();
        double y = e.getY();
        int intx = (int) Math.floor(x / 25);
        int inty = (int) Math.floor(y / 25);

        if (mapArray[inty][intx] == 1 || mapArray[inty][intx] == 2)
            return;

        if (mapArray[inty][intx] == 4){
            mapArray[inty][intx] = 0;
        } else {
            mapArray[inty][intx] = 4;
        }
        drawMap();
        initRobot();
    }

    public void clearMap(){
        for(int i=0; i<backup.length; i++)
            for(int j=0; j<backup[i].length; j++)
                mapArray[i][j] = backup[i][j];
        drawMap();
    }

    public void initRobot() {
        robot = new Robot(sim, false, 1, 1, MapDirections.RIGHT);
        Paint color = getColor(5);
        gc.setFill(color);
        gc.fillOval(3, 428,69, 69);
        gc.strokeOval(3, 428,69, 69);

        Paint color2 = getColor(3);
        gc.setFill(color2);
        gc.fillOval(28, 430,15, 15);
    }

    // Set Robot Location and Rotate
    private boolean setRobotLocation(int row, int col) {
        if (map.checkValidMove(row, col)) {
            Point point = new Point(col, row);
            startPos.setLocation(col, row);
            startPosTxt.setText(String.format("(%d, %d)", col, row));
            robot.setStartPos(row, col, exploredMap);
            exploredMap.setAllExplored(true);
            System.out.println("Robot moved to new position at row: " + row + " col:" + col);
            return true;
        }
        return false;
    }
}