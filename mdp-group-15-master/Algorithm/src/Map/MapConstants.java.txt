package Map;

import javafx.scene.paint.Color;

import java.awt.*;

public class MapConstants {
    public static final short GRID_SIZE = 10;
    public static final short MAP_LENGTH = 20;
    public static final short MAP_WIDTH = 15;
    public static final short GOALZONE_ROW = MAP_LENGTH - 2;
    public static final short GOALZONE_COL = MAP_WIDTH - 2;
    public static final short STARTZONE_ROW = 1;
    public static final short STARTZONE_COL = 1;

    public static final int MAP_CELL_SIZE = 25;
    public static final int MAP_OFFSET = 25;

    //Graphic Constants
    public static final javafx.scene.paint.Color SZ_COLOR = javafx.scene.paint.Color.GREEN;	//Start Zone Color
    public static final javafx.scene.paint.Color GZ_COLOR = javafx.scene.paint.Color.RED;	//Goal Zone Color
    public static final javafx.scene.paint.Color UE_COLOR = javafx.scene.paint.Color.BURLYWOOD;	//Unexplored Color
    public static final javafx.scene.paint.Color EX_COLOR = javafx.scene.paint.Color.WHITE;	//Explored Color
    public static final javafx.scene.paint.Color OB_COLOR = javafx.scene.paint.Color.BLACK;	//Obstacle Color
    public static final javafx.scene.paint.Color CW_COLOR = javafx.scene.paint.Color.WHITESMOKE;	//Cell Border Color
    public static final javafx.scene.paint.Color WP_COLOR = javafx.scene.paint.Color.LIGHTSKYBLUE;	// WayPoint Color
    public static final javafx.scene.paint.Color THRU_COLOR = javafx.scene.paint.Color.LIGHTBLUE;
    public static final javafx.scene.paint.Color PH_COLOR  = Color.PINK; //Path Color
}
